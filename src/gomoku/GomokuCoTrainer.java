package gomoku;

import gomoku.game.WorldModel;
import gomoku.genetics.Individual;
import gomoku.genetics.Population;
import gomoku.graphics.Piece;
import gomoku.individual.HeuristicIndividual;
import gomoku.individual.NeuralNetwork;
import gomoku.individual.factory.HeuristicIndividualFactory;
import gomoku.individual.factory.IndividualFactory;
import gomoku.individual.factory.NeuralNetworkFactory;

import java.util.ArrayList;
import java.util.List;

public class GomokuCoTrainer {

	private final WorldModel model;
	private final int numNetworks;
	private Population<NeuralNetwork> host;
	private Population<NeuralNetwork> parasite;
	private Population<HeuristicIndividual> hallOfFame;
	private List<List<Integer>> hostWins = new ArrayList<List<Integer>>();
	private List<List<Integer>> hostLosses = new ArrayList<List<Integer>>();
	private List<List<Integer>> parasiteWins = new ArrayList<List<Integer>>();
	private List<List<Integer>> parasiteLosses = new ArrayList<List<Integer>>();

	public GomokuCoTrainer(WorldModel model,
			List<NeuralNetwork> hostGeneration, int numNetworks, int networkSize) {
		this.model = model;
		this.numNetworks = numNetworks;
		host = new Population<NeuralNetwork>(hostGeneration);
		IndividualFactory<NeuralNetwork> nnFactory = new NeuralNetworkFactory(
				networkSize);
		// parasite = new Population<NeuralNetwork>(numNetworks, nnFactory);
		parasite = new Population<NeuralNetwork>(hostGeneration);
		IndividualFactory<HeuristicIndividual> hFactory = new HeuristicIndividualFactory();
		hallOfFame = new Population<HeuristicIndividual>(10, hFactory);
	}

	public NeuralNetwork train(int iterations) {
		for (int i = 0; i < iterations; i++) {
			System.out.println("Co Iteration: " + i);
			computeFitness();
			System.out.println();
			host.changeGeneration();
			parasite.changeGeneration();
			swapPopulations();
		}
		int maxSize = Integer.MIN_VALUE;
		NeuralNetwork bestIndividual = null;
		for (int i = 0; i < numNetworks; i++) {
			int size = hostWins.get(i).size();
			if (size > maxSize) {
				maxSize = size;
				bestIndividual = host.getIndividual(i);
			}
		}
		return bestIndividual;
	}

	private <S> void fillListOfLists(List<List<S>> list, int size) {
		list.clear();
		for (int i = 0; i < size; i++)
			list.add(new ArrayList<S>());
	}

	public void computeFitness() {
		int numHosts = host.getNumIndividuals();
		int numParasites = parasite.getNumIndividuals();
		int numFamous = hallOfFame.getNumIndividuals();
		int numWins = 0;
		int numDraws = 0;
		int numLosses = 0;

		fillListOfLists(hostWins, numHosts);
		fillListOfLists(hostLosses, numHosts);
		fillListOfLists(parasiteWins, numParasites + numFamous);
		fillListOfLists(parasiteLosses, numParasites + numFamous);

		for (int i = 0; i < numHosts; i++)
			for (int j = 0; j < numParasites; j++) {
				int result = playGame(host.getIndividual(i),
						parasite.getIndividual(j));
				if (result > 0) {
					numWins++;
					hostWins.get(i).add(j);
					parasiteLosses.get(j).add(i);
				} else if (result == 0) {
					numDraws++;
				} else {
					numLosses++;
					hostLosses.get(i).add(j);
					parasiteWins.get(j).add(i);
				}
			}

		for (int i = 0; i < numHosts; i++)
			for (int j = 0; j < numFamous; j++) {
				int result = playGame(host.getIndividual(i),
						hallOfFame.getIndividual(j));
				if (result > 0) {
					numWins++;
					hostWins.get(i).add(j);
					parasiteLosses.get(numParasites + j).add(i);
				} else if (result == 0) {
					numDraws++;
				} else {
					numLosses++;
					hostLosses.get(i).add(numParasites + j);
					parasiteWins.get(numParasites + j).add(i);
				}
			}

		for (int i = 0; i < numHosts; i++) {
			double s = 0.0;
			for (Integer j : hostWins.get(i))
				s += 1. / parasiteLosses.get(j).size();
			host.getIndividual(i).setFitness(s);
		}
		for (int j = 0; j < numParasites; j++) {
			double s = 0.0;
			for (Integer i : parasiteWins.get(j))
				s += 1. / hostLosses.get(i).size();
			parasite.getIndividual(j).setFitness(s);
		}
		for (int j = 0; j < numFamous; j++) {
			double s = 0.0;
			for (Integer i : parasiteWins.get(numParasites + j))
				s += 1. / hostLosses.get(i).size();
			hallOfFame.getIndividual(j).setFitness(s);
		}
		System.out.println("Wins: " + numWins);
		System.out.println("Draws: " + numDraws);
		System.out.println("Losses: " + numLosses);
	}

	public int playGame(Individual<?> x, Individual<?> y) {
		model.clearBoard();
		List<MoveEntry> history = new ArrayList<MoveEntry>();
		while (true) {
			Piece.Color player = model.getCurrentPlayer();
			Individual<?> nn = (player == Piece.Color.BLACK) ? x : y;
			MoveEntry entry = model.playMove(nn, player);
			if (entry != null)
				history.add(entry);
			else
				break;
		}
		// System.out.println(history);
		Piece.Color winner = model.getWinner();
		if (winner == Piece.Color.BLACK)
			return 1;
		else if (winner == Piece.Color.WHITE)
			return -1;
		else
			return 0;
	}

	private void swapPopulations() {
		Population<NeuralNetwork> swp = host;
		host = parasite;
		parasite = swp;
	}

}
