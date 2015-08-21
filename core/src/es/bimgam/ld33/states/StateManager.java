package es.bimgam.ld33.states;

import java.util.HashMap;

public class StateManager {
	private HashMap<String, State> states = new HashMap<String, State>();

	private State activeState;

	public static StateManager Instance = null;

	public StateManager() {
		Instance = this;

		this.activeState = null;
	}

	public void release() {
		if (activeState != null) {
			activeState.deactivate();
			activeState = null;
		}
		states.clear();

		Instance = null;
	}

	public void register(State state) {
		state.manager = this;
		states.put(state.getName(), state);
	}

	public boolean setActiveState(String stateName) {
		if (! states.containsKey(stateName)) {
			return false;
		}

		if (activeState != null) {
			activeState.deactivate();
		}
		activeState = states.get(stateName);
		if (activeState != null) {
			activeState.activate();
		}
		return true;
	}

	public void tick(float deltaTime) {
		if (activeState != null) {
			activeState.tick(deltaTime);
		}
	}

	public void render() {
		if (activeState != null) {
			activeState.render();
		}
	}
}
