package gomoku.graphics;

public class Piece extends AbstractGraphicObject {

	public enum Color {

		BLACK(0), WHITE(1);

		private int id;

		private Color(int id) {
			this.id = id;
		}

		public int getID() {
			return id;
		}

		public Color flipped() {
			if (WHITE.equals(this))
				return BLACK;
			else
				return WHITE;
		}

	}

	private Color color;

	public Piece(int x, int y, Color color) {
		super(x, y);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
