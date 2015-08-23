package es.bimgam.ld33.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.bimgam.ld33.core.Debug;

import java.util.HashMap;

public class StateManager {
	private HashMap<String, Class<? extends State>> states = new HashMap<String, Class<? extends State>>();

	private State activeState;

	public static StateManager Instance = null;

	private Stage stage;
	private Skin skin;

	private class PendingStateChange {
		public State newState;

		public PendingStateChange(State state) {
			this.newState = state;
		}
	}
	private PendingStateChange pendingStateChange;

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

	public boolean setActiveState(String stateName) {
		if (this.pendingStateChange != null) {
			Debug.Log("State change in this moment is not possible");
			return false;
		}

		if (! this.states.containsKey(stateName)) {
			return false;
		}

		try {
			State newState = this.states.get(stateName).getConstructor(StateManager.class, Stage.class, Skin.class).newInstance(this, stage, skin);
			this.pendingStateChange = new PendingStateChange(newState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean setActiveState(String stateName, HashMap<String, Object> params) {
		if (this.pendingStateChange != null) {
			Debug.Log("State change in this moment is not possible");
			return false;
		}

		if (! this.states.containsKey(stateName)) {
			return false;
		}

		try {
			State newState = this.states.get(stateName).getConstructor(StateManager.class, Stage.class, Skin.class).newInstance(this, stage, skin);
			newState.setParams(params);
			this.pendingStateChange = new PendingStateChange(newState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void tick(float deltaTime) {
		if (this.pendingStateChange != null) {
			if (this.activeState != null) {
				this.activeState.deactivate();
			}
			this.activeState = this.pendingStateChange.newState;
			this.pendingStateChange = null;
			if (this.activeState != null) {
				this.activeState.activate();
			}
		}

		if (this.activeState != null) {
			this.activeState.tick(deltaTime);
		}
	}

	public void render() {
		if (this.activeState != null) {
			this.activeState.render();
		}
	}

	public void resize(int width, int height) {
		if (this.activeState != null) {
			this.activeState.resize(width, height);
		}
	}

	public <T extends State> T getActiveStateSafe(Class<T> checkClass) {
		if (this.activeState != null && this.activeState.getClass() == checkClass) {
			return (T) this.activeState;
		}
		return null;
	}
}
