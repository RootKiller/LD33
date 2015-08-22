package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import es.bimgam.ld33.LD33;
import es.bimgam.ld33.graphics.Font;

public class MenuState extends State {

	private Graphics graphics = null;

	private AssetManager assetsManager = null;

	private Texture logo = null;
	private Font font1 = null;

	private SpriteBatch batch = null;

	private static final String HORNY_PEPPERS_LOGO = "interface/horny_peppers_logo.png";

	public MenuState(StateManager stateManager, Stage stage, Skin skin) {
		super(stateManager, stage, skin);

		this.assetsManager = LD33.Instance.getAssetsManager();
	}

	@Override
	public String getName() {
		return "MenuState";
	}

	@Override
	public void activate() {
		this.assetsManager.load(HORNY_PEPPERS_LOGO, Texture.class);
		this.graphics = Gdx.graphics;
		this.font1 = new Font("fonts/arial.ttf", 20);

		this.batch = new SpriteBatch();

		TextButton newGame = new TextButton("New game", this.skin);
		newGame.setSize(300, 50);
		newGame.setPosition(this.graphics.getWidth() / 2 - 150, this.graphics.getHeight() / 2 + 10.0f);
		this.stage.addActor(newGame);
	}

	@Override
	public void deactivate() {
		this.font1.dispose();
		this.font1 = null;

		this.assetsManager.unload(HORNY_PEPPERS_LOGO);
		this.logo = null;

		this.batch.dispose();
		this.batch = null;

		this.stage.clear();
	}

	@Override
	public void render() {
		if (this.logo == null && this.assetsManager.isLoaded(HORNY_PEPPERS_LOGO)) {
			this.logo = this.assetsManager.get(HORNY_PEPPERS_LOGO);
		}

		this.batch.begin();
		if (this.logo != null) {
			float size = this.logo.getHeight();
			if (this.logo.getHeight() > this.graphics.getHeight()) {
				size = this.graphics.getHeight();
			}
			this.batch.draw(logo, this.graphics.getWidth() / 2 - size / 2, 0, size, size);
		}

		this.font1.draw(this.batch, "FPS: " + this.graphics.getFramesPerSecond(), 2, 2);
		this.batch.end();
	}
}
