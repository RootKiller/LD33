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
		UNSET, HEALTH, FREEZER
	}

	private AssetManager assetManager;
	private Vector2 centerPoint = new Vector2();

	private PickupKind kind;
	private int value;

	private String textureName;

	public Pickup(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.kind = PickupKind.UNSET;
		this.value = 0;
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
		fixtureDef.filter.maskBits = CollisionMasks.PLAYER;

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
			this.sprite.setSize(texture.getWidth(), texture.getHeight());
			centerPoint.x = texture.getWidth() / 2;
			centerPoint.y = texture.getHeight() / 2;
		}
	}

	public PickupKind getKind() {
		return this.kind;
	}

	public int getValue() {
		return this.value;
	}

	public void setup(PickupKind kind, int value) {
		Debug.Assert(this.kind == PickupKind.UNSET, "Kind is not unset!");
		if (this.kind != PickupKind.UNSET) {
			return;
		}
		this.textureName = textureFromKind(kind);
		assetManager.load(this.textureName, Texture.class);
		this.kind = kind;
		this.value = value;
	}

	public void onPickup(GameEntity entity) {
		this.scene.destroyEntity(this);
	}

	private String textureFromKind(PickupKind kind) {
		switch (kind) {
		case HEALTH:
			return "entities/sprites/PICKUP/heart.png";
		case FREEZER:
			return "entities/sprites/BULLET/freezingBullet.png";
		}
		return "entities/sprites/PICKUP/heart.png";
	}

}
