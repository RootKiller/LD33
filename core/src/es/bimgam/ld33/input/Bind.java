package es.bimgam.ld33.input;

import com.badlogic.gdx.Gdx;

/**
 * Created by Eryk on 20.08.2015.
 */
public class Bind {
	int key;
	boolean down;

	public Bind(int key, boolean down) {
		this.key = key;
		this.down = down;
	}

	public boolean isActive() {
		boolean isPressed = Gdx.input.isKeyPressed(this.key);
		return this.down ? isPressed : !isPressed;
	}
}
