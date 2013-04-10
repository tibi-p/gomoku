package gomoku.util;

import java.awt.Point;
import java.util.Random;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class Utils {

	public static final Random random = new Random();
	public static final int[] offsetX = new int[] { -1, 0, 1, 0 };
	public static final int[] offsetY = new int[] { 0, 1, 0, -1 };

	public static Point translateBy(Point point, int direction) {
		Point translatedPoint = new Point(point);
		int dx = offsetX[direction];
		int dy = offsetY[direction];

		translatedPoint.translate(dx, dy);

		return translatedPoint;
	}

	public static double sigmoid(double x) {
		return 1. / (1. + Math.exp(-x));
	}

	public static void useCoolSkin() {
		boolean changed = false;

		try {
			for (LookAndFeelInfo feel : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(feel.getName())) {
					UIManager.setLookAndFeel(feel.getClassName());
					changed = true;
					break;
				}
			}

			if (!changed)
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

}
