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
		this.bindPool = new BindPool();
		this.commandManager = new CommandManager();
		this.assetsManager = new AssetManager();
		this.stateManager = new StateManager();
		registerStates();

		this.stateManager.setActiveState("MenuState");

		this.commandManager.register(new QuitCommand());

		this.bindPool.register(new Bind(Input.Keys.ESCAPE, true, "quit"));
	}

	@Override
	public void render () {
		this.assetsManager.update();

		this.stateManager.tick(Gdx.graphics.getDeltaTime());
		this.bindPool.tick();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.stateManager.render();
	}

	@Override
	public void dispose () {
		this.stateManager.release();
		this.assetsManager.dispose();
	}

	public AssetManager getAssetsManager() {
		return this.assetsManager;
	}

	private void registerStates() {
		this.stateManager.register(new MenuState());
	}
}
