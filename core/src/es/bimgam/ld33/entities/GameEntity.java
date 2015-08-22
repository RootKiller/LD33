package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class GameEntity {

	public Body physicalBody;
	public Sprite sprite;

	public GameEntity(Scene scene, World physicalWorld, AssetManager assetManager) {
		setupPhysics(physicalWorld);
		setupVisuals(assetManager);
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
}
