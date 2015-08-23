package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyBullet extends Bullet {

	public EnemyBullet(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);
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

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(4.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 1.0f;
		fixtureDef.filter.categoryBits = CollisionMasks.ENEMY_BULLET;
		fixtureDef.filter.maskBits = CollisionMasks.PLAYER;

		this.physicalBody.createFixture(fixtureDef);
		circleShape.dispose();
	}
}
