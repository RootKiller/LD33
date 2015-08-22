package es.bimgam.ld33.states;

public class State {

	public StateManager manager;

	State() {
		this.manager = null;
	}

	public String getName() {
		return "Base state";
	}

	public void activate() {
	}

	public void deactivate() {
	}

	public void render() {
	}

	public void tick(float deltaTime) {
	}
}
