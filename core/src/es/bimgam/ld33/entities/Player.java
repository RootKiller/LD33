package es.bimgam.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import es.bimgam.ld33.graphics.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Player extends GameEntity {

	static private final String SPRITE_FILE = "entities/sprites/PLAYER/character.png";

	private AssetManager assetManager;
	private World physicalWorld;
	private CircleShape circleShape;

	private boolean wasFireButtonPressed;

	private int bulletCounter;

	public int killedEnemies;

	private Vector2 centerPoint = new Vector2();

	private Vector2 velocity = new Vector2(0.0f, 0.0f);

	private ArrayList<Class<? extends Bullet>> bulletTypes = new ArrayList<Class<? extends Bullet>>();
	private int currentBulletType = 0;

	public Player(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.wasFireButtonPressed = false;

		bulletTypes.add(Bullet.class);
		bulletTypes.add(FreezingBullet.class);
	}

	@Override
	public String getTypeName() {
		return "Player";
	}

	@Override
	public void setupPhysics(World world) {
		this.physicalWorld = world;

		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.KinematicBody;
		def.position.set(0, 0);
		def.fixedRotation = true;

		this.physicalBody = this.physicalWorld.createBody(def);
		this.physicalBody.setUserData(this);

		circleShape = new CircleShape();
		circleShape.setRadius(13.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.filter.categoryBits = CollisionMasks.PLAYER;
		fixtureDef.filter.maskBits = CollisionMasks.ENEMY;

		this.physicalBody.createFixture(fixtureDef);
		circleShape.dispose();
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load(SPRITE_FILE, Texture.class);
	}

	@Override
	public void handleInput() {
		if (this.physicalBody == null) {
			return;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			velocity.y += 1.0f;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			velocity.y += -1.0f;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			velocity.x += -1.0f;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			velocity.x += 1.0f;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			this.currentBulletType = (this.currentBulletType + 1) % this.bulletTypes.size();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			this.currentBulletType = (this.currentBulletType + (this.bulletTypes.size() - 1)) % this.bulletTypes.size();
		}

		final boolean isFireButtonPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		final boolean fire = (isFireButtonPressed && (! wasFireButtonPressed));
		wasFireButtonPressed = isFireButtonPressed;

		if (fire) {
			fire();
		}

		if (velocity.len() > 0.0f) {
			this.physicalBody.setTransform(this.physicalBody.getPosition().add(velocity), this.physicalBody.getAngle());
		}
	}

	@Override
	public void tick(float deltaTime) {
		if (this.sprite != null && this.physicalBody != null) {
			Vector2 pos = this.physicalBody.getPosition();
			this.sprite.setPosition(pos.x - centerPoint.x, pos.y - centerPoint.y);

			this.sprite.setRotation(this.physicalBody.getAngle());
		}

		if (this.physicalBody != null) {
			final float alpha = MathUtils.clamp(10.0f * deltaTime, 0.0f, 1.0f);
			velocity.lerp(Vector2.Zero, alpha);
			velocity.interpolate(Vector2.Zero, alpha, Interpolation.sine);
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
	public Vector2 getPosition() {
		if (this.physicalBody != null) {
			return this.physicalBody.getPosition();
		}
		return Vector2.Zero;
	}

	@Override
	public void drawHudElement(ShapeRenderer shapeRenderer, SpriteBatch batch, Font hudFont) {
		Field weaponNameField = null;
		try {
			weaponNameField = this.bulletTypes.get(this.currentBulletType).getField("WEAPON_NAME");
			hudFont.draw(batch, "Current weapon: " + weaponNameField.get(null), 10, 10, Color.BLACK);
		} catch(Exception e) {
		}
	}

	public void fire() {
		Bullet bullet = this.scene.createEntity("Bullet" + bulletCounter, this.bulletTypes.get(this.currentBulletType));
		bulletCounter++;
		Vector3 screen = new Vector3((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0.0f);
		Vector3 world = this.scene.unproject(screen);

		Vector2 myPos = getPosition();
		bullet.physicalBody.setTransform(myPos.x, myPos.y, 0.0f);
		Vector2 world2 = new Vector2(world.x, world.y);
		Vector2 vel = world2.sub(myPos.x, myPos.y).nor();
		vel.x *= 100.0f;
		vel.y *= 100.0f;
		bullet.physicalBody.setLinearVelocity(vel);
	}
}
