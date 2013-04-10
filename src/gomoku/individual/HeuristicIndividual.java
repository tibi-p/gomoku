package gomoku.individual;

import gomoku.genetics.StagnantIndividual;
import gomoku.graphics.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicIndividual extends
		StagnantIndividual<HeuristicIndividual> {

	private final Random random = new Random();

	public double[][] getOutput(Piece[][] grid, Piece.Color player) {
		if (grid == null || player == null)
			throw new NullPointerException();

		int playerID = player.getID();
		int rows = grid.length;
		if (rows > 0) {
			int columns = grid[0].length;

			int[] bestValue = new int[] { -1, -1 };
			int[] bestX = new int[] { -1, -1 };
			int[] bestY = new int[] { -1, -1 };

			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					int crtID, runLength;
					List<Integer> freeX = new ArrayList<Integer>();
					List<Integer> freeY = new ArrayList<Integer>();

					crtID = -1;
					runLength = 0;
					freeX.clear();
					freeY.clear();
					for (int k = 0; k < 5; k++) {
						int nx = i + k;
						int ny = j;
						if (nx >= rows) {
							crtID = -1;
							break;
						}
						Piece piece = grid[nx][ny];
						if (piece != null) {
							int pieceID = piece.getColor().getID();
							int realID = pieceID ^ playerID;
							if (crtID == -1) {
								crtID = realID;
							}
							if (realID == crtID) {
								runLength++;
							} else {
								break;
							}
						} else {
							freeX.add(nx);
							freeY.add(ny);
						}
					}
					if (crtID >= 0) {
						if (runLength > bestValue[crtID] && !freeX.isEmpty()
								&& !freeY.isEmpty()) {
							bestValue[crtID] = runLength;
							bestX[crtID] = getRandomElement(freeX);
							bestY[crtID] = getRandomElement(freeY);
						}
					}

					crtID = -1;
					runLength = 0;
					freeX.clear();
					freeY.clear();
					for (int k = 0; k < 5; k++) {
						int nx = i;
						int ny = j + k;
						if (ny >= columns) {
							crtID = -1;
							break;
						}
						Piece piece = grid[nx][ny];
						if (piece != null) {
							int pieceID = piece.getColor().getID();
							int realID = pieceID ^ playerID;
							if (crtID == -1) {
								crtID = realID;
							}
							if (realID == crtID) {
								runLength++;
							} else {
								break;
							}
						} else {
							freeX.add(nx);
							freeY.add(ny);
						}
					}
					if (crtID >= 0) {
						if (runLength > bestValue[crtID] && !freeX.isEmpty()
								&& !freeY.isEmpty()) {
							bestValue[crtID] = runLength;
							bestX[crtID] = getRandomElement(freeX);
							bestY[crtID] = getRandomElement(freeY);
						}
					}

					crtID = -1;
					runLength = 0;
					freeX.clear();
					freeY.clear();
					for (int k = 0; k < 5; k++) {
						int nx = i + k;
						int ny = j + k;
						if (nx >= rows || ny >= columns) {
							crtID = -1;
							break;
						}
						Piece piece = grid[nx][ny];
						if (piece != null) {
							int pieceID = piece.getColor().getID();
							int realID = pieceID ^ playerID;
							if (crtID == -1) {
								crtID = realID;
							}
							if (realID == crtID) {
								runLength++;
							} else {
								break;
							}
						} else {
							freeX.add(nx);
							freeY.add(ny);
						}
					}
					if (crtID >= 0) {
						if (runLength > bestValue[crtID] && !freeX.isEmpty()
								&& !freeY.isEmpty()) {
							bestValue[crtID] = runLength;
							bestX[crtID] = getRandomElement(freeX);
							bestY[crtID] = getRandomElement(freeY);
						}
					}

					crtID = -1;
					runLength = 0;
					freeX.clear();
					freeY.clear();
					for (int k = 0; k < 5; k++) {
						int nx = i + k;
						int ny = j - k;
						if (nx >= rows || ny < 0) {
							crtID = -1;
							break;
						}
						Piece piece = grid[nx][ny];
						if (piece != null) {
							int pieceID = piece.getColor().getID();
							int realID = pieceID ^ playerID;
							if (crtID == -1) {
								crtID = realID;
							}
							if (realID == crtID) {
								runLength++;
							} else {
								break;
							}
						} else {
							freeX.add(nx);
							freeY.add(ny);
						}
					}
					if (crtID >= 0) {
						if (runLength > bestValue[crtID] && !freeX.isEmpty()
								&& !freeY.isEmpty()) {
							bestValue[crtID] = runLength;
							bestX[crtID] = getRandomElement(freeX);
							bestY[crtID] = getRandomElement(freeY);
						}
					}
				}

			double[][] output = new double[rows][columns];
			if (bestValue[0] >= 0 && bestValue[0] >= bestValue[1]) {
				output[bestX[0]][bestY[0]] = 1.0;
			} else if (bestValue[1] >= 0 && bestValue[1] > bestValue[0]) {
				output[bestX[1]][bestY[1]] = 1.0;
			} else {
				for (int i = 0; i < rows; i++)
					for (int j = 0; j < columns; j++) {
						if (grid[i][j] == null)
							output[i][j] = (1.0 + random.nextDouble()) / 2;
					}
			}

			/*
			 * int[][] horizontal = new int[rows][2]; int[][] vertical = new
			 * int[columns][2]; for (int i = 0; i < rows; i++) for (int j = 0; j
			 * < columns; j++) { Piece piece = grid[i][j]; if (piece != null) {
			 * int pieceID = piece.getColor().getID(); int realID = pieceID ^
			 * playerID; horizontal[i][realID]++; vertical[j][realID]++; } }
			 * 
			 * double[][] output = new double[rows][columns]; for (int i = 0; i
			 * < rows; i++) for (int j = 0; j < columns; j++) { int r = 0; if
			 * (grid[i][j] == null) { int p = Math.abs(horizontal[i][0] -
			 * horizontal[i][1]); int q = Math.abs(vertical[j][0] -
			 * vertical[j][1]); r = Math.max(p, q); } output[i][j] =
			 * Utils.sigmoid(r); }
			 */
			return output;
		} else {
			return new double[0][0];
		}
	}

	public HeuristicIndividual[] crossover(HeuristicIndividual other) {
		return new HeuristicIndividual[] { this, other };
	}

	private int getRandomElement(List<Integer> list) {
		if (list != null)
			return list.get(random.nextInt(list.size()));
		else
			throw new NullPointerException("list cannot be null");
	}

}
