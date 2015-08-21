package es.bimgam.ld33.states;

public class State {

	private String name;

	public StateManager manager;

	State(String name) {
		this.name = name;
		this.manager = null;
	}

	public String getName() {
		return this.name;
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
