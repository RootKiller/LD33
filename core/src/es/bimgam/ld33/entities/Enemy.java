package es.bimgam.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import es.bimgam.ld33.core.Debug;

public class Enemy extends GameEntity {

	static private final String SPRITE_FILE = "entities/sprites/ENEMY/character.png";

	private AssetManager assetManager;
	private World physicalWorld;

	private CircleShape circleShape;

	private Player player;

	private Vector2 centerPoint = new Vector2();

	private float timeToChangeTask;

	public Enemy(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.timeToChangeTask = 0.0f;
		this.player = scene.find("Player");
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
		def.position.set(-250.0f + (float)Math.random() * 500.0f, -250.0f + (float)Math.random() * 500.0f);
		def.fixedRotation = true;

		this.physicalBody = this.physicalWorld.createBody(def);
		this.physicalBody.setUserData(this);

		circleShape = new CircleShape();
		circleShape.setRadius(10.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		fixtureDef.filter.categoryBits = CollisionMasks.ENEMY;
		fixtureDef.filter.maskBits = CollisionMasks.BULLET | CollisionMasks.PLAYER;

		this.physicalBody.createFixture(fixtureDef);
	}

	private void doAIWork(float deltaTime) {
		timeToChangeTask -= deltaTime;

		if (timeToChangeTask <= 0.0f) {
			Vector2 playerPos = this.player.getPosition().nor();
			Vector2 myPos = getPosition().nor();

			float dot = playerPos.dot(myPos);

			this.physicalBody.setLinearVelocity(new Vector2((float)Math.random(), (float)Math.random()));

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
	public void onCollisionEnter(GameEntity entity) {
		if (entity.getTypeName() == "Bullet") {
			Debug.Log("HIT");
			this.scene.destroyEntity(this);
			this.scene.destroyEntity(entity);
		}
	}
}
