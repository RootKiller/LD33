package es.bimgam.ld33.input;

import com.badlogic.gdx.Gdx;
import es.bimgam.ld33.core.CommandManager;

public class Bind {
	private int key;
	private boolean down;
	private boolean hasCommand;
	private String command;

	public Bind(int key, boolean down) {
		this.key = key;
		this.down = down;
		this.hasCommand = false;
	}

	public Bind(int key, boolean down, String command) {
		this.key = key;
		this.down = down;
		this.hasCommand = true;
		this.command = command;
	}

	public void tick() {
		if (! this.hasCommand) {
			return;
		}

		if (isActive()) {
			CommandManager.Instance.execute(this.command);
		}
	}

	public boolean isActive() {
		boolean isPressed = Gdx.input.isKeyPressed(this.key);
		return this.down ? isPressed : !isPressed;
	}
}
