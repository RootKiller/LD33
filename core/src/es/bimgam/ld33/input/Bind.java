package es.bimgam.ld33.input;

import com.badlogic.gdx.Gdx;
import es.bimgam.ld33.core.CommandManager;

public class Bind {
	private int key;
	private boolean down;
	private String command;
	private Runnable runnable;
	private boolean isActive;

	public Bind(int key, boolean down) {
		this.key = key;
		this.down = down;
	}

	public Bind(int key, boolean down, String command) {
		this.key = key;
		this.down = down;
		this.command = command;
	}

	public Bind(int key, boolean down, Runnable runnable) {
		this.key = key;
		this.down = down;
		this.runnable = runnable;
	}

	public void tick() {
		boolean isPressed = Gdx.input.isKeyJustPressed(this.key);
		isActive = this.down ? isPressed : !isPressed;

		if (isActive) {
			if (this.command.length() > 0) {
				CommandManager.Instance.execute(this.command);
			}

			if (this.runnable != null) {
				this.runnable.run();
			}
		}
	}

	public boolean isActive() {
		return this.isActive;
	}
}
