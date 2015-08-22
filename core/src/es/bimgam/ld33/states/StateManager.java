package es.bimgam.ld33.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

public class StateManager {
	private HashMap<String, Class<? extends State>> states = new HashMap<String, Class<? extends State>>();

	private State activeState;

	public static StateManager Instance = null;

	private Stage stage;
	private Skin skin;

	public StateManager(Stage stage, Skin skin) {
		Instance = this;

		this.stage = stage;
		this.skin = skin;

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

	public void register(Class<? extends State> stateClass) {
		State tempState = null;
		try {
			tempState = stateClass.getConstructor(StateManager.class, Stage.class, Skin.class).newInstance(this, stage, skin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.states.put(tempState.getName(), stateClass);
	}

	public boolean setActiveState(Class<? extends  State> stateClass) {
		if (! this.states.containsValue(stateClass)) {
			return false;
		}

		if (this.activeState != null) {
			this.activeState.deactivate();
		}
		try {
			this.activeState = stateClass.getConstructor(StateManager.class, Stage.class, Skin.class).newInstance(this, stage, skin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (this.activeState != null) {
			this.activeState.activate();
		}
		return true;
	}

	public boolean setActiveState(String stateName) {
		if (! this.states.containsKey(stateName)) {
			return false;
		}

		if (this.activeState != null) {
			this.activeState.deactivate();
		}
		try {
			this.activeState = this.states.get(stateName).getConstructor(StateManager.class, Stage.class, Skin.class).newInstance(this, stage, skin);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
