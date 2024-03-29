package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import es.bimgam.ld33.graphics.Font;

public class Soldier extends Enemy {

	private static int bulletCounter = 0;

	static private final String SPRITE_FILE = "entities/sprites/ENEMY/character2.png";

	static private final int MAX_HEALTH = 10;

	static private final float HEALTH_REGENERATION = 4.0f;

	private final static float AI_TIME_TO_CHANGE_TASK = 5.0f;
	private final static float AI_MINIMAL_SHOOT_DISTANCE = 200.0f;

	private AssetManager assetManager;

	private Vector2 centerPoint = new Vector2();

	private float timeToChangeTask;

	private float timeToRegenerateHP;

	private float freezeCooldown;

	private float shootCooldown;

	public Soldier(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.health = MAX_HEALTH;
		this.shootCooldown = 10.0f;
	}

	@Override
	public String getTypeName() {
		return "Enemy";
	}

	@Override
	public void setupPhysics(World world) {
		this.player = scene.find("Player");
		Vector2 playerPos = this.player.getPosition();

		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(playerPos.x + (-500.0f + (float)Math.random() * 1000.0f), playerPos.y + (-500.0f + (float)Math.random() * 1000.0f));
		def.fixedRotation = true;

		this.physicalBody = world.createBody(def);
		this.physicalBody.setUserData(this);

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(10.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.8f;
		fixtureDef.filter.categoryBits = CollisionMasks.ENEMY;
		fixtureDef.filter.maskBits = CollisionMasks.BULLET | CollisionMasks.PLAYER | CollisionMasks.ENEMY;

		this.physicalBody.createFixture(fixtureDef);
		circleShape.dispose();
	}

	private void ai_updateShooting(float deltaTime) {
		this.shootCooldown -= deltaTime;
		if (this.shootCooldown <= 0.0f) {
			if (this.player.getWantedLevel() == 0) {
				return;
			}

			Vector2 playerPos = this.player.getPosition();
			Vector2 myPos = getPosition();

			final float dst = Vector2.dst2(playerPos.x, playerPos.y, myPos.x, myPos.y);
			if (dst <= (AI_MINIMAL_SHOOT_DISTANCE * AI_MINIMAL_SHOOT_DISTANCE)) {
				fire(playerPos);
				this.shootCooldown = 10.0f;
			}
		}
	}

	private void fire(Vector2 target) {
		String name = "EnemyBullet" + bulletCounter;
		Bullet bullet = this.scene.createEntity(name, EnemyBullet.class);
		bulletCounter++;

		Vector2 myPos = getPosition();
		bullet.physicalBody.setTransform(myPos.x, myPos.y, 0.0f);
		Vector2 vel = target.sub(myPos.x, myPos.y).nor();
		vel.x *= 100.0f;
		vel.y *= 100.0f;
		bullet.physicalBody.setLinearVelocity(vel);
	}

	private void ai_doWork(float deltaTime) {
		// If we are under "freezer" effect do not process "AI"
		if (this.freezeCooldown > 0.0f) {
			return;
		}

		ai_updateShooting(deltaTime);

		timeToChangeTask -= deltaTime;

		if (timeToChangeTask <= 0.0f) {
			Vector2 playerPos = this.player.getPosition();
			Vector2 myPos = getPosition();
			Vector2 direction = playerPos.sub(myPos).nor();

			final float MOVEMENT_SPEED = 15.0f;
			if (this.player.getWantedLevel() > 0) {
				this.physicalBody.setLinearVelocity((float)(direction.x * Math.random()) * MOVEMENT_SPEED, (float)(direction.y * Math.random()) * MOVEMENT_SPEED);
			}
			else {
				this.physicalBody.setLinearVelocity((float)(-1.0f + Math.random() * 2.0f) * MOVEMENT_SPEED, (float)(-1.0f + Math.random() * 2.0f) * MOVEMENT_SPEED);
			}

			timeToChangeTask = AI_TIME_TO_CHANGE_TASK;
		}
	}

	private void ai_runAwayFromPlayer() {
		Vector2 playerPos = this.player.getPosition();
		Vector2 myPos = getPosition();
		Vector2 direction = playerPos.sub(myPos).nor();

		final float MOVEMENT_SPEED = 35.0f;
		this.physicalBody.setLinearVelocity(-direction.x * MOVEMENT_SPEED, -direction.y * MOVEMENT_SPEED);

		timeToChangeTask = AI_TIME_TO_CHANGE_TASK;
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load(SPRITE_FILE, Texture.class);
	}

	@Override
	public void tick(float deltaTime) {
		ai_doWork(deltaTime);

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
				ai_runAwayFromPlayer();
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
		Vector2 myPosition = getPosition();

		Vector3 world = new Vector3(myPosition.x, myPosition.y + 15.0f, 0);
		Vector3 screen = this.scene.project(world);
		drawHealthBar(shapeRenderer, screen);

		world = new Vector3(myPosition.x, myPosition.y + 20.0f, 0);
		screen = this.scene.project(world);
		drawFreezerBar(shapeRenderer, screen);
	}

	@Override
	public void onCollisionEnter(GameEntity entity) {
		if (entity.getTypeName() == "Bullet") {
			this.health -= ((Bullet) entity).getDamage();
			this.player.onHitEnemy(this);
			timeToRegenerateHP = HEALTH_REGENERATION;
			if (this.health <= 0) {
				this.scene.destroyEntity(this);
				this.player.addXP(3);
				this.player.killedSoldiers ++;
			}
			this.scene.destroyEntity(entity);
			return;
		}

		if (entity.getTypeName() == "FreezingBullet") {
			if (this.physicalBody.getType() != BodyDef.BodyType.StaticBody) {
				this.freezeCooldown = FreezingBullet.FREEZEE_COOLDOWN;
			}

			this.scene.destroyEntity(entity);
		}
	}

	private void drawHealthBar(ShapeRenderer shapeRenderer, Vector3 screen) {
		if (this.health == MAX_HEALTH) {
			return;
		}

		super.drawBar(shapeRenderer, screen, Color.RED, ((float) this.health / MAX_HEALTH));
	}

	private void drawFreezerBar(ShapeRenderer shapeRenderer, Vector3 screen) {
		if (this.freezeCooldown <= 0.0f || this.physicalBody.getType() != BodyDef.BodyType.StaticBody) {
			return;
		}

		super.drawBar(shapeRenderer, screen, Color.BLUE, this.freezeCooldown / FreezingBullet.FREEZEE_COOLDOWN);
	}
}
