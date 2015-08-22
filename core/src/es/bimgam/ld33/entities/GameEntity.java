package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class GameEntity {

	public Body physicalBody;
	public Sprite sprite;
	public Scene scene;

	private boolean isAlive;
	private World physicalWorld;

	public GameEntity(Scene scene, World physicalWorld, AssetManager assetManager) {
		this.scene = scene;
		this.isAlive = true;
		this.physicalWorld = physicalWorld;

		setupPhysics(physicalWorld);
		setupVisuals(assetManager);
	}

	public String getTypeName() {
		return "GameEntity";
	}

	public void dispose() {
		if (this.physicalBody != null) {
			this.physicalWorld.destroyBody(this.physicalBody);
			this.physicalBody = null;
		}
	}

	public void destroy() {
		this.isAlive = false;
	}

	public boolean isAlive() {
		return this.isAlive;
	}

	public void setupPhysics(World physicalWorld) {
	}

	public void setupVisuals(AssetManager assetManager) {
	}

	public void handleInput() {
	}

	public void tick(float deltaTime) {
	}

	public void render(SpriteBatch batch) {
	}

	public void onCollisionEnter(GameEntity entity) {
	}

	public void onCollisionExit(GameEntity entity) {
	}

	public Vector2 getPosition() {
		return Vector2.Zero;
	}
}
