public class ChessBoard {
    // Default pieces for Chess with unique values so names can be referenced 
    // in the bitboard
    public static final int PAWN = 0;
    public static final int BISHOP = 1;
    public static final int KNIGHT = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;

    // String Array containing abbreviations for the chess pieces
    public static final String[] names = new String[] {"P", "B", "H", "R", "Q", "K"};

    // Two bitboard arrays for each color, storing piece location
    public final long[] bbWhite;
    public final long[] bbBlack;

    /**
     *Default constructor 
     *Initializes the default board layout
     */    

    public ChessBoard(){
	bbWhite = new long[6];
	bbWhite[PAWN] = ((1<<8)-1)<<8;
	bbWhite[BISHOP] = (1<<2) + (1<<5);
	bbWhite[KNIGHT] = (1<<1) + (1<<6);
	bbWhite[ROOK] = (1<<0) + (1<<7);
	bbWhite[QUEEN] = (1<<4);
	bbWhite[KING] = (1<<3);
	bbBlack = new long[6];
	for (int i=0; i<bbBlack.length; i++) bbBlack[i]=Long.reverse(bbWhite[i]);
    }
    
    /**
     *toString method that returns the chess board 
     *@return String Visual representation of the present chessboard. The lowercase letter preceeding the uppercase letter indicates color. K = King, Q = Queen, R = Rook, B = Bishop, N = Knight, P = Pawn
     */    
    
    public String toString() {
	String out = "";
	for (int i=63; i>=0; i--) {
	    String s = "_";
	    for (int j=0; j<bbWhite.length; j++)
		if (((1L << i) & bbWhite[j]) != 0) s = "w" + names[j];
	    for (int j=0; j<bbBlack.length; j++)
		if (((1L << i) & bbBlack[j]) != 0) s = "b" + names[j];
	    out+=String.format(String.format("%%%ds", 4), s);
	    if (i%8==0) out +="\n";
	}
	return out;
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
	for (int i=; i<6; i++) out |= bbBlack[i];
	return out;
    }

    /**
     *This method is used to get a bitboard containg all of the pieces 
     *@return long A bitboard containing all present pieces
     */

    public long getAll() {
	return getWhite() | getBlack();
    }

        // piece number for presence of either color, -1 for blank
    public int typeAtPosition(int i) {
	for (int j=0; j<6; j++)
	    if (((1L << i) & (bbBlack[j] | bbWhite[j])) != 0) return j;
	return -1;
    }

    // 0 for empty, 1 for white, -1 for black
    public int colorAtPosition(int i) {
	if (((1L << i) & getWhite()) != 0) return 1;
	if (((1L << i) & getBlack()) != 0) return -1;
	return 0;
    }
    
    public boolean makeMove(ChessMove move) {
	boolean capture = typeAtPosition(move.end) != -1;
	int type = typeAtPosition(move.start);
	switch (colorAtPosition(move.start)) {
	    case 1:
		bbWhite[type] |= (1L << move.end);
		bbWhite[type] &= -1 *((1L << move.start)+1L);
		break;
	    case -1:
		bbBlack[type] |= -1 * (1L << move.end);
		bbBlack[type] &= -1 *((1L << move.start)+1L);
		break;
	    case 0: return false;
	    }
	return capture;
    }
    
    public static void main(String[] a) {
	ChessBoard b = new ChessBoard();
	b.makeMove(new ChessMove("d2", "d3"));
	pr(b);
		   
    }



    static void pr(Object s) {
	System.out.println(s);
    }
    static void prl(Long l) {
	pr(Long.toBinaryString(l));
    }
}
