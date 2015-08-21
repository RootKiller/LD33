package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import es.bimgam.ld33.LD33;
import es.bimgam.ld33.graphics.Font;

public class MenuState extends State {

	private Graphics graphics = null;

	private AssetManager assetsManager = null;

	private Texture logo = null;
	private Font font1 = null;
	private Font font2 = null;

	private SpriteBatch batch = null;

	private static final String HORNY_PEPPERS_LOGO = "interface/horny_peppers_logo.png";

	public MenuState() {
		super("MenuState");

		assetsManager = LD33.Instance.getAssetsManager();
	}

	@Override
	public void activate() {
		assetsManager.load(HORNY_PEPPERS_LOGO, Texture.class);
		graphics = Gdx.graphics;
		font1 = new Font("fonts/segoepr.ttf", 20);
		font2 = new Font("fonts/arial.ttf", 25);

		batch = new SpriteBatch();
	}

	@Override
	public void deactivate() {
		font1.dispose();
		font1 = null;

		font2.dispose();
		font2 = null;

		assetsManager.unload(HORNY_PEPPERS_LOGO);
		logo = null;

		batch.dispose();
		batch = null;
	}

	@Override
	public void render() {
		if (logo == null && assetsManager.isLoaded(HORNY_PEPPERS_LOGO)) {
			logo = assetsManager.get(HORNY_PEPPERS_LOGO);
		}

		batch.begin();
		if (logo != null) {
			batch.draw(logo, 0, 0, graphics.getWidth(), graphics.getHeight());
		}

		font1.draw(batch, "Hello world", 0, 0);
		font2.draw(batch, "Hello font 2!", 0, font1.getRenderHeight("Hello world") + 2.0f);
		batch.end();
	}
}
