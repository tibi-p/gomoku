package gomoku.graphics;

import java.awt.Point;

public abstract class AbstractGraphicObject implements IGraphicObject {

	protected Point position;
	protected double theta;

	public AbstractGraphicObject(int x, int y) {
		this.position = new Point(x, y);
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public int getX() {
		return position.x;
	}

	public void setX(int x) {
		position.x = x;
	}

	public int getY() {
		return position.y;
	}

	public void setY(int y) {
		position.y = y;
	}

}
