package gomoku.individual.factory;

import gomoku.individual.RandomIndividual;

public class RandomIndividualFactory implements
		IndividualFactory<RandomIndividual> {

	public RandomIndividual createIndividual() {
		return new RandomIndividual();
	}

}
