public class ChessMove {
    public final int start;
    public final int end;
    public final boolean capture;
    public static final ChessMove MATE = new ChessMove(-1, -1);
    
    public ChessMove(int start, int end) {
	this(start, end, false);
    }
    public ChessMove(String start, String end) {
	this(start, end, false);
    }
    public ChessMove(int start, int end, boolean capture) {
	this.start = start;
	this.end = end;
	this.capture = capture;
    }
    public ChessMove(String start, String end, boolean capture) {
	this.start = toIndex(start);
	this.end = toIndex(end);
	this.capture = capture;
    }

    /*
    public ChessMove opposite() {
	return new ChessMove(start, end, !capture);
    }
    public boolean equals(ChessMove other) {
	return start==other.start & end==other.end & capture==other.capture;
    }
    */
    public boolean equals(Object other) {
	if (other==null || !(other instanceof ChessMove)) return false;
	ChessMove otherMove = (ChessMove) other;
	if (start==otherMove.start & end==otherMove.end) return true;
	return false;
    }
    
    public static int toIndex(String s) {
	try {
	    int column = 104 - s.charAt(0);
	    int row = s.charAt(1) - 49;
	    if (row < 0 || row >= 8 || column < 0 || column >=8)
		throw new IndexOutOfBoundsException();
	    return 8*row + column;
	} catch (IndexOutOfBoundsException iofbe) {
	    throw new IllegalArgumentException("No square with specified position");
	}
    }
    public static String toPositionString(int n) {
	if (n<0 || n >= 64)
	    throw new IllegalArgumentException("Invalid board index");
	String column = Character.toString((char) (104-n%8));
	String row = Character.toString((char) (n/8 + 49));
	return column + row;
    }

    public String toString() {
	return toPositionString(start) + " -> " + toPositionString(end) + (capture? " ; capture": "");
    }
    public static void main(String[] args) {
	for (String s: args) System.out.println(toPositionString(toIndex(s)));
    }
}

   
