
public class StateManager {
	private State firstState;
	
	/*public static void main(String[] args) {
		StateManager manager = new StateManager();
		manager.run();
	}
	
	public void run() {
		int input = 0;
		while (input != -1) {
			input = Integer.parseInt(System.console().readLine());
			State state = new State(input);
			if (threefoldRepetitionCheck(state)) {
				printStates();
				System.out.println("Yes");
				break;
			}
			
			printStates();
		}
	}*/
	
	public void addState(State state) {
		if (firstState == null) firstState = state;
		else {
			State current = firstState;
			while (current.getNextState() != null) {
				current = current.getNextState();
			}
			current.setNextState(state);
		}
	}
	
	public boolean threefoldRepetitionCheck(State state) {
		if (firstState == null) firstState = state;
		else {
			int counter = 0;
			
			if (state.getState() == firstState.getState()) counter++;
			
			if(state.getState() < firstState.getState()) {
				state.setNextState(firstState);
				firstState = state;
			}
			else {
				State current = firstState, next = current.getNextState();
				
				while (next != null) {
					if (state.getState() == next.getState()) {
						counter++;
						if (counter == 2) return true;
					}
					else if (state.getState() < next.getState()) {
						current.setNextState(state);
						state.setNextState(next);
						return false;
					}
					
					current = current.getNextState();
					next = current.getNextState();
				}
				current.setNextState(state);
			}
		}
		return false;
	}

	public void printStates() {
		State current = firstState;
		while (current != null) {
			System.out.print(current.getState()+ ", ");
			current = current.getNextState();
		}
		System.out.println();
	}
	
}
