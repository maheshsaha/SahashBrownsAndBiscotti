public class ChessBoard {
    public static final int PAWN = 0;
    public static final int BISHOP = 1;
    public static final int KNIGHT = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;
    public static final String[] names = new String[] {"P", "B", "H", "R", "Q", "K"};
    public final long[] bbWhite;
    public final long[] bbBlack;

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

    public long getWhite() {
	long out = 0L;
	for (int i=0; i<6; i++) out |= bbWhite[i];
	return out;
    }

    public long getBlack() {
	long out = 0L;
	for (int i=; i<6; i++) out |= bbBlack[i];
	return out;
    }

    public long getAll() {
	return getWhite() | getBlack();
    }

    public static void main(String[] a) {
	ChessBoard b = new ChessBoard();
	System.out.println(b);
    }
}
