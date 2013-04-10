package gomoku.gui;

import gomoku.GomokuWorker;
import gomoku.game.WorldModel;
import gomoku.graphics.IGraphicObject;
import gomoku.graphics.Piece;
import gomoku.individual.NeuralNetwork;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GomokuPanel extends JPanel {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 6741211207073293087L;

	private Dimension panelSize = null;
	private BufferedImage bufferImage = null;
	private Graphics2D imageGraphics = null;
	private GomokoGUI gui;
	private BufferedImage whiteImage;
	private BufferedImage blackImage;
	private boolean resized;
	private WorldModel model;
	private GomokuWorker worker;
	// private Piece.Color humanPlayer = Piece.Color.BLACK;
	private Piece.Color humanPlayer = Piece.Color.WHITE;
	private NeuralNetwork nn = new NeuralNetwork(10);

	public GomokuPanel(final GomokoGUI gui) throws IOException {
		super(new BorderLayout());
		this.gui = gui;
		whiteImage = ImageIO.read(new File("white_duck.jpg"));
		whiteImage.setAccelerationPriority(1.0f);
		blackImage = ImageIO.read(new File("cabbage5.jpg"));
		blackImage.setAccelerationPriority(1.0f);
		nn.initRandom();
		resized = true;
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if (model != null
						&& humanPlayer.equals(model.getCurrentPlayer())) {
					double x = (double) event.getX() / panelSize.width;
					double y = (double) event.getY() / panelSize.height;
					y *= model.rows();
					x *= model.columns();
					int row = (int) (y + .5);
					int col = (int) (x + .5);
					y -= row;
					x -= col;
					double dist = x * x + y * y;
					if (dist < .2) {
						if (col > 0 && row > 0 && col < model.columns()
								&& row < model.rows()) {
							boolean valid = model.addPiece(new Piece(row - 1,
									col - 1, model.getCurrentPlayer()));
							if (model.getWinner() != null) {
								stop();
							} else if (valid) {
								worker.sendMessage(1);
							}
						}
					}
				}
			}

		});
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				resized = true;
			}

		});
	}

	public void start(int rows, int columns) {
		model = new WorldModel(rows, columns);
		worker = new GomokuWorker(gui, this, model);
		worker.sendMessage(humanPlayer.equals(Piece.Color.BLACK) ? 0 : 1);
		new Thread(worker).start();
	}

	public void stop() {
		worker.sendMessage(-1);
	}

	public void finishStop() {
		worker = null;
		model = null;
	}

	@Override
	public void paint(Graphics g) {
		// Are we running?
		if (model == null)
			return;

		Graphics2D panelGraphics = (Graphics2D) g;
		int rows = model.rows();
		int columns = model.columns();

		if (resized) {
			initBackBuffer();
			resized = false;
		}

		imageGraphics.setBackground(Color.DARK_GRAY);
		imageGraphics.clearRect(0, 0, panelSize.width, panelSize.height);

		for (int i = 1; i < rows; i++) {
			int y = i * panelSize.height / rows;
			imageGraphics.setBackground(Color.LIGHT_GRAY);
			imageGraphics.clearRect(0, y - 1, panelSize.width, 3);
		}
		for (int i = 1; i < columns; i++) {
			int x = i * panelSize.width / columns;
			imageGraphics.setBackground(Color.LIGHT_GRAY);
			imageGraphics.clearRect(x - 1, 0, 3, panelSize.height);
		}
		panelGraphics.drawImage(bufferImage, 0, 0, this);
		for (Piece plant : model.getPieces()) {
			BufferedImage image;
			if (plant.getColor() == Piece.Color.WHITE)
				image = whiteImage;
			else
				image = blackImage;
			AffineTransform xform = getObjectTransform(plant, image);
			panelGraphics.drawImage(image, xform, this);
		}
	}

	private void initBackBuffer() {
		panelSize = getSize();
		bufferImage = (BufferedImage) createImage(panelSize.width,
				panelSize.height);
		imageGraphics = (Graphics2D) bufferImage.getGraphics();
	}

	private double getObjectScale(BufferedImage objectImage, double maxHeight,
			double maxWidth) {
		int height = objectImage.getHeight();
		int width = objectImage.getWidth();
		double diameter = Math.sqrt(height * height + width * width);
		return Math.min(maxHeight, maxWidth) / diameter;
	}

	private AffineTransform getObjectTransform(IGraphicObject graphicObject,
			BufferedImage objectImage) {
		AffineTransform xform = new AffineTransform();
		double rowHeight = (double) panelSize.height / model.rows();
		double columnWidth = (double) panelSize.width / model.columns();
		double scale = .95 * getObjectScale(objectImage, rowHeight - 3,
				columnWidth - 3);
		double x = graphicObject.getX() + 1;
		double y = graphicObject.getY() + 1;

		xform.translate(y * columnWidth, x * rowHeight);
		xform.scale(scale, scale);
		xform.translate(-objectImage.getWidth() / 2,
				-objectImage.getHeight() / 2);

		return xform;
	}

}
