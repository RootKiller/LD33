package es.bimgam.ld33.entities;

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

public class Bullet extends GameEntity {

	static private final String SPRITE_FILE = "entities/sprites/BULLET/bullet.png";

	private CircleShape circleShape;
	private AssetManager assetManager;

	private float lifeTime;
	private Vector2 centerPoint = new Vector2();

	public Bullet(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		lifeTime = 5.0f;
	}

	@Override
	public String getTypeName() {
		return "Bullet";
	}

	@Override
	public void setupPhysics(World world) {
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(0, 0);
		def.fixedRotation = true;
		def.bullet = true;

		this.physicalBody = world.createBody(def);
		this.physicalBody.setUserData(this);

		circleShape = new CircleShape();
		circleShape.setRadius(4.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 1.0f;
		fixtureDef.filter.categoryBits = CollisionMasks.BULLET;
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
	public void tick(float deltaTime) {
		Debug.Assert(isAlive(), "Bullet is not active but engine tried to tick it!");

		if (this.sprite != null && this.physicalBody != null) {
			Vector2 pos = this.physicalBody.getPosition();
			this.sprite.setPosition(pos.x - centerPoint.x, pos.y - centerPoint.y);

			this.sprite.setRotation(this.physicalBody.getAngle());
		}

		lifeTime -= deltaTime;
		Debug.Log(lifeTime);
		if (lifeTime <= 0.0f) {
			this.scene.destroyEntity(this);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		Debug.Assert(isAlive(), "Bullet is not alive but engine tried to draw it!");

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
}
