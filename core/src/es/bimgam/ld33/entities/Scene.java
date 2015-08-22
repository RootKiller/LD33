package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Scene {

	private World world;

	private ArrayList<GameEntity> entities = new ArrayList<GameEntity>();

	private SpriteBatch spriteBatch;

	private AssetManager assetsManager;

	public Scene(AssetManager assetManager) {
		this.world = new World(new Vector2(0, -10.0f), false);
		this.spriteBatch = new SpriteBatch();
		this.assetsManager = assetManager;
	}

	public GameEntity createEntity(Class<? extends GameEntity> entityClass) {
		GameEntity entity = null;
		try {
			Constructor<? extends GameEntity> ctor = entityClass.getConstructor(Scene.class, World.class, AssetManager.class);
			entity = ctor.newInstance(this, this.world, assetsManager);
		} catch(Exception e) {
			System.out.println("Unable to create game entity. " + e.getMessage());
		}
		return entity;
	}

	public void dispose() {
		this.spriteBatch.dispose();
		this.spriteBatch = null;
	}

	public void tick(float deltaTime) {
		for (GameEntity entity : this.entities) {
			entity.handleInput();
			entity.tick(deltaTime);
		}
	}

	public void render() {
		this.spriteBatch.begin();
		for (GameEntity entity : this.entities) {
			entity.render(this.spriteBatch);
		}
		this.spriteBatch.end();
	}

}
