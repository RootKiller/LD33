package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
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

	private Font hudFont;

	private int enemyCounter = 0;
	private float timeToSpawnNewEntities = 0.0f;

	private static final int ENEMIES_UPPER_LIMIT = 1000;

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

		for (int i = 0; i < 500; ++i) {
			createEnemy();
		}
		timeToSpawnNewEntities = 20.0f + (float)Math.random() * 10.0f;
	}

	private void createEnemy() {
		this.scene.createEntity("Enemy " + this.enemyCounter, Enemy.class);
		this.enemyCounter ++;
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
		Color color = new Color(0x4470a7ff);
		Gdx.gl.glClearColor(color.r, color.g, color. b, 1);
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

		timeToSpawnNewEntities -= deltaTime;
		if (timeToSpawnNewEntities <= 0.0f) {
			int enemiesOnScene = this.scene.countAliveEntitiesByClass(Enemy.class);
			if (enemiesOnScene < ENEMIES_UPPER_LIMIT) {
				int count = 20 + (int) (Math.random() * 100.0);
				if ((enemiesOnScene + count) > ENEMIES_UPPER_LIMIT) {
					count = (ENEMIES_UPPER_LIMIT - enemiesOnScene);
				}

				for (int i = 0; i < count; ++i) {
					createEnemy();
				}
			}
			timeToSpawnNewEntities = 20.0f + (float)Math.random() * 10.0f;
		}
	}

	@Override
	public void resize(int width, int height) {
	}
}
