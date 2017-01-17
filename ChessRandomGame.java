import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;


public class ChessRandomGame {
    ChessBoard b;
    int turn;
    List<List<ChessMove>> moves;
    int start;
    int end;


    ChessRandomGame() {
	b = new ChessBoard();
	b.setup();
	moves = new ArrayList<List<ChessMove>>(64);
	start = -1;
	end = -1;
	turn = 1;
    }

    void setMoves() {
	moves.clear();
	for (int i=0; i<64; i++) {
	    if (((1L<<i) & b.getByColor(turn))!=0L) {
		moves.add(b.pieceMoves(i));
	    } else {
		moves.add(new LinkedList<>());
	    }
	}
    }

    List<ChessMove> allMoves() {
	List<ChessMove> out = new LinkedList<>();
	for (int i=0; i<64; i++) {
	    for (ChessMove move: moves.get(i)) {
		out.add(move);
	    }
	}
	return out;
    }

    ChessMove compMove() {
	List<ChessMove> allMoves = allMoves();
	List<ChessMove> captures = new LinkedList<>();
	for (ChessMove move: allMoves) {
	    if (move.capture) captures.add(move);
	}
	for (ChessMove move: captures) {
	    allMoves.remove(move);
	}
	if (captures.size()>0) {
	    return captures.get((new Random()).nextInt(captures.size()));
	}
	return allMoves.get((new Random()).nextInt(allMoves.size()));
    }
	
    
    boolean movesAvailable() {
	for (int i=0; i<64; i++) {
	    if (moves.get(i).size()!=0)
		if (((1L<<i) & b.getByColor(turn))!=0L)
		    return true;
	}
	return false;
    }
    
    void setHumanStart() {
	while (start==-1) {
	    System.out.print("Enter starting square: ");
	    String sStart = System.console().readLine().toLowerCase().trim();
	    try {
		start = ChessMove.toIndex(sStart);
		if (((1L<<start) & b.getAll()) == 0L)
		    throw new IllegalArgumentException("No piece at specified position");
		if (((1L<<start) & b.getByColor(-turn)) != 0L)
		    throw new IllegalArgumentException("Choose a " + (turn==ChessBoard.WHITE? "white": "black") + " piece");

		//if (b.typeAtPosition(start) != ChessBoard.KING) moves = ChessBoard.applyMask(moves, b.getByColor(turn), checkMask);
		if (moves.get(start).size()==0)

		    throw new IllegalArgumentException("No moves available for specified piece");
	    } catch (IllegalArgumentException iae) {
		System.out.println(iae.getMessage());
		start = -1;
	    }
	}
    }

    void setHumanEnd() {
	while (end == -1) {
	    System.out.print("Enter ending square (or c to cancel move): ");
	    String sEnd = System.console().readLine().toLowerCase().trim();
	    if (sEnd.toLowerCase().trim().equals("c")) {
		start = end = -1;
		return;
	    }
	    try {
		end = ChessMove.toIndex(sEnd);
		if (!moves.get(start).contains(new ChessMove(start, end)))
		    throw new IllegalArgumentException("Invalid move chosen for given piece");

	    } catch (IllegalArgumentException iae) {
		System.out.println(iae.getMessage());
		end = -1;
	    }
	}
    }

    

    void makeTurn() {
	System.out.println(b);
	if (turn==1) {
	    while (end == -1 || start == -1) {
		setHumanStart();
		System.out.println(b.toString(ChessBoard.moveMask(moves.get(start))|ChessBoard.captureMask(moves.get(start))));
		setHumanEnd();
	    }
	} else {
	    System.out.println("Computer move: ");
	    
	    ChessMove move = compMove();
	    start = move.start;
	    end = move.end;
	}
	b.makeMove(new ChessMove(start, end));
    }

    void playGame() {
	setMoves();
	while (movesAvailable()) {
	    makeTurn();
	    turn *= -1;
	    end = start = -1;
	    setMoves();
	    // checkMask = -1L;
	}
	if (b.attacking(b.getKingIndex(turn), -turn)==0L) {
	    System.out.println("Stalemate!");
	} else {
	    System.out.println("Checkmate! Win for " + (turn==ChessBoard.BLACK? "white": "black"));
	}
    }
    
    public static void main(String[] args) {
	ChessRandomGame game = new ChessRandomGame();
	game.playGame();
    }

    //debugging prints
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
	
    
	    
