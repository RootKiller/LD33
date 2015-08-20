package es.bimgam.ld33;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import es.bimgam.ld33.input.Bind;

public class LD33 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	Graphics graphics;

	private Bind exitGameBind = new Bind(Input.Keys.ESCAPE, true);

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("interface/horny_peppers_logo.png");
		graphics = Gdx.graphics;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0, graphics.getWidth(), graphics.getHeight());
		batch.end();

		if (exitGameBind.isActive()) {
			Gdx.app.exit();
		}
	}
}
