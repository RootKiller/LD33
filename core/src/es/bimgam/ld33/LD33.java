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

import es.bimgam.ld33.graphics.Font;


public class LD33 extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;

	private Graphics graphics;

	private CommandManager commandManager;
	private BindPool bindPool;
	private Font font1;
	private Font font2;

	private AssetManager assetsManager;

	public static LD33 Instance = null;

	public LD33() {
		Instance = this;
	}

	@Override
	public void create () {
		bindPool = new BindPool();
		commandManager = new CommandManager();
		batch = new SpriteBatch();
		assetsManager = new AssetManager();
		assetsManager.load("interface/horny_peppers_logo.png", Texture.class);
		graphics = Gdx.graphics;
		font1 = new Font("fonts/segoepr.ttf", 20);
		font2 = new Font("fonts/arial.ttf", 25);

		commandManager.register(new QuitCommand());

		bindPool.register(new Bind(Input.Keys.ESCAPE, true, "quit"));
	}

	@Override
	public void render () {
		if (assetsManager.update()) {
			img = assetsManager.get("interface/horny_peppers_logo.png");
		}
		bindPool.tick();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (img != null) {
			batch.begin();
			batch.draw(img, 0, 0, graphics.getWidth(), graphics.getHeight());
			font1.draw(batch, "Test żaźbćcłdóeśfągń", 20, 20);
			font2.draw(batch, "Kolejny testowy tekst", 350, 20);
			batch.end();
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		assetsManager.dispose();
		font1.dispose();
		font2.dispose();
	}

	public AssetManager getAssetsManager() {
		return assetsManager;
	}
}
