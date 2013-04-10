package gomoku.game;

import gomoku.MoveEntry;
import gomoku.genetics.Individual;
import gomoku.graphics.Piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class WorldModel {

	private Piece[][] grid;
	private List<Piece> pieces = new ArrayList<>();
	private int rows, columns;
	private Piece.Color currentPlayer = Piece.Color.BLACK;

	public WorldModel(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		grid = new Piece[rows - 1][columns - 1];
	}

	public Piece[][] getGrid() {
		return grid;
	}

	public Piece.Color getCurrentPlayer() {
		return currentPlayer;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public boolean addPiece(Piece piece) {
		int x = piece.getX();
		int y = piece.getY();
		if (grid[x][y] == null && piece.getColor() == currentPlayer) {
			pieces.add(piece);
			grid[x][y] = piece;
			currentPlayer = currentPlayer.flipped();
			return true;
		} else {
			return false;
		}
	}

	public int rows() {
		return rows;
	}

	public int columns() {
		return columns;
	}

	public boolean isInGrid(Point p) {
		return p.x >= 0 && p.x < rows && p.y >= 0 && p.y < columns;
	}

	public Piece.Color getWinner() {
		for (int i = 0; i < rows - 1; i++)
			for (int j = 0; j < columns - 1; j++) {
				Piece piece = grid[i][j];
				if (piece != null) {
					Piece.Color player = piece.getColor();
					if (winsVertically(player, i, j))
						return player;

					if (winsHorizontally(player, i, j))
						return player;

					if (winsFirstDiagonal(player, i, j))
						return player;

					if (winsSecondDiagonal(player, i, j))
						return player;
				}
			}
		return null;
	}

	public void clearBoard() {
		for (Piece piece : pieces)
			grid[piece.getX()][piece.getY()] = null;
		pieces.clear();
		currentPlayer = Piece.Color.BLACK;
	}

	public MoveEntry playMove(Piece.Color player, double[][] output) {
		List<MoveEntry> entries = new ArrayList<MoveEntry>();
		for (int i = 0; i < output.length; i++)
			for (int j = 0; j < output[i].length; j++) {
				entries.add(new MoveEntry(output[i][j], i, j));
			}
		PriorityQueue<MoveEntry> queue = new PriorityQueue<MoveEntry>(entries);
		MoveEntry entry;
		while ((entry = queue.poll()) != null) {
			if (addPiece(new Piece(entry.row, entry.col, player)))
				break;
		}
		return entry;
	}

	public MoveEntry playMove(Individual<?> individual, Piece.Color player) {
		double[][] output = individual.getOutput(grid, player);
		return playMove(player, output);
	}

	private boolean winsVertically(Piece.Color player, int i, int j) {
		if (i + 4 < rows - 1) {
			for (int k = 1; k < 5; k++) {
				Piece nextPiece = grid[i + k][j];
				if (nextPiece == null || !player.equals(nextPiece.getColor())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean winsHorizontally(Piece.Color player, int i, int j) {
		if (j + 4 < columns - 1) {
			for (int k = 1; k < 5; k++) {
				Piece nextPiece = grid[i][j + k];
				if (nextPiece == null || !player.equals(nextPiece.getColor())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean winsFirstDiagonal(Piece.Color player, int i, int j) {
		if (i + 4 < rows - 1 && j + 4 < columns - 1) {
			for (int k = 1; k < 5; k++) {
				Piece nextPiece = grid[i + k][j + k];
				if (nextPiece == null || !player.equals(nextPiece.getColor())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean winsSecondDiagonal(Piece.Color player, int i, int j) {
		if (i + 4 < rows - 1 && j - 4 >= 0) {
			for (int k = 1; k < 5; k++) {
				Piece nextPiece = grid[i + k][j - k];
				if (nextPiece == null || !player.equals(nextPiece.getColor())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

}
