
public class State {
	private final double state;
	private State nextState;
	
	public State(double state) {
		this.state = state;
	}
	
	public double getState() {
		return state;
	}
	
	public void setNextState(State newState) {
		nextState = newState;
	}
	
	public State getNextState() {
		return nextState;
	}

}
