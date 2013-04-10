package gomoku.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class NeuralMap extends JPanel {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 8486775150671296748L;

	private Dimension panelSize = null;
	private BufferedImage bufferImage = null;
	private Graphics2D imageGraphics = null;
	private boolean resized = true;
	private double[][] map = null;

	public NeuralMap() {
		super(new BorderLayout());
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				resized = true;
			}

		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D panelGraphics = (Graphics2D) g;

		if (resized) {
			initBackBuffer();
			resized = false;
		}

		imageGraphics.setBackground(Color.LIGHT_GRAY);
		imageGraphics.clearRect(0, 0, panelSize.width, panelSize.height);

		// for (int i = 1; i < rows; i++) {
		// int y = i * panelSize.height / rows;
		// imageGraphics.setBackground(Color.LIGHT_GRAY);
		// imageGraphics.clearRect(0, y - 1, panelSize.width, 3);
		// }
		// for (int i = 1; i < columns; i++) {
		// int x = i * panelSize.width / columns;
		// imageGraphics.setBackground(Color.LIGHT_GRAY);
		// imageGraphics.clearRect(x - 1, 0, 3, panelSize.height);
		// }
		if (map != null) {
			int rows = map.length;
			if (rows > 0) {
				int columns = map[0].length;
				for (int i = 0; i < rows; i++)
					for (int j = 0; j < columns; j++) {
						int sy = i * panelSize.height / rows;
						int ey = (i + 1) * panelSize.height / rows;
						int sx = j * panelSize.width / columns;
						int ex = (j + 1) * panelSize.width / columns;
						float value = (float) map[i][j];
						Color color = new Color(value, value, value);
						imageGraphics.setBackground(color);
						imageGraphics.clearRect(sx, sy, ex - sx, ey - sy);
					}
			}
		}

		panelGraphics.drawImage(bufferImage, 0, 0, this);
		/*
		 * for (Piece plant : model.getPieces()) { BufferedImage image; if
		 * (plant.getColor() == Piece.Color.WHITE) image = whiteImage; else
		 * image = blackImage; AffineTransform xform = getObjectTransform(plant,
		 * image); panelGraphics.drawImage(image, xform, this); }
		 */
	}

	public void setMap(double[][] map) {
		this.map = map;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});
	}

	private void initBackBuffer() {
		panelSize = getSize();
		bufferImage = (BufferedImage) createImage(panelSize.width,
				panelSize.height);
		imageGraphics = (Graphics2D) bufferImage.getGraphics();
	}

}
