package gomoku.individual;

import gomoku.genetics.Individual;
import gomoku.graphics.Piece;
import gomoku.util.Utils;

public class NeuralNetwork extends Individual<NeuralNetwork> {

	private final int size;
	private double[][] wi;
	private double[][] wh;
	private double[] wo;
	private double[] wb;

	public NeuralNetwork(int size) {
		this.size = size;
		wi = new double[2][size];
		wh = new double[size][size];
		wo = new double[size];
		wb = new double[size];
	}

	public void initRandom() {
		fillRandomMatrix(wi);
		fillRandomMatrix(wh);
		fillRandomArray(wo);
		fillRandomArray(wb);
	}

	public int size() {
		return size;
	}

	public double[][] getOutput(Piece[][] grid, Piece.Color player) {
		if (grid == null || player == null)
			throw new NullPointerException();

		int playerID = player.getID();
		int rows = grid.length;
		if (rows > 0) {
			int columns = grid[0].length;
			int[][][] input = new int[rows][columns][2];
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					Piece piece = grid[i][j];
					if (piece != null) {
						int pieceID = piece.getColor().getID();
						input[i][j][pieceID ^ playerID] = 1;
					}
				}

			double[][][][] hidden = new double[4][rows][columns][size];
			computeHiddenRightDown(grid, input, rows, columns, hidden[0]);
			computeHiddenLeftDown(grid, input, rows, columns, hidden[1]);
			computeHiddenLeftUp(grid, input, rows, columns, hidden[2]);
			computeHiddenRightUp(grid, input, rows, columns, hidden[3]);

			double[][] output = new double[rows][columns];
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					double sum = 0.0;
					for (int k = 0; k < 4; k++)
						for (int l = 0; l < size; l++)
							sum += wo[l] * hidden[k][i][j][l];
					output[i][j] = Utils.sigmoid(sum);
				}
			return output;
		} else {
			return new double[0][0];
		}
	}

	public NeuralNetwork[] crossover(NeuralNetwork other) {
		if (size != other.size)
			throw new IllegalArgumentException();

		NeuralNetwork[] offsprings = new NeuralNetwork[2];
		for (int i = 0; i < 2; i++)
			offsprings[i] = new NeuralNetwork(size);

		for (int i = 0; i < wi.length; i++)
			for (int j = 0; j < wi[i].length; j++) {
				DoublePair pair = new DoublePair(wi[i][j], other.wi[i][j]);
				pair = pair.mixAll();
				offsprings[0].wi[i][j] = pair.getFirst();
				offsprings[1].wi[i][j] = pair.getSecond();
			}

		for (int i = 0; i < wh.length; i++)
			for (int j = 0; j < wh[i].length; j++) {
				DoublePair pair = new DoublePair(wh[i][j], other.wh[i][j]);
				pair = pair.mixAll();
				offsprings[0].wh[i][j] = pair.getFirst();
				offsprings[1].wh[i][j] = pair.getSecond();
			}

		for (int i = 0; i < wo.length; i++) {
			DoublePair pair = new DoublePair(wo[i], other.wo[i]);
			pair = pair.mixAll();
			offsprings[0].wo[i] = pair.getFirst();
			offsprings[1].wo[i] = pair.getSecond();
		}

		for (int i = 0; i < wb.length; i++) {
			DoublePair pair = new DoublePair(wb[i], other.wb[i]);
			pair = pair.mixAll();
			offsprings[0].wb[i] = pair.getFirst();
			offsprings[1].wb[i] = pair.getSecond();
		}

		return offsprings;
	}

	public void mutate(int percent) {
		mutateRandomMatrix(wi, percent);
		mutateRandomMatrix(wh, percent);
		mutateRandomArray(wo, percent);
		mutateRandomArray(wb, percent);
	}

	private class DoublePair {

		private double fst, snd;

		public DoublePair(double fst, double snd) {
			this.fst = fst;
			this.snd = snd;
		}

		public double getFirst() {
			return fst;
		}

		public double getSecond() {
			return snd;
		}

		public DoublePair mixAll() {
			if (Utils.random.nextBoolean())
				return new DoublePair(fst, snd);
			else
				return new DoublePair(snd, fst);
		}

		@Override
		public String toString() {
			return fst + " " + snd;
		}

	}

	private void computeHiddenRightDown(Piece[][] grid, int[][][] input,
			int rows, int columns, double[][][] hidden) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				double[] left, up;

				if (i > 0)
					left = hidden[i - 1][j];
				else
					left = wb;

				if (j > 0)
					up = hidden[i][j - 1];
				else
					up = wb;

				computeHiddenEntry(input[i][j], left, up, hidden[i][j]);
			}
		}
	}

	private void computeHiddenLeftDown(Piece[][] grid, int[][][] input,
			int rows, int columns, double[][][] hidden) {
		for (int i = rows - 1; i >= 0; i--) {
			for (int j = 0; j < columns; j++) {
				double[] right, up;

				if (i < rows - 1)
					right = hidden[i + 1][j];
				else
					right = wb;

				if (j > 0)
					up = hidden[i][j - 1];
				else
					up = wb;

				computeHiddenEntry(input[i][j], right, up, hidden[i][j]);
			}
		}
	}

	private void computeHiddenLeftUp(Piece[][] grid, int[][][] input, int rows,
			int columns, double[][][] hidden) {
		for (int i = rows - 1; i >= 0; i--) {
			for (int j = columns - 1; j >= 0; j--) {
				double[] right, down;

				if (i < rows - 1)
					right = hidden[i + 1][j];
				else
					right = wb;

				if (j < columns - 1)
					down = hidden[i][j + 1];
				else
					down = wb;

				computeHiddenEntry(input[i][j], right, down, hidden[i][j]);
			}
		}
	}

	private void computeHiddenRightUp(Piece[][] grid, int[][][] input,
			int rows, int columns, double[][][] hidden) {
		for (int i = 0; i < rows; i++) {
			for (int j = columns - 1; j >= 0; j--) {
				double[] left, down;

				if (i > 0)
					left = hidden[i - 1][j];
				else
					left = wb;

				if (j < columns - 1)
					down = hidden[i][j + 1];
				else
					down = wb;

				computeHiddenEntry(input[i][j], left, down, hidden[i][j]);
			}
		}
	}

	private void computeHiddenEntry(int[] cell, double[] x, double[] y,
			double[] hidden) {
		for (int k = 0; k < size; k++) {
			double sum = 0.0;
			for (int l = 0; l < 2; l++)
				sum += cell[l] * wi[l][k];
			for (int l = 0; l < size; l++)
				sum += x[l] * wh[l][k];
			for (int l = 0; l < size; l++)
				sum += y[l] * wh[l][k];
			hidden[k] = Math.tanh(sum);
		}
	}

	private void fillRandomMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = 2 * Utils.random.nextDouble() - 1;
	}

	private void fillRandomArray(double[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = 2 * Utils.random.nextDouble() - 1;
	}

	private void mutateRandomMatrix(double[][] matrix, int percent) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				if (Utils.random.nextInt(100) < percent)
					matrix[i][j] = 2 * Utils.random.nextDouble() - 1;
	}

	private void mutateRandomArray(double[] array, int percent) {
		for (int i = 0; i < array.length; i++)
			if (Utils.random.nextInt(100) < percent)
				array[i] = 2 * Utils.random.nextDouble() - 1;
	}

}
