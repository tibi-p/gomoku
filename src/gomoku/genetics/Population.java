package gomoku.genetics;

import gomoku.individual.factory.IndividualFactory;
import gomoku.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Population<T extends Individual<T>> {

	private List<T> generation;
	private double[] weights;
	private double susSeed;
	private double susRatio;

	public Population(int numIndividuals, IndividualFactory<T> factory) {
		generation = new ArrayList<T>();
		for (int i = 0; i < numIndividuals; i++) {
			generation.add(factory.createIndividual());
		}
		weights = new double[numIndividuals + 1];
		susRatio = 1. / (numIndividuals / 2);
	}

	public Population(List<T> generation) {
		if (generation != null) {
			this.generation = new ArrayList<T>(generation);
			int numIndividuals = generation.size();
			weights = new double[numIndividuals + 1];
			susRatio = 1. / (numIndividuals / 2);
		} else {
			throw new NullPointerException();
		}
	}

	public List<T> getGeneration() {
		return generation;
	}

	public int getNumIndividuals() {
		return generation.size();
	}

	public T getIndividual(int index) {
		return generation.get(index);
	}

	public void sortGeneration() {
		Collections.sort(generation);
	}

	public void changeGeneration() {
		sortGeneration();
		List<T> nextGeneration = new ArrayList<T>();

		weights[0] = generation.get(0).getFitness();
		for (int i = 1; i < generation.size(); i++)
			weights[i] = weights[i - 1] + generation.get(i).getFitness();
		for (int i = 0; i < generation.size(); i++)
			weights[i] /= weights[generation.size() - 1];

		susSeed = Utils.random.nextDouble() * susRatio;
		boolean done = false;
		while (!done) {
			// NeuralNetwork father = getRandomFitIndividual();
			// NeuralNetwork mother = getRandomFitIndividual();
			T father = getRandomRouletteIndividual();
			T mother = getRandomRouletteIndividual();
			// NeuralNetwork father = getRandomSusIndividual();
			// NeuralNetwork mother = getRandomSusIndividual();
			T[] children = father.crossover(mother);
			for (T child : children) {
				nextGeneration.add(child);
				if (nextGeneration.size() == generation.size()) {
					done = true;
					break;
				}
			}
		}
		generation = nextGeneration;
		for (T individual : generation)
			individual.mutate(1);
	}

	private T getRandomFitIndividual() {
		int index = Utils.random.nextInt(generation.size() / 2);
		return generation.get(index);
	}

	private T getRandomRouletteIndividual() {
		double x = Utils.random.nextDouble();
		int idx = Arrays.binarySearch(weights, x);
		if (idx < 0) {
			idx = -idx - 1;
			if (idx == generation.size())
				idx--;
		}
		return generation.get(idx);

	}

	private T getRandomSusIndividual() {
		int index = Utils.random.nextInt(generation.size() / 2);
		return getRouletteIndividual(susSeed + index * susRatio);
	}

	private T getRouletteIndividual(double x) {
		int idx = Arrays.binarySearch(weights, x);
		if (idx < 0) {
			idx = -idx - 1;
			if (idx == generation.size())
				idx--;
		}
		return generation.get(idx);

	}

}
