package gomoku.individual.factory;

import gomoku.individual.HeuristicIndividual;

public class HeuristicIndividualFactory implements
		IndividualFactory<HeuristicIndividual> {

	public HeuristicIndividual createIndividual() {
		return new HeuristicIndividual();
	}

}
