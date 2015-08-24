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

public class Pickup extends GameEntity {

	public enum PickupKind {
		UNSET, HEALTH, FREEZER, BOOST
	}

	private AssetManager assetManager;
	private Vector2 centerPoint = new Vector2();

	private PickupKind kind;
	private Object value;

	private String textureName;

	private float lifeTime;

	public Pickup(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.kind = PickupKind.UNSET;
		this.value = 0;
		this.lifeTime = 30.0f;
	}

	@Override
	public String getTypeName() {
		return "Pickup";
	}

	@Override
	public void setupPhysics(World world) {
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.StaticBody;
		def.fixedRotation = true;

		this.physicalBody = world.createBody(def);
		this.physicalBody.setUserData(this);

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(8.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 0.8f;
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.filter.categoryBits = CollisionMasks.PICKUP;
		fixtureDef.filter.maskBits = CollisionMasks.PLAYER | CollisionMasks.ENEMY;

		this.physicalBody.createFixture(fixtureDef);
		circleShape.dispose();
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public void tick(float deltaTime) {
		if (this.sprite != null) {
			Vector2 pos = this.physicalBody.getPosition();
			this.sprite.setPosition(pos.x - centerPoint.x, pos.y - centerPoint.y);
		}

		this.lifeTime -= deltaTime;
		if (this.lifeTime <= 0.0f) {
			this.scene.destroyEntity(this);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (this.sprite != null) {
			this.sprite.draw(batch);
		}

		if (this.textureName == null) {
			return;
		}

		if (this.sprite == null && this.assetManager.isLoaded(this.textureName)) {
			Texture texture = this.assetManager.get(this.textureName, Texture.class);
			this.sprite = new Sprite(texture);
			this.sprite.setPosition(0, 0);
			this.sprite.setSize(16.0f, 16.0f);
			centerPoint.x = 8.0f;
			centerPoint.y = 8.0f;
		}
	}

	public PickupKind getKind() {
		return this.kind;
	}

	public Object getValue() {
		return this.value;
	}

	public void setup(PickupKind kind, Object value) {
		Debug.Assert(this.kind == PickupKind.UNSET, "Kind is not unset!");
		if (this.kind != PickupKind.UNSET) {
			return;
		}
		this.kind = kind;
		this.value = value;
		this.textureName = textureFromKind(kind);
		assetManager.load(this.textureName, Texture.class);
	}

	private String textureFromKind(PickupKind kind) {
		switch (kind) {
		case HEALTH:
			return "entities/sprites/PICKUP/heart.png";
		case FREEZER:
			return "entities/sprites/BULLET/freezingBullet.png";
		case BOOST: {
			Boost boost = (Boost) this.value;
			switch (boost) {
			case TRIPLE_BULLETS:
				return "interface/boosts/SHOT_TRIPLE.png";
			case SHIELD:
				return "interface/boosts/SHIELD.png";
			}
			return "interface/boosts/SHIELD.png";
		}
		}
		return "entities/sprites/PICKUP/heart.png";
	}

}
