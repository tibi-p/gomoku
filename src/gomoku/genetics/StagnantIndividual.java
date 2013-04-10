package gomoku.genetics;

public abstract class StagnantIndividual<T extends StagnantIndividual<T>>
		extends Individual<T> {

	public void mutate(int percent) {
	}

}
