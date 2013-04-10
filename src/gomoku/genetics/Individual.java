package gomoku.genetics;

import gomoku.graphics.Piece;

public abstract class Individual<T extends Individual<T>> implements
		Comparable<T> {

	protected double fitness;

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public abstract T[] crossover(T other);

	public abstract void mutate(int percent);

	public abstract double[][] getOutput(Piece[][] grid, Piece.Color player);

	public int compareTo(T other) {
		return new Double(other.getFitness()).compareTo(getFitness());
	}

}
