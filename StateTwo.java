
public class StateTwo {

	private final String state;
	private StateTwo nextState;
	
	public StateTwo(String state) {
		this.state = state;
	}
	
	public String getState() {
		return state;
	}
	
	public void setNextState(StateTwo newState) {
		nextState = newState;
	}
	
	public StateTwo getNextState() {
		return nextState;
	}


}
