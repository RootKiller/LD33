package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.bimgam.ld33.LD33;

import es.bimgam.ld33.core.Debug;
import es.bimgam.ld33.entities.*;
import es.bimgam.ld33.graphics.Font;

import java.util.HashMap;

public class InGameState extends State {
	private OrthographicCamera orthoCamera;
	private SpriteBatch spriteBatch;
	private SpriteBatch hudSpriteBatch;
	private Scene scene;

	private Player player;

	private ShapeRenderer shapeRenderer;

	private Font hudFont;

	private int enemyCounter = 0;
	private float timeToSpawnNewEntities = 0.0f;

	public float timeToSpawnNewPickup = 0.0f;

	private boolean devMenu = false;

	private static final int ENEMIES_UPPER_LIMIT = 1000;

	public InGameState(StateManager stateManager, Stage stage, Skin skin) {
		super(stateManager, stage, skin);
	}

	public String getName() {
		return "InGameState";
	}

	@Override
	public void activate() {
		this.shapeRenderer = new ShapeRenderer();
		this.hudFont = new Font("fonts/segoepr.ttf", 20);

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		this.orthoCamera = new OrthographicCamera(300.0f, 300.0f * (height / width));
		this.orthoCamera.position.set(orthoCamera.viewportWidth / 2f, orthoCamera.viewportHeight / 2f, 0);
		this.orthoCamera.update();

		this.spriteBatch = new SpriteBatch();
		this.hudSpriteBatch = new SpriteBatch();

		this.scene = new Scene(this, LD33.Instance.getAssetsManager(), this.orthoCamera);

		this.player = this.scene.createEntity("Player", Player.class);

		for (int i = 0; i < 400; ++i) {
			createEnemy(false);
		}
		timeToSpawnNewEntities = 20.0f + (float)Math.random() * 10.0f;
	}

