public class Chess {
    //never meant to be inititated, no instance methods.

    public static long[] bishopMasks = new long[64];
    public static long[] rookMasks = new long[64];
    public static long[] queenMasks = new long[64];
    static {
	for (int i=0; i<64; i++) {
	    bishopMasks[i]=diagonalMask(i);
	    rookMasks[i]=parallelMask(i);
	    queenMasks[i]=bishopMasks[i] | rookMasks[i];
	}
    }
	
    private static long diagonalMask(int position) {
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
	return (forwardMask | backwardMask) & ~(1L<<position);
    }

    private static long parallelMask(int position) {
	long horizontalMask = ((1L<<8)-1L)<<(position/8*8);
	long verticalMask = (1L<<position%8)*72340172838076673L;

	return (horizontalMask | verticalMask) & ~(1L<<position);
    }


    //visual repr of longs for debugging
    public static String longToString(long l) {
	String out = "";
	for (int i=63; i>=0; i--) {
	    String s = "_";
	    if (((1L << i) & l) != 0) s="O";
	    out+=s;
	    if (i%8==0) out +="\n";
	}
	return out;
    }
    
    public static void main(String[] args) {
	System.out.println(longToString(queenMasks[Integer.parseInt(args[0])]));
    }

    static void pr(Object o) {
	System.out.println(o);
    }
}	    
