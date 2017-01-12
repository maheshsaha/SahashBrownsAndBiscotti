//Code implemented/developed from https://proghammer.wordpress.com/2010/08/10/chess01-dragging-game-pieces/

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * all x and y coordinates point to the upper left position of a component all
 * lists are treated as 0 being the bottom and size-1 being the top piece
 * 
 */
public class ChessGui extends JPanel {

	private static final long serialVersionUID = 3114147670071466558L;
	
	private static final int COLOR_WHITE = 0;
	private static final int COLOR_BLACK = 1;

	private static final int TYPE_ROOK = 1;
	private static final int TYPE_KNIGHT = 2;
	private static final int TYPE_BISHOP = 3;
	private static final int TYPE_QUEEN = 4;
	private static final int TYPE_KING = 5;
	private static final int TYPE_PAWN = 6;

	private static final int BOARD_START_X = 301;
	private static final int BOARD_START_Y = 51;

	private static final int TILE_OFFSET_X = 50;
	private static final int TILE_OFFSET_Y = 50;

	private Image imgBackground;

	// 0 = bottom, size-1 = top
	private List<Piece> pieces = new ArrayList<Piece>();

    	public ChessGui() {
		// load and set background image
		URL urlBackgroundImg = getClass().getResource("/img/board.png");
		this.imgBackground = new ImageIcon(urlBackgroundImg).getImage();

		// create application frame and set visible
		//
		JFrame f = new JFrame();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(this);
		f.setResizable(false);
		f.setSize(this.imgBackground.getWidth(null), this.imgBackground.getHeight(null));
	}


	private Image getImageForPiece(int color, int type) {
		String filename = "";

		filename += (color == COLOR_WHITE ? "w" : "b");
		switch (type) {
			case TYPE_BISHOP:
				filename += "b";
				break;
			case TYPE_KING:
				filename += "k";
				break;
			case TYPE_KNIGHT:
				filename += "n";
				break;
			case TYPE_PAWN:
				filename += "p";
				break;
			case TYPE_QUEEN:
				filename += "q";
				break;
			case TYPE_ROOK:
				filename += "r";
				break;
		}
		filename += ".png";

		URL urlPieceImg = getClass().getResource("/img/" + filename);
		return new ImageIcon(urlPieceImg).getImage();
	}

	public static void main(String[] args) {
		new ChessGui();
	}
}
