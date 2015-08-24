package es.bimgam.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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

import es.bimgam.ld33.core.Debug;
import es.bimgam.ld33.graphics.Font;
import es.bimgam.ld33.states.InGameState;
import es.bimgam.ld33.states.StateManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Player extends GameEntity {

	static private final String SPRITE_FILE = "entities/sprites/PLAYER/character.png";

	static private final String HEALTH_ICON_FILE = "interface/icons/health.png";

	static private final float SHOOTING_COOLDOWN = 0.8f;

	static private final float SHOOTING_BONUS_FOR_KILLS = 0.00001f;

	static private final int STARTUP_HEALTH = 5;

	private AssetManager assetManager;

	private boolean wasFireButtonPressed;

	private int bulletCounter;

	public int killedSoldiers;
	public int killedCivs;
	public int hitsCount;

	private Vector2 centerPoint = new Vector2();

	private Vector2 velocity = new Vector2(0.0f, 0.0f);

	private Sound shootSound;
	private Sound hitSound;
	private Sound deadSound;
	private Sound pickupSound;

	// HUD
	private Texture healthTexture;

	private class WeaponInfo {
		Class<? extends Bullet> bullet;
		int ammo;

		public WeaponInfo(Class<? extends Bullet> bullet, int ammo) {
			this.bullet = bullet;
			this.ammo = ammo;
		}
	};

	private ArrayList<WeaponInfo> weapons = new ArrayList<WeaponInfo>();
	private int currentWeapon = 0;

	private float shootCooldown;

	private int xp;
	private int level;

	private int health;

	private float levelUpLabel;

	public Player(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);

		this.wasFireButtonPressed = false;

		this.weapons.add(new WeaponInfo(Bullet.class, -1));

		this.shootCooldown = 0.0f;
		this.shootSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot.ogg"));
		this.hitSound = Gdx.audio.newSound(Gdx.files.internal("sound/hit.ogg"));
		this.deadSound = Gdx.audio.newSound(Gdx.files.internal("sound/dead.ogg"));
		this.pickupSound = Gdx.audio.newSound(Gdx.files.internal("sound/pickup.ogg"));

		this.xp = 0;
		this.level = 1;

		this.levelUpLabel = 0.0f;

		this.health = STARTUP_HEALTH;
	}

	@Override
	public void dispose() {
		super.dispose();

		this.shootSound.dispose();
		this.hitSound.dispose();
		this.deadSound.dispose();
		this.pickupSound.dispose();
		if (this.healthTexture != null) {
			this.healthTexture.dispose();
		}
	}

	@Override
	public String getTypeName() {
		return "Player";
	}

	@Override
	public void setupPhysics(World world) {
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(0, 0);
		def.fixedRotation = true;
		def.gravityScale = 0.0f;

		this.physicalBody = world.createBody(def);
		this.physicalBody.setUserData(this);

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(13.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.filter.categoryBits = CollisionMasks.PLAYER;
		fixtureDef.filter.maskBits = CollisionMasks.PICKUP | CollisionMasks.ENEMY | CollisionMasks.ENEMY_BULLET;

		this.physicalBody.createFixture(fixtureDef);
		circleShape.dispose();
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load(SPRITE_FILE, Texture.class);

		assetManager.load(HEALTH_ICON_FILE, Texture.class);
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
			nextWeapon();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			previousWeapon();
		}

		final boolean isFireButtonPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		final boolean fire = (isFireButtonPressed && (! wasFireButtonPressed));
		wasFireButtonPressed = isFireButtonPressed;

		if (fire) {
			fire();
		}

		this.physicalBody.setLinearVelocity(velocity.x * 500.0f, velocity.y * 500.0f);
	}

	@Override
	public void tick(float deltaTime) {
		if (this.sprite != null && this.physicalBody != null) {
			Vector2 pos = this.physicalBody.getPosition();
			this.sprite.setPosition(pos.x - centerPoint.x, pos.y - centerPoint.y);

			this.sprite.setRotation(this.physicalBody.getAngle());
		}

		if (this.physicalBody != null) {
			final float alpha = MathUtils.clamp(100.0f * deltaTime, 0.0f, 1.0f);
			velocity.lerp(Vector2.Zero, alpha);
			// velocity.interpolate(Vector2.Zero, alpha, Interpolation.sine);
		}

		this.shootCooldown -= deltaTime;
		this.levelUpLabel -= deltaTime;
	}

	@Override
	public void render(SpriteBatch batch) {
		if (this.sprite != null) {
			this.sprite.draw(batch);
		}

		if (this.sprite == null && this.assetManager.isLoaded(SPRITE_FILE)) {
			Texture texture = this.assetManager.get(SPRITE_FILE, Texture.class);
			this.sprite = new Sprite(texture);
			this.sprite.setPosition(0, 0);
			this.sprite.setSize(texture.getWidth(), texture.getHeight());
			centerPoint.x = texture.getWidth() / 2;
			centerPoint.y = texture.getHeight() / 2;
		}

		if (this.healthTexture == null && this.assetManager.isLoaded(HEALTH_ICON_FILE)) {
			this.healthTexture = this.assetManager.get(HEALTH_ICON_FILE, Texture.class);
		}
	}

	@Override
	public void drawHudElement(ShapeRenderer shapeRenderer, SpriteBatch batch, Font hudFont) {
		if (this.levelUpLabel > 0.0f) {
			final float width = hudFont.getRenderWidth("Level "+this.level+"!");
			hudFont.draw(batch, "Level " + this.level + "!", Gdx.graphics.getWidth() / 2 - width / 2, Gdx.graphics.getHeight() / 2 - 100.0f, Color.YELLOW);
		}

		Field weaponNameField = null;
		try {
			WeaponInfo currentWeaponInfo = this.weapons.get(this.currentWeapon);
			weaponNameField = currentWeaponInfo.bullet.getField("WEAPON_NAME");
			hudFont.draw(batch, "Current weapon: " + weaponNameField.get(null) + " (" + ((currentWeaponInfo.ammo == -1) ? "INF" : currentWeaponInfo.ammo) + ")", 10, 10, Color.BLACK);
		} catch(Exception e) {
		}
		hudFont.draw(batch, "XP: " + xp + " Level: " + level, 10, 50, Color.BLACK);

		if (shootCooldown > 0.0f) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(10, Gdx.graphics.getHeight() - 90, 100, 20);
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.rect(10, Gdx.graphics.getHeight() - 90, 100 * (shootCooldown / SHOOTING_COOLDOWN), 20);
		}

		if (this.healthTexture != null) {
			batch.draw(this.healthTexture, 10, 10);
		}
		String healthStr = ""+this.health;
		hudFont.draw(batch, healthStr, 31, Gdx.graphics.getHeight() -25, Color.BLACK);
		hudFont.draw(batch, healthStr, 30, Gdx.graphics.getHeight() -26, Color.WHITE);
	}

	public int getLevelFromXP(int xp) {
		return 1 + (int) Math.sqrt(xp / 5);
	}

	public void addXP(int xp) {
		this.xp += xp;
		int lvl = getLevelFromXP(this.xp);
		if (lvl != this.level) {
			this.levelUpLabel = 3.0f;
			this.level = lvl;
		}
	}

	public void fire() {
		if (shootCooldown > 0.0f) {
			return;
		}

		WeaponInfo weaponInfo = this.weapons.get(this.currentWeapon);
		if (weaponInfo == null) {
			return;
		}

		Bullet bullet = this.scene.createEntity("Bullet" + bulletCounter, weaponInfo.bullet);
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

		this.shootSound.play();
		shootCooldown = SHOOTING_COOLDOWN - (SHOOTING_BONUS_FOR_KILLS * (this.killedCivs + this.killedSoldiers));

		if (weaponInfo.ammo != -1) {
			-- weaponInfo.ammo;
			if (weaponInfo.ammo <= 0) {
				this.weapons.remove(weaponInfo);
				nextWeapon();
			}
		}
	}

	public void onHitEnemy(Enemy enemy) {
		hitsCount ++;
		if (enemy.getHealth() > 0) {
			this.hitSound.play();
		}
		else {
			this.deadSound.play();
		}
	}

	@Override
	public void onCollisionEnter(GameEntity entity) {
		if (entity.getTypeName() == "Bullet") {
			this.health -= ((Bullet) entity).getDamage();
			if (this.health <= 0) {
				this.deadSound.play();
				StateManager.Instance.getActiveStateSafe(InGameState.class).gameOver(this.killedCivs + this.killedSoldiers, this.xp, this.level);
			}
			else {
				this.hitSound.play();
			}
			this.scene.destroyEntity(entity);
		}

		if (entity.getTypeName() == "Pickup") {
			Pickup pickup = (Pickup) entity;
			switch (pickup.getKind()) {
			case HEALTH:
				this.health += (Integer) pickup.getValue();
				break;
			case FREEZER:
				this.addWeapon(FreezingBullet.class, (Integer) pickup.getValue());
				break;
				break;
			}
			this.scene.destroyEntity(pickup);
			this.pickupSound.play();
		}
	}

	private void nextWeapon() {
		this.currentWeapon = (this.currentWeapon + 1) % this.weapons.size();
	}

	private void previousWeapon() {
		this.currentWeapon = (this.currentWeapon + (this.weapons.size() - 1)) % this.weapons.size();
	}

	public void addWeapon(Class<? extends Bullet> bulletType, int ammo) {
		if (ammo <= 0) {
			return;
		}

		for (WeaponInfo wInfo : this.weapons) {
			if (wInfo.bullet == bulletType) {
				wInfo.ammo += ammo;
				return;
			}
		}

		this.weapons.add(new WeaponInfo(bulletType, ammo));
	}

	public void takeWeapon(Class<? extends Bullet> bulletType) {
		for (WeaponInfo wInfo : this.weapons) {
			if (wInfo.bullet == bulletType) {
				this.weapons.remove(wInfo);
				break;
			}
		}
	}

	public int getWantedLevel() {
		if (this.killedSoldiers > 0) {
			return 3;
		}
		if (this.killedCivs > 0) {
			return 2;
		}
		if (this.hitsCount > 0) {
			return 1;
		}
		return 0;
	}

	public void addHealth(int hp) {
		this.health += hp;
	}
}
