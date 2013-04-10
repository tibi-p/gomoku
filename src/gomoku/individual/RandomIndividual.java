package gomoku.individual;

import gomoku.genetics.StagnantIndividual;
import gomoku.graphics.Piece;
import gomoku.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomIndividual extends StagnantIndividual<RandomIndividual> {

	private final Random random = new Random();

	public double[][] getOutput(Piece[][] grid, Piece.Color player) {
		if (grid == null || player == null)
			throw new NullPointerException();

		int rows = grid.length;
		if (rows > 0) {
			int columns = grid[0].length;
			List<Pair<Integer, Integer>> freePositions = new ArrayList<Pair<Integer, Integer>>();
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					if (grid[i][j] == null)
						freePositions.add(new Pair<Integer, Integer>(i, j));

			double[][] output = new double[rows][columns];
			if (!freePositions.isEmpty()) {
				int r = random.nextInt(freePositions.size());
				Pair<Integer, Integer> move = freePositions.get(r);
				output[move.getKey()][move.getValue()] = 1.0;
			}
			return output;
		} else {
			return new double[0][0];
		}
	}

	public RandomIndividual[] crossover(RandomIndividual other) {
		return new RandomIndividual[] { this, other };
	}

}
