package gomoku.individual.factory;

import gomoku.individual.NeuralNetwork;

public class NeuralNetworkFactory implements IndividualFactory<NeuralNetwork> {

	private final int networkSize;

	public NeuralNetworkFactory(int networkSize) {
		this.networkSize = networkSize;
	}

	public NeuralNetwork createIndividual() {
		NeuralNetwork nn = new NeuralNetwork(networkSize);
		nn.initRandom();
		return nn;
	}

}
