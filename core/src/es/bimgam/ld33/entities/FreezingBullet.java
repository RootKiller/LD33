package es.bimgam.ld33.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import es.bimgam.ld33.core.Debug;

public class FreezingBullet extends Bullet {

	static private final String SPRITE_FILE = "entities/sprites/BULLET/freezingBullet.png";

	public final static float FREEZEE_COOLDOWN = 5.0f;

	private AssetManager assetManager;
	private Vector2 centerPoint = new Vector2();

	public FreezingBullet(Scene scene, World physicalWorld, AssetManager assetManager) {
		super(scene, physicalWorld, assetManager);
	}

	@Override
	public String getTypeName() {
		return "FreezingBullet";
	}

	@Override
	public int getDamage() {
		return 0;
	}

	@Override
	public void setupVisuals(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load(SPRITE_FILE, Texture.class);
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
}
