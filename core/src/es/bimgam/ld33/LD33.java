package es.bimgam.ld33;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import es.bimgam.ld33.input.Bind;
import es.bimgam.ld33.input.BindPool;

import es.bimgam.ld33.core.CommandManager;

public class LD33 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	Graphics graphics;

	private CommandManager commandManager;
	private BindPool bindPool;

	@Override
	public void create () {
		bindPool = new BindPool();
		commandManager = new CommandManager();
		batch = new SpriteBatch();
		img = new Texture("interface/horny_peppers_logo.png");
		graphics = Gdx.graphics;

		commandManager.register(new QuitCommand());

		bindPool.register(new Bind(Input.Keys.ESCAPE, true, "quit"));
	}

	@Override
	public void render () {
		// TODO: Find better place to handle all the non-graphical stuffs
		bindPool.tick();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0, graphics.getWidth(), graphics.getHeight());
		batch.end();
	}
}
