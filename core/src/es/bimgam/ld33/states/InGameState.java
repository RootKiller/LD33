package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.bimgam.ld33.LD33;

import es.bimgam.ld33.entities.Enemy;
import es.bimgam.ld33.entities.Scene;
import es.bimgam.ld33.entities.Player;

import java.util.ArrayList;

public class InGameState extends State {
	private OrthographicCamera orthoCamera;
	private SpriteBatch spriteBatch;
	private Scene scene;

	private Player player;

	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	public InGameState(StateManager stateManager, Stage stage, Skin skin) {
		super(stateManager, stage, skin);
	}

	public String getName() {
		return "InGameState";
	}

	public Vector3 unproject(Vector3 screen) {
		if (orthoCamera == null) {
			return Vector3.Zero;
		}
		return orthoCamera.unproject(screen);
	}

	@Override
	public void activate() {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		this.orthoCamera = new OrthographicCamera(300.0f, 300.0f * (height / width));
		this.orthoCamera.position.set(orthoCamera.viewportWidth / 2f, orthoCamera.viewportHeight / 2f, 0);
		this.orthoCamera.update();

		this.spriteBatch = new SpriteBatch();

		this.scene = new Scene(this, LD33.Instance.getAssetsManager(), this.orthoCamera);

		this.player = this.scene.createEntity("Player", Player.class);

		for (int i = 0; i < 100; ++i) {
			enemies.add(this.scene.createEntity("Enemy " + i, Enemy.class));
		}
	}

	@Override
	public void deactivate() {
		this.player = null;
		this.orthoCamera = null;
		this.spriteBatch = null;
		this.scene.dispose();
		this.scene = null;
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
	}

	@Override
	public void tick(float deltaTime) {
		this.scene.tick(deltaTime);
	}

	@Override
	public void resize(int width, int height) {
	}
}
