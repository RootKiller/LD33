package es.bimgam.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import es.bimgam.ld33.core.Debug;
import es.bimgam.ld33.graphics.Font;

public class Enemy extends GameEntity {

	static private final String SPRITE_FILE = "entities/sprites/ENEMY/character.png";

	static private final int MAX_HEALTH = 5;

	static private final float HEALTH_REGENERATION = 4.0f;

	private AssetManager assetManager;
	private World physicalWorld;

	private CircleShape circleShape;

	private Player player;

	private Vector2 centerPoint = new Vector2();

	private float timeToChangeTask;

	private int health;

	private float timeToRegenerateHP;

	private float freezeCooldown;

	public Enemy(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.timeToChangeTask = 0.0f;
		this.player = scene.find("Player");

		this.health = MAX_HEALTH;
		this.freezeCooldown = 0.0f;
	}

	@Override
	public String getTypeName() {
		return "Enemy";
	}

	@Override
	public void setupPhysics(World world) {
		physicalWorld = world;

		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(-1000.0f + (float)Math.random() * 2000.0f, -1000.0f + (float)Math.random() * 2000.0f);
		def.fixedRotation = true;

		this.physicalBody = this.physicalWorld.createBody(def);
		this.physicalBody.setUserData(this);

		circleShape = new CircleShape();
		circleShape.setRadius(10.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.8f;
		fixtureDef.filter.categoryBits = CollisionMasks.ENEMY;
		fixtureDef.filter.maskBits = CollisionMasks.BULLET | CollisionMasks.PLAYER;

		this.physicalBody.createFixture(fixtureDef);
	}

	private void doAIWork(float deltaTime) {
		timeToChangeTask -= deltaTime;

		if (timeToChangeTask <= 0.0f) {
			// Vector2 playerPos = this.player.getPosition().nor();
			// Vector2 myPos = getPosition().nor();

			// float dot = playerPos.dot(myPos);

			final float MOVEMENT_SPEED = 15.0f;
			this.physicalBody.setLinearVelocity(new Vector2((float)Math.random() * MOVEMENT_SPEED, (float)Math.random() * MOVEMENT_SPEED));

			timeToChangeTask = (float) Math.random() * 10.0f;
		}
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load(SPRITE_FILE, Texture.class);
	}

	@Override
	public void tick(float deltaTime) {
		doAIWork(deltaTime);

		if (this.sprite != null) {
			Vector2 pos = this.physicalBody.getPosition();
			this.sprite.setPosition(pos.x - centerPoint.x, pos.y - centerPoint.y);

			this.sprite.setRotation(this.physicalBody.getAngle());
		}

		if (this.health < MAX_HEALTH) {
			timeToRegenerateHP -= deltaTime;
			if (timeToRegenerateHP <= 0.0f) {
				this.health ++;
				timeToRegenerateHP = HEALTH_REGENERATION;
			}
		}

		if (this.freezeCooldown > 0.0f) {
			if (this.physicalBody.getType() != BodyDef.BodyType.StaticBody) {
				this.physicalBody.setType(BodyDef.BodyType.StaticBody);
				this.physicalBody.setLinearVelocity(Vector2.Zero);
			}
			this.freezeCooldown -= deltaTime;

			if (this.freezeCooldown <= 0.0f) {
				this.physicalBody.setType(BodyDef.BodyType.DynamicBody);
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (this.sprite != null) {
			this.sprite.draw(batch);
		}

		if (this.sprite == null && assetManager.isLoaded(SPRITE_FILE)) {
			Texture texture = assetManager.get(SPRITE_FILE, Texture.class);
			this.sprite = new Sprite(texture);
			this.sprite.setPosition(0, 0);
			this.sprite.setSize(texture.getWidth(), texture.getHeight());
			centerPoint.x = texture.getWidth() / 2;
			centerPoint.y = texture.getHeight() / 2;
		}
	}

	@Override
	public void drawHudElement(ShapeRenderer shapeRenderer, SpriteBatch batch, Font hudFont) {
		if (this.health < MAX_HEALTH) {
			Vector2 myPosition = getPosition();
			Vector3 world = new Vector3(myPosition.x, myPosition.y + 15.0f, 0);
			Vector3 screen = this.scene.project(world);
			screen.x -= 50.0f;
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(screen.x, screen.y, 100, 10);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(screen.x + 2.0f, screen.y + 2.0f, 96.0f * ((float)this.health / MAX_HEALTH), 6);
		}

		if (this.physicalBody.getType() == BodyDef.BodyType.StaticBody && this.freezeCooldown > 0.0f) {
			Vector2 myPosition = getPosition();
			Vector3 world = new Vector3(myPosition.x, myPosition.y + 20.0f, 0);
			Vector3 screen = this.scene.project(world);
			screen.x -= 50.0f;
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(screen.x, screen.y, 100, 10);
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.rect(screen.x + 2.0f, screen.y + 2.0f, 96.0f * (this.freezeCooldown / FreezingBullet.FREEZEE_COOLDOWN), 6);
		}
	}

	@Override
	public Vector2 getPosition() {
		if (this.physicalBody != null) {
			return this.physicalBody.getPosition();
		}
		return Vector2.Zero;
	}

	@Override
	public void onCollisionEnter(GameEntity entity) {
		if (entity.getTypeName() == "Bullet") {
			this.health -= ((Bullet) entity).getDamage();
			timeToRegenerateHP = HEALTH_REGENERATION;
			if (this.health <= 0) {
				this.scene.destroyEntity(this);
				this.player.killedEnemies ++;
			}
			this.scene.destroyEntity(entity);
		}

		if (entity.getTypeName() == "FreezingBullet") {
			if (this.physicalBody.getType() != BodyDef.BodyType.StaticBody) {
				this.freezeCooldown = FreezingBullet.FREEZEE_COOLDOWN;
			}

			this.scene.destroyEntity(entity);
		}
	}
}
