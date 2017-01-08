public class Chess {
    //never meant to be inititated, no instance methods.

    //diagonal masks
    public static long[] rowMasks = new long[64];
    public static long[] columnMasks = new long[64];
    //parallel masks
    public static long[] forwardMasks = new long[64];
    public static long[] backwardMasks = new long[64];

    public static long[][] masks = new long[][] {rowMasks, forwardMasks, columnMasks, backwardMasks};
    
    //piece masks
    public static long[] bishopMasks = new long[64];
    public static long[] rookMasks = new long[64];
    public static long[] queenMasks = new long[64];
    public static long[] knightMasks = new long[64];
    public static long[] kingMasks = new long[64];
    public static long[] pawnMasks = new long[64];
    
    static {
	for (int i=0; i<64; i++) {
	    diagonalMask(i);
	    parallelMask(i);
	    bishopMasks[i]=forwardMasks[i] | backwardMasks[i];
	    rookMasks[i]=rowMasks[i] | columnMasks[i];
	    queenMasks[i]=bishopMasks[i] | rookMasks[i];
	}
    }
	
    private static void diagonalMask(int position) {
	int forward  = position%8 - position/8;
	int backward = position%8 + position/8;
	
	long forwardMask  = 0L;
	long backwardMask = 0L;

	int start = forward>=0? forward: 8*forward;
	for (int i=0; i<(8-Math.abs(forward)); i++) {
	    forwardMask += 1L<<(9*i+start);
	}
	start = backward<=7? backward: 8*(backward-6)-1;
	for (int i=0; i<(8-Math.abs(backward-7)); i++) {
	    backwardMask += 1L<<(7*i+start);
	}
	
	forwardMasks[position] = forwardMask - (1L<<position);
	backwardMasks[position] = backwardMask - (1L<<position);
    }

    private static void parallelMask(int position) {
	long horizontalMask = ((1L<<8)-1L)<<(position/8*8);
	long verticalMask = (1L<<position%8)*72340172838076673L;

	rowMasks[position] = horizontalMask - (1L<<position);
	columnMasks[position] = verticalMask - (1L<<position);
    }


    //visual repr of longs for debugging
    public static String longToString(long l) {
	String out = "";
	for (int i=63; i>=0; i--) {
	    String s = "0";
	    if (((1L << i) & l) != 0) s="1";
	    out+=s;
	    if (i%8==0) out +="\n";
	}
	return out;
    }

    public static long forwardAttacks(long occ, int direction, int i) {
	long mask = masks[direction%4][i];
	long moves = occ & mask;
	long reverse = Long.reverse(moves);
	moves -= 2 * (1L<<i);
	reverse -= 2* (1L<<(63-i));
	moves ^= Long.reverse(reverse);
	return moves & mask;
    }
    
    public static void main(String[] args) {
	long occ = 0b00000000_01000001_00000010_00000000_00001000_00010010_00100000_01000000L;
	prl(forwardAttacks(occ, 3, 27));
    }

    static void pr(Object o) {
	System.out.println(o);
    }
    static void prl(long l) {
	pr(longToString(l));
    }
}	    
