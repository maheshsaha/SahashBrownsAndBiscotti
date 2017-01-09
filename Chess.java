public class Chess {
    //never meant to be inititated, no instance methods.

    
    
    
    //diagonal masks
    public static long[] rowMasks = new long[64];
    public static long[] columnMasks = new long[64];
    //parallel masks
    public static long[] forwardMasks = new long[64];
    public static long[] backwardMasks = new long[64];

    public static long[][] masks = new long[][] {rowMasks, forwardMasks, columnMasks, backwardMasks};
    
    /* Uses function instead, because depends on occupancy
    public static long[] bishopMasks = new long[64];
    public static long[] rookMasks = new long[64];
    public static long[] queenMasks = new long[64];
    */
    public static long[] knightMasks = new long[64];
    public static long[] kingMasks = new long[64];
    //pawns have diff masks for white moves, white captures, black moves, black captures
    public static long[][] pawnMasks = new long[64][4];
    
    static {
	for (int i=0; i<64; i++) {
	    diagonalMask(i);
	    parallelMask(i);
	    /*bishopMasks[i]=forwardMasks[i] | backwardMasks[i];
	    rookMasks[i]=rowMasks[i] | columnMasks[i];
	    queenMasks[i]=bishopMasks[i] | rookMasks[i];*/

	    knightMask(i);
	    kingMask(i);
	    pawnMask(i);
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


    private static void knightMask(int position) {
	long knightMask = 0L;
	int[][] knightMoves = {{2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}, {1, 2}};
	outer: for (int i=0; i<8; i++) {
	    for (int j=0; j<2; j++) {
		knightMoves[i][j]+= (j==0)? position%8: position/8;
		if (knightMoves[i][j]<0 || knightMoves[i][j]>=8) continue outer;
	    }
	    knightMask += (1L<<(knightMoves[i][0]+8*knightMoves[i][1]));
	}
	knightMasks[position] = knightMask;
    }

    private static void kingMask(int position) {
	long kingMask = 0L;
	int[][] kingMoves = {{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}};
	outer: for (int i=0; i<8; i++) {
	    for (int j=0; j<2; j++) {
		kingMoves[i][j]+= (j==0)? position%8: position/8;
		if (kingMoves[i][j]<0 || kingMoves[i][j]>=8) continue outer;
	    }
	    kingMask += (1L<<(kingMoves[i][0]+8*kingMoves[i][1]));
	}
	kingMasks[position] = kingMask;
    }

    private static void pawnMask(int position) {
	if (position/8<7) pawnMasks[position][0] = (1L<<(position+8));
	if (position/8==1) pawnMasks[position][0] += (1L<<(position+16));
	if (position/8>0) pawnMasks[position][2] = (1L<<(position-8));
	if (position/8==6) pawnMasks[position][2] += (1L<<(position-16));

	long pawnWCapMask = 0L;
	long pawnBCapMask = 0L;
	int[][] pawnWCapMoves = {{1, 1}, {-1, 1}};
	int[][] pawnBCapMoves = {{1, -1}, {-1, -1}};
	outer: for (int i=0; i<2; i++) {
	    for (int j=0; j<2; j++) {
		pawnWCapMoves[i][j]+= (j==0)? position%8: position/8;
		if (pawnWCapMoves[i][j]<0 || pawnWCapMoves[i][j]>=8) continue outer;
	    }
	    pawnWCapMask += (1L<<(pawnWCapMoves[i][0]+8*pawnWCapMoves[i][1]));
	}
	pawnMasks[position][1] = pawnWCapMask;
	outer: for (int i=0; i<2; i++) {
	    for (int j=0; j<2; j++) {
		pawnBCapMoves[i][j]+= (j==0)? position%8: position/8;
		if (pawnBCapMoves[i][j]<0 || pawnBCapMoves[i][j]>=8) continue outer;
	    }
	    pawnBCapMask += (1L<<(pawnBCapMoves[i][0]+8*pawnBCapMoves[i][1]));
	}
	pawnMasks[position][3] = pawnBCapMask;
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

    public static long directionMask(long occ, int direction, int i) {
	long mask = masks[direction%4][i];
	long moves = occ & mask;
	long reverse = Long.reverse(moves);
	moves -= 2 * (1L<<i);
	reverse -= 2* (1L<<(63-i));
	moves ^= Long.reverse(reverse);
	return moves & mask;
    }

    public static long bishopMask(long occ, int i) {
	return directionMask(occ, 1, i) | directionMask(occ, 3, i);
    }
    public static long rookMask(long occ, int i) {
	return directionMask(occ, 0, i) | directionMask(occ, 2, i);
    }
    public static long queenMask(long occ, int i) {
	return bishopMask(occ, i) | rookMask(occ, i);
    }
    
    public static void main(String[] args) {
	prl(knightMasks[0]);
    }

    static void pr(Object o) {
	System.out.println(o);
    }
    static void prl(long l) {
	pr(longToString(l));
    }
}	    
