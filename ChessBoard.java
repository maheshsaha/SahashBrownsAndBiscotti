import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ChessBoard {
    // Default pieces for Chess with unique values so names can be referenced 
    // in the bitboard
    public static final int PAWN = 0;
    public static final int BISHOP = 1;
    public static final int KNIGHT = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;

    public static final int WHITE = 1;
    public static final int BLACK = -1;

    // String Array containing abbreviations for the chess pieces
    public static final String[] names = new String[] {"P", "B", "H", "R", "Q", "K"};

    // Two bitboard arrays for each color, storing piece location
    public final long[] bbWhite;
    public final long[] bbBlack;
    private long bbPieces(int color, int type) {
	return (color==WHITE)? bbWhite[type]: bbBlack[type];
    }

    // index of enpassantable square if exists, -1 otherwise
    public int passant;

    private void resetPassant() {passant = -1;}

    /**
     *Default constructor 
     *Initializes the default board layout
     */    

    public ChessBoard(){
	bbWhite = new long[6];
	bbBlack = new long[6];
	resetPassant();
    }

    public void setup() {
	bbWhite[PAWN] = ((1<<8)-1)<<8;
	bbWhite[BISHOP] = (1<<2) + (1<<5);
	bbWhite[KNIGHT] = (1<<1) + (1<<6);
	bbWhite[ROOK] = (1<<0) + (1<<7);
	bbWhite[QUEEN] = (1<<4);
	bbWhite[KING] = (1<<3);
	for (int i=0; i<bbBlack.length; i++) bbBlack[i]=Long.reverse(bbWhite[i]);

    }
	
    /**
     *toString method that returns the chess board 
     *@return String Visual representation of the present chessboard. The lowercase letter preceeding the uppercase letter indicates color. K = King, Q = Queen, R = Rook, B = Bishop, N = Knight, P = Pawn
     */    
    
    public String toString(long mask) {
	String out = "  a b c d e f g h\n8";
	for (int i=63; i>=0; i--) {
	    String s = "  ";
	    for (int j=0; j<bbWhite.length; j++)
		if (((1L << i) & bbWhite[j]) != 0) s = "w" + names[j];
	    for (int j=0; j<bbBlack.length; j++)
		if (((1L << i) & bbBlack[j]) != 0) s = "b" + names[j];
	    out += "\033[" + ((((1L<<i) & mask) != 0)? 43:((i/8+i%8)%2==0? 47: 40)) + ";" + ((((1L << i) & getWhite()) != 0)? 34: 31) + "m" + s + "\033[0m";
	    if (i%8==0) out +=(i/8+1) + "\n" + (i>0? (i/8):"");
	    
	}
	return out + " a b c d e f g h";
    }
    
    public String toString() {
	return toString(0L);
    }
    //mainly for debugging, ineffecient
    public String toString(String pos) {
	try {
	    List<ChessMove> pieceMoves = pieceMoves(ChessMove.toIndex(pos));
	    long mask = moveMask(pieceMoves) | captureMask(pieceMoves);
	    return toString(mask);
	} catch (Exception e) {return toString();}
    }
	    
    
    /**
     *This method is used to get a bitboard that contains only white pieces 
     *@return long A bitboard containing present white pieces only
     */    

    public long getWhite() {
	long out = 0L;
	for (int i=0; i<6; i++) out |= bbWhite[i];
	return out;
    }

    /**
     *This method is used to get a bitboard that contains only black pieces 
     *@return long A bitboard containing present black pieces only
     */

    public long getBlack() {
	long out = 0L;
	for (int i=0; i<6; i++) out |= bbBlack[i];
	return out;
    }

    public long getByColor(int color) {
	return (color==WHITE)? getWhite(): getBlack();
    }
    
    /**
     *This method is used to get a bitboard containg all of the pieces 
     *@return long A bitboard containing all present pieces
     */

    public long getAll() {
	return getWhite() | getBlack();
    }

    public long attacking(int pos, int color) {
	long pawns, knights, kings, bishopQueens, rookQueens;
	pawns = bbPieces(color, PAWN);
	knights = bbPieces(color, KNIGHT);
	kings = bbPieces(color, KING);
	bishopQueens = rookQueens = bbPieces(color, QUEEN);
	bishopQueens |= bbPieces(color, BISHOP);
	rookQueens |= bbPieces(color, ROOK);
	return(pawns & Chess.pawnMasks[pos][2+color])
	    | (knights & Chess.knightMasks[pos])
	    | (kings & Chess.kingMasks[pos])
	    | (bishopQueens & Chess.bishopMask(getAll(), pos))
	    | (rookQueens & Chess.rookMask(getAll(), pos));
    }
	
    public long inCheckFilter(int color) {
	int pos = 0;
	long king = bbPieces(color, KING);
	while ((king>>=1)>0) pos++;
	long attacking = attacking(pos, -color);
	if (attacking==0L) return -1L;

	long filter = (attacking & bbPieces(-color, KNIGHT))|(attacking & bbPieces(-color, PAWN));
	for (Integer i: toIndices(attacking & (bbPieces(-color, QUEEN)|bbPieces(-color, ROOK)|bbPieces(-color, BISHOP)))) {
	    filter |= Chess.rayMask(pos, i);
	}
	return filter;
    }
	
	
    
        // piece number for presence of either color, -1 for blank
    public int typeAtPosition(int i) {
	for (int j=0; j<6; j++)
	    if (((1L << i) & (bbBlack[j] | bbWhite[j])) != 0) return j;
	return -1;
    }

    // 0 for empty, 1 for white, -1 for black
    public int colorAtPosition(int i) {
	if (((1L << i) & getWhite()) != 0) return WHITE;
	if (((1L << i) & getBlack()) != 0) return BLACK;
	return 0;
    }
    
    public void makeMove(ChessMove move) {
	int startType = typeAtPosition(move.start);
	int endType = typeAtPosition(move.end);
	int color = colorAtPosition(move.start);
	switch (color) {
	case WHITE:
	    bbWhite[startType] |= (1L << move.end); //add white piece at end
	    bbWhite[startType] &= -1*((1L << move.start)+1L);//remove white piece at start
	    if (endType!=-1) bbBlack[endType] &= -1 *((1L << move.end)+1L);//remove black piece at end
	    break;
	case BLACK:
	    bbBlack[startType] |= (1L << move.end);//add black piece at end
	    bbBlack[startType] &= -1*((1L << move.start)+1L);//remove black piece at start
	    if (endType!=-1) bbWhite[endType] &= -1 *((1L << move.end)+1L);//remove white piece at end
	    break;
	}


	//if a pawn just moved in front of a passantable square, kill the pawn there
	if (startType==PAWN && move.end-8*color==passant) {
	    switch (color) {
	    case WHITE:
		bbBlack[PAWN] &= -1*((1L << passant)+1L);
		break;
	    case BLACK:
		bbWhite[PAWN] &= -1*((1L << passant)+1L);
		break;
	    }
	}
	
	resetPassant();
	//if you double push a pawn, set its endpoint to be the enpassant square
	if (startType==PAWN && Math.abs(move.start/8-move.end/8)==2) {
	    passant = move.end;
	}
    }
    public void makeMove(String start, String end) {
	makeMove(new ChessMove(start, end));
    }

    //for debugging only
    public void place(int color, int type, int pos) {
	int t = typeAtPosition(pos);
	if (color==WHITE) {
	    if (t!=-1) bbBlack[t] &= -1 *((1L << pos)+1L);
	} else {
	    if (t!=-1) bbWhite[t] &= -1 *((1L << pos)+1L);
	}
	if (type==-1) return;
 	if (color==WHITE) {
	    bbWhite[type] |= (1L<<pos);
	} else {
	    bbBlack[type] |= (1L<<pos);
	}
    }
    public void place(int color, int type, String pos) {
	place(color, type, ChessMove.toIndex(pos));
    }
    //i tested this a lot, pretty sure its the fastest way....
    public static List<Integer> toIndices(long l) {
	List<Integer> ints = new LinkedList<>();
	for(int i=0; i<64; i++) {
	    if ((1 & l)==1) ints.add(i);
	    l>>=1;
	}
	return ints;
    }
	
    public static List<ChessMove> toMoves(int start, long ends, boolean capture) {
	List<ChessMove> moves = new LinkedList<>();
	for (Integer end: toIndices(ends))
	    moves.add(new ChessMove(start, end, capture));
	return moves;
    }

    public static long moveMask(List<ChessMove> moves) {
	long moveMask = 0L;
	for (ChessMove move: moves) {
	    if (!move.capture) moveMask += (1L<<move.end);
	}
	return moveMask;
    }
    public static long captureMask(List<ChessMove> moves) {
	long captureMask = 0L;
	for (ChessMove move: moves) {
	    if (move.capture) captureMask += (1L<<move.end);
	}
	return captureMask;
    }
    
    //it may seem convoluted to generate masks, turn them into lists of moves, then convert them back to masks, but its more effecient to store the list because they maintain origin and capture imformation, and regeneration to resotre that information is extremely costly
    public List<ChessMove>  pieceMoves(int pos) {
	List<ChessMove> moves = new LinkedList<>();
	int color = colorAtPosition(pos);
	if (color == 0) return moves;
	long opp = getByColor(-color);
	long all = getAll();
	long moveMask = 0L;
	long captureMask = 0L;
	switch (typeAtPosition(pos)) {
	case PAWN:
	    moveMask = Chess.pawnMasks[pos][1-color] & ~all;
	    captureMask = Chess.pawnMasks[pos][2-color] & opp;
	    captureMask |= Chess.passantMask(color, passant, pos);
	    break;
	case KNIGHT:
	    moveMask = Chess.knightMasks[pos] & ~all;
	    captureMask = Chess.knightMasks[pos] & opp;
	    break;
	case BISHOP:
	    long bishopMask = Chess.bishopMask(all, pos);
	    moveMask = bishopMask & ~all;
	    captureMask = bishopMask & opp;
	    break;
	case ROOK:
	    long rookMask = Chess.rookMask(all, pos);
	    moveMask = rookMask & ~all;
	    captureMask = rookMask & opp;
	    break;
	case QUEEN:
	    long queenMask = Chess.queenMask(all, pos);
	    moveMask = queenMask & ~all;
	    captureMask = queenMask & opp;
	    break;
	case KING:
	    moveMask = Chess.kingMasks[pos] & ~all;
	    captureMask = Chess.kingMasks[pos] & opp;
	    break;
	}
	moves.addAll(toMoves(pos, moveMask, false));
	moves.addAll(toMoves(pos, captureMask, true));
	
	return moves;
    }

    public List<ChessMove> pieceMoves(String pos) {
	return pieceMoves(ChessMove.toIndex(pos));
    }

    //returns
    public static List<ChessMove> applyMask(List<ChessMove> moves, long startMask, long endMask) {
	List<Integer> starts = toIndices(startMask);
	List<Integer> ends = toIndices(endMask);
	return  moves.stream()
	    .filter(cm -> (starts.contains(cm.start) & ends.contains(cm.end)))
	    .collect(Collectors.toList());
    }
	
    public static void main(String[] a) {
	ChessBoard b = new ChessBoard();
	b.setup();
	b.makeMove("d2", "d4");
	b.makeMove("e7", "e5");
	pr(b.toString(captureMask(b.pieceMoves("d4"))));

    }


    //debuggin prints
    static void pr(Object s) {
	System.out.println(s);
    }
    static void prl(long l) {
	pr(Long.toBinaryString(l));
    }
    static void prll(long l) {
	pr(Chess.longToString(l));
    }
}
