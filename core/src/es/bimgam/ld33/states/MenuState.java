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

		this.assetsManager = LD33.Instance.getAssetsManager();
	}

	@Override
	public void activate() {
		this.assetsManager.load(HORNY_PEPPERS_LOGO, Texture.class);
		this.graphics = Gdx.graphics;
		this.font1 = new Font("fonts/segoepr.ttf", 20);
		this.font2 = new Font("fonts/arial.ttf", 25);

		this.batch = new SpriteBatch();
	}

	@Override
	public void deactivate() {
		this.font1.dispose();
		this.font1 = null;

		this.font2.dispose();
		this.font2 = null;

		this.assetsManager.unload(HORNY_PEPPERS_LOGO);
		this.logo = null;

		this.batch.dispose();
		this.batch = null;
	}

	@Override
	public void render() {
		if (this.logo == null && this.assetsManager.isLoaded(HORNY_PEPPERS_LOGO)) {
			this.logo = this.assetsManager.get(HORNY_PEPPERS_LOGO);
		}

		this.batch.begin();
		if (this.logo != null) {
			this.batch.draw(logo, 0, 0, this.graphics.getWidth(), this.graphics.getHeight());
		}

		this.font1.draw(this.batch, "Hello world", 0, 0);
		this.font2.draw(this.batch, "Hello font 2!", 0, this.font1.getRenderHeight("Hello world") + 2.0f);
		this.batch.end();
	}
}
