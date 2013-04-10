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

public class GomokuFixedTrainer {

	private final WorldModel model;
	private final int numNetworks;
	private Population<NeuralNetwork> host;
	private Population<?> parasite;
	private List<List<Integer>> hostWins = new ArrayList<List<Integer>>();
	private List<List<Integer>> hostLosses = new ArrayList<List<Integer>>();
	private List<List<Integer>> parasiteWins = new ArrayList<List<Integer>>();
	private List<List<Integer>> parasiteLosses = new ArrayList<List<Integer>>();

	public GomokuFixedTrainer(WorldModel model, int numNetworks,
			int networkSize, int numHeuristics) {
		this.model = model;
		this.numNetworks = numNetworks;
		IndividualFactory<NeuralNetwork> nnFactory = new NeuralNetworkFactory(
				networkSize);
		host = new Population<NeuralNetwork>(numNetworks, nnFactory);
		IndividualFactory<HeuristicIndividual> hFactory = new HeuristicIndividualFactory();
		parasite = new Population<HeuristicIndividual>(numHeuristics, hFactory);
		// IndividualFactory<RandomIndividual> rFactory = new
		// RandomIndividualFactory();
		// parasite = new Population<RandomIndividual>(numHeuristics, rFactory);
	}

	public int getNumNetworks() {
		return numNetworks;
	}

	public List<NeuralNetwork> train(int iterations) {
		for (int i = 0; i < iterations; i++) {
			System.out.println("Fixed Iteration: " + i);
			computeFitness();
			System.out.println();
			host.changeGeneration();
		}
		host.sortGeneration();
		return host.getGeneration();
	}

	private <S> void fillListOfLists(List<List<S>> list, int size) {
		list.clear();
		for (int i = 0; i < size; i++)
			list.add(new ArrayList<S>());
	}

	public void computeFitness() {
		int numHosts = host.getNumIndividuals();
		int numParasites = parasite.getNumIndividuals();
		int numWins = 0;
		int numDraws = 0;
		int numLosses = 0;

		fillListOfLists(hostWins, numHosts);
		fillListOfLists(hostLosses, numHosts);
		fillListOfLists(parasiteWins, numParasites);
		fillListOfLists(parasiteLosses, numParasites);

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

}
