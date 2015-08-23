package es.bimgam.ld33.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.bimgam.ld33.core.Debug;

import java.util.HashMap;

public class State {

	public StateManager manager;
	public Stage stage;
	public Skin skin;

	protected HashMap<String, Object> params;

	public State(StateManager manager, Stage stage, Skin skin) {
		this.manager = manager;
		this.stage = stage;
		this.skin = skin;
	}

	public void setParams(HashMap<String, Object> params) {
		Debug.Assert(this.params == null, "Params are not null!");
		this.params = params;
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

	public void resize(int width, int height) {
	}
}
