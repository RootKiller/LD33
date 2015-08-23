package es.bimgam.ld33;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.bimgam.ld33.entities.Scene;
import es.bimgam.ld33.graphics.Font;
import es.bimgam.ld33.input.Bind;
import es.bimgam.ld33.input.BindPool;

import es.bimgam.ld33.core.CommandManager;

import es.bimgam.ld33.states.GameOverState;
import es.bimgam.ld33.states.InGameState;
import es.bimgam.ld33.states.MenuState;

import es.bimgam.ld33.states.StateManager;


public class LD33 extends ApplicationAdapter {
	private CommandManager commandManager;
	private BindPool bindPool;

	private AssetManager assetsManager;

	private StateManager stateManager;

	private Stage stage;
	private Skin skin;

	private Font uiFont;

	public static LD33 Instance = null;

	public LD33() {
		Instance = this;
	}

	@Override
	public void create () {
		this.bindPool = new BindPool();
		this.commandManager = new CommandManager();
		this.assetsManager = new AssetManager();

		this.uiFont = new Font("fonts/arial.ttf", 20);

		this.stage = new Stage();
		this.skin = new Skin();
		skin.addRegions(new TextureAtlas(Gdx.files.internal("UI/uiskin.atlas")));
		skin.add("default-font", uiFont.getBitmapFont());
		skin.load(Gdx.files.internal("UI/uiskin.json"));

		this.stateManager = new StateManager(this.stage, this.skin);

		Gdx.input.setInputProcessor(this.stage);
		registerStates();

		this.stateManager.setActiveState("MenuState");

		this.commandManager.register(new QuitCommand());

		new Bind(Input.Keys.ESCAPE, true, "quit");
		new Bind(Input.Keys.ENTER, true, new Runnable() {
			@Override
			public void run() {
			if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
				if (Gdx.graphics.isFullscreen()) {
					Gdx.graphics.setDisplayMode(1280, 720, false);
				}
				else {
					Gdx.graphics.setDisplayMode(1920, 1080, true);
				}
			}
		}
		});
	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height);
		this.stateManager.resize(width, height);
	}

	@Override
	public void render () {
		this.assetsManager.update();
		this.bindPool.tick();

		this.stateManager.tick(Gdx.graphics.getDeltaTime());
		this.stage.act(Gdx.graphics.getDeltaTime());

		this.stateManager.render();
		this.stage.draw();
	}

	@Override
	public void dispose () {
		this.stateManager.release();
		this.bindPool.release();
		this.stage.dispose();

		this.assetsManager.dispose();
	}

	public AssetManager getAssetsManager() {
		return this.assetsManager;
	}

	private void registerStates() {
		this.stateManager.register(MenuState.class);
		this.stateManager.register(InGameState.class);
		this.stateManager.register(GameOverState.class);
	}
}
