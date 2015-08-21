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
		if (this.activeState != null) {
			this.activeState.deactivate();
			this.activeState = null;
		}
		this.states.clear();

		Instance = null;
	}

	public void register(State state) {
		state.manager = this;
		this.states.put(state.getName(), state);
	}

	public boolean setActiveState(String stateName) {
		if (! this.states.containsKey(stateName)) {
			return false;
		}

		if (this.activeState != null) {
			this.activeState.deactivate();
		}
		this.activeState = this.states.get(stateName);
		if (this.activeState != null) {
			this.activeState.activate();
		}
		return true;
	}

	public void tick(float deltaTime) {
		if (this.activeState != null) {
			this.activeState.tick(deltaTime);
		}
	}

	public void render() {
		if (this.activeState != null) {
			this.activeState.render();
		}
	}
}
