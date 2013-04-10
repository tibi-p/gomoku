package gomoku.individual.factory;

import gomoku.genetics.Individual;

public interface IndividualFactory<T extends Individual<T>> {

	public T createIndividual();

}
