public class ChessMove {
    public final int start;
    public final int end;

    public ChessMove(int start, int end) {
	this.start = start;
	this.end = end;
    }
    public ChessMove(String start, String end) {
	this.start = toIndex(start);
	this.end = toIndex(end);
    }
    public static int toIndex(String s) {
	try {
	    int column = 104 - s.charAt(0);
	    int row = s.charAt(1) - 49;
	    System.out.println("\t" + column + ", " + row);
	    if (row < 0 || row >= 8 || column < 0 || column >=8)
		throw new IndexOutOfBoundsException();
	    return 8*row + column;
	} catch (IndexOutOfBoundsException iofbe) {
	    throw new IllegalArgumentException("Invalid board position string");
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
	return toPositionString(start) + " -> " + toPositionString(end);
    }
    public static void main(String[] args) {
	for (String s: args) System.out.println(toPositionString(toIndex(s)));
    }
}

   
