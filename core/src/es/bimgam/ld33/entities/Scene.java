package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import es.bimgam.ld33.core.Debug;
import es.bimgam.ld33.graphics.Font;
import es.bimgam.ld33.states.InGameState;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Scene {

	private Camera camera;

	private World world;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	private HashMap<String, GameEntity> entities = new HashMap<String, GameEntity>();
	private HashMap<String, GameEntity> entitiesToCreate = new HashMap<String, GameEntity>();

	private ArrayList<GameEntity> entitiesToRemove = new ArrayList<GameEntity>();

	private boolean processing;

	private AssetManager assetsManager;
	private InGameState state;

	public Scene(InGameState state, AssetManager assetManager, Camera camera) {
		this.camera = camera;
		this.state = state;
		this.world = new World(Vector2.Zero, true);
		this.assetsManager = assetManager;
		this.processing = false;

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Object udA = contact.getFixtureA().getBody().getUserData();
				Object udB = contact.getFixtureB().getBody().getUserData();

				if (udA != null && udB != null) {
					GameEntity a = (GameEntity) udA;
					GameEntity b = (GameEntity) udB;

					if (a.isAlive() && b.isAlive()) {
						a.onCollisionEnter(b);
						b.onCollisionEnter(a);
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
				Object udA = contact.getFixtureA().getBody().getUserData();
				Object udB = contact.getFixtureB().getBody().getUserData();

				if (udA != null && udB != null) {
					GameEntity a = (GameEntity) udA;
					GameEntity b = (GameEntity) udB;

					if (a.isAlive() && b.isAlive()) {
						a.onCollisionExit(b);
						b.onCollisionExit(a);
					}
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}

	public Vector3 unproject(Vector3 screen) {
		return this.camera.unproject(screen);
	}

	public Vector3 project(Vector3 world) {
		return this.camera.project(world);
	}

	private void flushToRemove() {
		if (entitiesToRemove.isEmpty()) {
			return;
		}

		for (Map.Entry<String, GameEntity> pair : this.entities.entrySet()) {
			for (GameEntity entity : this.entitiesToRemove) {
				if (pair.getValue() == entity) {
					this.entitiesToRemove.remove(entity);
					entity.dispose();
					this.entities.remove(pair);
					break;
				}
			}
		}
		Debug.Assert(this.entitiesToRemove.isEmpty(), "Entities to remove is not empty");
	}

	private void processToCreate() {
		if (processing) {
			return;
		}

		Iterator<Map.Entry<String, GameEntity>> it = this.entitiesToCreate.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, GameEntity> pair = it.next();
			entities.put(pair.getKey(), pair.getValue());
			it.remove();
		}
		this.entitiesToCreate.clear();
	}

	public void dispose() {
		processToCreate();
		for (GameEntity entity : this.entities.values()) {
			entity.destroy();
			entity.dispose();
		}
		entities.clear();
		world.dispose();
	}

	public <T extends GameEntity> T find(String name) {
		return (T) this.entities.get(name);
	}

	public <T extends GameEntity> T createEntity(String name, Class<T> entityClass) {
		T entity = null;
		try {
			Constructor<T> ctor = entityClass.getConstructor(Scene.class, World.class, AssetManager.class);
			entity = ctor.newInstance(this, this.world, assetsManager);
			if (processing) {
				this.entitiesToCreate.put(name, entity);
			}
			else {
				this.entities.put(name, entity);
			}
		} catch(Exception e) {
			System.out.println("Unable to create game entity. " + e.getMessage());
		}
		return entity;
	}

	public void destroyEntity(GameEntity entity) {
		if (! entity.isAlive()) {
			return;
		}

		entity.destroy();
		entitiesToRemove.add(entity);
	}

	public void tick(float deltaTime) {
		processing = true;
		this.world.step(1/60.0f, 6, 2);
		for (GameEntity entity : this.entities.values()) {
			if (! entity.isAlive()) {
				continue;
			}

			entity.handleInput();
			entity.tick(deltaTime);
		}
		processing = false;
		processToCreate();
		flushToRemove();
	}

	public void debugDraw() {
		if (Debug.RENDER_PHYSICS) {
			debugRenderer.render(world, camera.combined);
		}
	}

	public void render(SpriteBatch batch) {
		for (GameEntity entity : this.entities.values()) {
			if (! entity.isAlive()) {
				continue;
			}

			entity.render(batch);
		}
	}

	public void drawHudElements(ShapeRenderer shapeRenderer, SpriteBatch batch, Font hudFont) {
		for (GameEntity entity : this.entities.values()) {
			if (! entity.isAlive()) {
				continue;
			}

			entity.drawHudElement(shapeRenderer, batch, hudFont);
		}
	}

	public int countAliveEntitiesByClass(Class<? extends GameEntity> lookupClass) {
		int count = 0;
		for (Map.Entry<String, GameEntity> pair : this.entities.entrySet()) {
			GameEntity entity = pair.getValue();
			if (! entity.isAlive()) {
				continue;
			}

			if (entity.getClass() == lookupClass) {
				count ++;
			}
		}
		return count;
	}
}
