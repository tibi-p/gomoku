package gomoku;

import gomoku.game.WorldModel;
import gomoku.genetics.Individual;
import gomoku.graphics.Piece;
import gomoku.gui.GomokoGUI;
import gomoku.gui.GomokuPanel;
import gomoku.individual.NeuralNetwork;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class GomokuWorker implements Runnable {

	private GomokoGUI gui;
	private Component panel;
	private WorldModel worldModel;
	private BlockingQueue<Integer> blockingQueue;

	public GomokuWorker(GomokoGUI gui, Component panel, WorldModel worldModel) {
		this.gui = gui;
		this.panel = panel;
		this.worldModel = worldModel;
		blockingQueue = new ArrayBlockingQueue<Integer>(1);
	}

	public void sendMessage(int value) {
		blockingQueue.offer(value);
	}

	public void run() {
		int iterations = 20;
		List<NeuralNetwork> firstGeneration = getFirstGeneration(iterations);
		int numNetworks = Math.min(10, firstGeneration.size());
		while (firstGeneration.size() > numNetworks)
			firstGeneration.remove(firstGeneration.size() - 1);
		NeuralNetwork nn = new GomokuCoTrainer(worldModel, firstGeneration,
				numNetworks, 4).train(iterations);
		// NeuralNetwork nn = firstGeneration.get(0);
		// Individual<?> nn = new HeuristicIndividual();
		worldModel.clearBoard();
		while (true) {
			try {
				Integer value = blockingQueue.take();
				if (value == null)
					break;
				if (value > 0) {
					Piece.Color currentPlayer = worldModel.getCurrentPlayer();
					double[][] output = nn.getOutput(worldModel.getGrid(),
							currentPlayer);
					Piece[][] grid = worldModel.getGrid();
					for (int i = 0; i < grid.length; i++)
						for (int j = 0; j < grid[i].length; j++) {
							Piece piece = grid[i][j];
							if (piece != null)
								output[i][j] = 0.0;
						}
					gui.updateNeuralMap(output);
					worldModel.playMove(currentPlayer, output);
				} else {
					if (nn != null)
						runSimulationStep(nn);
				}
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						panel.repaint();
					}
				});
				if (value < 0)
					break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					((GomokuPanel) panel).finishStop();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private List<NeuralNetwork> getFirstGeneration(int iterations) {
		GomokuFixedTrainer trainer = new GomokuFixedTrainer(worldModel, 75, 4,
				3);
		return trainer.train(iterations);
	}

	private void runSimulationStep(Individual<?> nn)
			throws InterruptedException, InvocationTargetException {
		double[][] output = nn.getOutput(worldModel.getGrid(),
				worldModel.getCurrentPlayer());
		Piece[][] grid = worldModel.getGrid();
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				Piece piece = grid[i][j];
				if (piece != null)
					output[i][j] = 0.0;
			}
		gui.updateNeuralMap(output);
	}

}
