import java.util.List;
import java.util.LinkedList;

public class ChessMoveHistory {
    final LinkedList<HistoryMove> moveHistory;
    final LinkedList<ChessBoard> boardHistory;

    public void addGeneration(ChessBoard child, ChessMove move, int type, boolean checking) {
	ChessBoard parent = boardHistory.getLast();
	HistoryMove current = new HistoryMove(move, type, checking, parent, child);
	boardHistory.add(child);
	moveHistory.add(current);
    }

    public ChessMoveHistory(ChessBoard initial) {
	moveHistory = new LinkedList<>();
	boardHistory = new LinkedList<ChessBoard>();
	boardHistory.add(initial);
    }
}

class HistoryMove {
    final int start;
    final int end;
    final int type;
    final boolean capture;
    final boolean checking;
    ChessBoard parent;
    ChessBoard child;
    public static final HistoryMove WHITEMATE = new HistoryMove();
    public static final HistoryMove BLACKMATE = new HistoryMove();
    public static final HistoryMove STALEMATE = new HistoryMove();

    public HistoryMove(ChessMove move, int type, boolean checking, ChessBoard parent, ChessBoard child) {
	start = move.start;
	end = move.end;
	this.type = type;
	capture = move.capture;
	this.checking = checking;
    }

    public HistoryMove(ChessMove move, int type) {
	this(move, type, false, new ChessBoard(), new ChessBoard());
    }

    public HistoryMove() {
	this(ChessMove.MATE, -1);
    }

    public void setParent(ChessBoard parent) {
	this.parent = parent;
    }

    public void setChild(ChessBoard child) {
	this.child = child;
    }
}
