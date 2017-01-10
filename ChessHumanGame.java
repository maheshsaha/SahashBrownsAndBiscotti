import java.util.List;
import java.util.LinkedList;

public class ChessHumanGame {
    ChessBoard b;
    int turn;
    long checkMask;
    List<ChessMove> moves;
    int start;
    int end;
    
    ChessHumanGame() {
	b = new ChessBoard();
	b.setup();
	turn = ChessBoard.WHITE;
	checkMask = -1L;
	moves = new LinkedList<>();
	start = -1;
	end = -1;
    }

    void setStart() {
	while (start==-1) {
	    System.out.print("Enter starting square: ");
	    String sStart = System.console().readLine().toLowerCase().trim();
	    try {
		start = ChessMove.toIndex(sStart);
		if (((1L<<start) & b.getAll()) == 0L)
		    throw new IllegalArgumentException("No piece at specified position");
		if (((1L<<start) & b.getByColor(-turn)) != 0L)
		    throw new IllegalArgumentException("Choose a " + (turn==ChessBoard.WHITE? "white": "black") + " piece");
		moves = b.pieceMoves(start);

		if (b.typeAtPosition(start) != ChessBoard.KING) moves = ChessBoard.applyMask(moves,b.getByColor(turn), checkMask); 

		if (moves.size()==0)
		    throw new IllegalArgumentException("No moves available for specified piece");
	    } catch (IllegalArgumentException iae) {
		System.out.println(iae.getMessage());
		start = -1;
	    }
	}
    }

    void setEnd() {
	while (end == -1) {
	    System.out.print("Enter ending square (or c to cancel move): ");
	    String sEnd = System.console().readLine().toLowerCase().trim();
	    if (sEnd.toLowerCase().trim().equals("c")) {
		start = end = -1;
		return;
	    }
	    try {
		end = ChessMove.toIndex(sEnd);
		if (!moves.contains(new ChessMove(start, end)))
		    throw new IllegalArgumentException("Invalid move chosen for given piece");

	    } catch (IllegalArgumentException iae) {
		System.out.println(iae.getMessage());
		end = -1;
	    }
	}
    }

    void makeTurn() {
	System.out.println(b);
	checkMask = b.inCheckFilter(turn);
	while (end == -1 || start == -1) {
	    setStart();
	    System.out.println(b.toString(ChessBoard.moveMask(moves)|ChessBoard.captureMask(moves)));
	    setEnd();
	}
	b.makeMove(new ChessMove(start, end));
    }

    void playGame() {
	while (true) {
	    makeTurn();
	    turn = -1*turn;
	    end = start = -1;
	    checkMask = -1L;
	}
    }
    
    public static void main(String[] args) {
	ChessHumanGame game = new ChessHumanGame();
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
	
    
	    
