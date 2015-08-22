package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.bimgam.ld33.LD33;

import es.bimgam.ld33.entities.Enemy;
import es.bimgam.ld33.entities.Scene;
import es.bimgam.ld33.entities.Player;
import es.bimgam.ld33.graphics.Font;

import java.util.ArrayList;

public class InGameState extends State {
	private OrthographicCamera orthoCamera;
	private SpriteBatch spriteBatch;
	private SpriteBatch hudSpriteBatch;
	private Scene scene;

	private Player player;

	private ShapeRenderer shapeRenderer;

	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	private Font hudFont;

	public InGameState(StateManager stateManager, Stage stage, Skin skin) {
		super(stateManager, stage, skin);
	}

	public String getName() {
		return "InGameState";
	}

	@Override
	public void activate() {
		this.shapeRenderer = new ShapeRenderer();
		this.hudFont = new Font("fonts/segoepr.ttf", 20);

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		this.orthoCamera = new OrthographicCamera(300.0f, 300.0f * (height / width));
		this.orthoCamera.position.set(orthoCamera.viewportWidth / 2f, orthoCamera.viewportHeight / 2f, 0);
		this.orthoCamera.update();

		this.spriteBatch = new SpriteBatch();
		this.hudSpriteBatch = new SpriteBatch();

		this.scene = new Scene(this, LD33.Instance.getAssetsManager(), this.orthoCamera);

		this.player = this.scene.createEntity("Player", Player.class);

		for (int i = 0; i < 100; ++i) {
			enemies.add(this.scene.createEntity("Enemy " + i, Enemy.class));
		}
	}

	@Override
	public void deactivate() {
		this.shapeRenderer.dispose();
		this.shapeRenderer = null;
		this.player = null;
		this.orthoCamera = null;
		this.spriteBatch = null;
		this.scene.dispose();
		this.scene = null;
		this.hudFont.dispose();
		this.hudFont = null;
		this.hudSpriteBatch.dispose();
		this.hudSpriteBatch = null;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.orthoCamera.position.set(this.player.getPosition(), 0);
		this.orthoCamera.update();
		this.spriteBatch.setProjectionMatrix(this.orthoCamera.combined);

		this.scene.debugDraw();
		this.spriteBatch.begin();
		this.scene.render(this.spriteBatch);
		this.spriteBatch.end();

		this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		this.hudSpriteBatch.begin();
		this.scene.drawHudElements(this.shapeRenderer, this.hudSpriteBatch, this.hudFont);
		this.hudSpriteBatch.end();
		this.shapeRenderer.end();
	}

	@Override
	public void tick(float deltaTime) {
		this.scene.tick(deltaTime);
	}

	@Override
	public void resize(int width, int height) {
	}
}
