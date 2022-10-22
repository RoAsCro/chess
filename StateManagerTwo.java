
public class StateManagerTwo {

	private StateTwo firstState;
	
	
	public void addState(StateTwo state) {
		if (firstState == null) firstState = state;
		else {
			StateTwo current = firstState;
			while (current.getNextState() != null) {
				current = current.getNextState();
			}
			current.setNextState(state);
		}
	}
	
	public boolean threefoldRepetitionCheck(StateTwo state) {
		if (firstState == null) firstState = state;
		else {
			int counter = 0;
			
			if (state.getState().equals(firstState.getState())) counter++;
			
			/*if(state.getState() < firstState.getState()) {
				state.setNextState(firstState);
				firstState = state;
			}*/
			StateTwo current = firstState, next = current.getNextState();
			
			while (next != null) {
				if (state.getState().equals(next.getState())) {
					counter++;
					if (counter == 2) return true;
				}
				/*else if (state.getState() < next.getState()) {
					current.setNextState(state);
					state.setNextState(next);
					return false;
				}*/
				
				current = current.getNextState();
				next = current.getNextState();
			}
			current.setNextState(state);
		}
		return false;
	}
	
	public void clear() {
		firstState = null;
	}

	public void printStates() {
		StateTwo current = firstState;
		while (current != null) {
			System.out.print(current.getState()+ ", ");
			current = current.getNextState();
		}
		System.out.println();
	}

	
}
