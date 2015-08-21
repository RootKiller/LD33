package es.bimgam.ld33;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import es.bimgam.ld33.input.Bind;
import es.bimgam.ld33.input.BindPool;

import es.bimgam.ld33.core.CommandManager;

import es.bimgam.ld33.states.MenuState;

import es.bimgam.ld33.states.StateManager;


public class LD33 extends ApplicationAdapter {
	private CommandManager commandManager;
	private BindPool bindPool;

	private AssetManager assetsManager;

	private StateManager stateManager;

	public static LD33 Instance = null;

	public LD33() {
		Instance = this;
	}

	@Override
	public void create () {
		bindPool = new BindPool();
		commandManager = new CommandManager();
		assetsManager = new AssetManager();
		stateManager = new StateManager();
		registerStates();

		stateManager.setActiveState("MenuState");

		commandManager.register(new QuitCommand());

		bindPool.register(new Bind(Input.Keys.ESCAPE, true, "quit"));
	}

	@Override
	public void render () {
		assetsManager.update();

		stateManager.tick(Gdx.graphics.getDeltaTime());
		bindPool.tick();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stateManager.render();
	}

	@Override
	public void dispose () {
		stateManager.release();
		assetsManager.dispose();
	}

	public AssetManager getAssetsManager() {
		return assetsManager;
	}

	private void registerStates() {
		stateManager.register(new MenuState());
	}
}
