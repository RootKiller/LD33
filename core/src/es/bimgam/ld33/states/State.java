package es.bimgam.ld33.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class State {

	public StateManager manager;
	public Stage stage;
	public Skin skin;

	public State(StateManager manager, Stage stage, Skin skin) {
		this.manager = manager;
		this.stage = stage;
		this.skin = skin;
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