	private void showDevMenu() {
		if (devMenu) {
			return;
		}
		devMenu = true;
		TextButton spawnSoldier = new TextButton("Spawn soldier", this.skin);
		spawnSoldier.setPosition(10, 300);
		spawnSoldier.setSize(100, 50);
		spawnSoldier.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				StateManager.Instance.getActiveStateSafe(InGameState.class).createEnemy(true);
			}
		});
		this.stage.addActor(spawnSoldier);
	}

	private void spawnPickup() {
		int freeIndex = -1;
		for (int i = 0; i < 5; ++i) {
			if (! this.scene.doesEntityExists("Pickup" + i)) {
				freeIndex = i;
				break;
			}
		}

		if (freeIndex == -1) {
			return;
		}

		Pickup pickup = this.scene.createEntity("Pickup" + freeIndex, Pickup.class);
		if (Math.random() > 0.5f) {
			pickup.setup(Pickup.PickupKind.FREEZER, 5);
		}
		else {
			pickup.setup(Pickup.PickupKind.HEALTH, 1);
		}
		Vector2 playerPos = this.player.getPosition();
		pickup.setPosition(new Vector2(playerPos.x + -500.0f + (float) Math.random() * 1000.0f, playerPos.y + -500.0f + (float) Math.random() * 1000.0f));
	}

	private void createEnemy(boolean soldier) {
		Class<? extends Enemy> enemyClass = soldier ? Soldier.class : Enemy.class;
		this.scene.createEntity("Enemy " + this.enemyCounter, enemyClass);
		this.enemyCounter ++;
	}

	@Override
	public void deactivate() {
		this.shapeRenderer.dispose();
		this.shapeRenderer = null;
		this.player = null;
		this.orthoCamera = null;
		this.spriteBatch = null;
		this.scene.dispose();
		this.scene = null;
		this.hudFont.dispose();
		this.hudFont = null;
		this.hudSpriteBatch.dispose();
		this.hudSpriteBatch = null;
		this.stage.clear();
	}

	@Override
	public void render() {
		Color color = new Color(0x4470a7ff);
		Gdx.gl.glClearColor(color.r, color.g, color. b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.orthoCamera.position.set(this.player.getPosition(), 0);
		this.orthoCamera.update();
		this.spriteBatch.setProjectionMatrix(this.orthoCamera.combined);

		this.scene.debugDraw();
		this.spriteBatch.begin();
		this.scene.render(this.spriteBatch);
		this.spriteBatch.end();

		this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		this.hudSpriteBatch.begin();
		this.scene.drawHudElements(this.shapeRenderer, this.hudSpriteBatch, this.hudFont);
		this.hudSpriteBatch.end();
		this.shapeRenderer.end();
	}

	private class Cheat {
		private int[] sequence;
		private Runnable runnable;
		private long lastKeyTime = 0;
		private int progress = 0;

		public Cheat(int[] seq, Runnable run) {
			this.sequence = seq;
			this.runnable = run;
		}

		public void tick() {
			if (this.sequence == null) {
				return;
			}

			final long now = System.currentTimeMillis() / 1000L;
			final long diff = (now - lastKeyTime);
			if (diff > 1) {
				progress = 0;
			}

			if (Gdx.input.isKeyJustPressed(sequence[progress])) {
				progress ++;
				lastKeyTime = now;
			}

			int sequenceSize = this.sequence.length;
			if (progress == sequenceSize) {
				this.runnable.run();
				this.progress = 0;
			}
		}
	};

	// dev69 - opens development menu
	private Cheat devMenuCheat = new Cheat(new int[]{Input.Keys.D, Input.Keys.E, Input.Keys.V, Input.Keys.NUM_6, Input.Keys.NUM_9}, new Runnable() {
		@Override
		public void run() {
			StateManager.Instance.getActiveStateSafe(InGameState.class).showDevMenu();
		}
	});

	// iamgod - adds 2000xp
	private Cheat iAmGodCheat = new Cheat(new int[]{Input.Keys.I, Input.Keys.A, Input.Keys.M, Input.Keys.G, Input.Keys.O, Input.Keys.D}, new Runnable() {
		@Override
		public void run() {
			StateManager.Instance.getActiveStateSafe(InGameState.class).player.addXP(2000);
		}
	});

	// medicine - adds 1 hp
	private Cheat medicineCheat = new Cheat(new int[]{Input.Keys.M, Input.Keys.E, Input.Keys.D, Input.Keys.I, Input.Keys.C, Input.Keys.I, Input.Keys.N, Input.Keys.E}, new Runnable() {
		@Override
		public void run() {
			StateManager.Instance.getActiveStateSafe(InGameState.class).player.addHealth(1);
		}
	});

	// icecold - adds freezer with 10bullets (if already in eq adds 10 bullets)
	private Cheat iceColdCheat = new Cheat(new int[]{Input.Keys.I, Input.Keys.C, Input.Keys.E, Input.Keys.C, Input.Keys.O, Input.Keys.L, Input.Keys.D}, new Runnable() {
		@Override
		public void run() {
			StateManager.Instance.getActiveStateSafe(InGameState.class).player.addWeapon(FreezingBullet.class, 10);
		}
	});

	private void processSuperSecretStuff() {
		if (Debug.ALLOW_CHEATS) {
			devMenuCheat.tick();
			iAmGodCheat.tick();
			medicineCheat.tick();
			iceColdCheat.tick();
		}
	}

	@Override
	public void tick(float deltaTime) {
		processSuperSecretStuff(); // cheater!

		this.scene.tick(deltaTime);

		timeToSpawnNewPickup -= deltaTime;
		if (timeToSpawnNewPickup <= 0.0f) {
			spawnPickup();
			timeToSpawnNewPickup = (float)Math.random() * 20.0f;
		}

		timeToSpawnNewEntities -= deltaTime;
		if (timeToSpawnNewEntities <= 0.0f) {
			int enemiesOnScene = this.scene.countAliveEntitiesByClass(Enemy.class);
			if (enemiesOnScene < ENEMIES_UPPER_LIMIT) {
				int count = 20 + (int) (Math.random() * 100.0);
				if ((enemiesOnScene + count) > ENEMIES_UPPER_LIMIT) {
					count = (ENEMIES_UPPER_LIMIT - enemiesOnScene);
				}

				int soldiers = (int) (count * (0.05f + (this.player.getWantedLevel() * 0.05f)));
				for (int i = 0; i < count; ++i) {
					createEnemy((i <= soldiers));
				}
			}
			timeToSpawnNewEntities = 20.0f + (float)Math.random() * 10.0f;
		}
	}

	public void gameOver(int killedEntities, int xp, int level) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("KilledEnemies", killedEntities);
		params.put("XP", xp);
		params.put("Level", level);
		this.manager.setActiveState("GameOverState", params);
	}
}
