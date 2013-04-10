package gomoku;

public class MoveEntry implements Comparable<MoveEntry> {

	public double probability;
	public int row, col;

	public MoveEntry(double probability, int row, int col) {
		this.probability = probability;
		this.row = row;
		this.col = col;
	}

	public boolean equals(Object obj) {
		if (obj instanceof MoveEntry) {
			MoveEntry other = (MoveEntry) obj;
			return probability == other.probability && row == other.row
					&& col == other.col;
		} else {
			return false;
		}
	}

	public int compareTo(MoveEntry other) {
		if (probability != other.probability)
			return new Double(other.probability).compareTo(probability);
		if (row != other.row)
			return new Integer(row).compareTo(other.row);
		return new Integer(col).compareTo(other.col);
	}

	@Override
	public String toString() {
		return String.format("(%d,%d): %g", row, col, probability);
	}

}
