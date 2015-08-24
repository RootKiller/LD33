package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class GameOverState extends State {

	private static final float ROW_SIZE = 40.0f;

	private Preferences savePref = Gdx.app.getPreferences("LD33.Game.Save");

	public GameOverState(StateManager manager, Stage stage, Skin skin) {
		super(manager, stage, skin);
	}

	@Override
	public String getName() {
		return "GameOverState";
	}

	@Override
	public void activate() {
		float yPos =  Gdx.graphics.getHeight()/2 + 100.0f;

		Label label = new Label("Game over!", this.skin);
		label.setPosition(Gdx.graphics.getWidth() / 2, yPos, Align.center);
		label.setSize(Gdx.graphics.getWidth(), ROW_SIZE);
		this.stage.addActor(label);

		yPos -= ROW_SIZE + 5.0f;
		int highScore = savePref.getInteger("HighScore", 0);
		Integer xp = (Integer)params.get("XP");
		boolean beaten = false;
		if (highScore < xp) {
			savePref.putInteger("HighScore", xp);
			highScore = xp;
			beaten = true;
		}

		label = new Label((beaten ? "New high" : "High") + " score"+(beaten ? "!" : "")+": " + highScore, this.skin);
		label.setPosition(Gdx.graphics.getWidth() / 2, yPos, Align.center);
		label.setColor(beaten ? Color.GREEN : Color.WHITE);
		this.stage.addActor(label);


		yPos -= ROW_SIZE + 5.0f;

		label = new Label("Killed enemies: " + params.get("KilledEnemies").toString(), this.skin);
		label.setPosition(Gdx.graphics.getWidth() / 2, yPos, Align.center);
		this.stage.addActor(label);

		yPos -= ROW_SIZE + 5.0f;

		label = new Label("XP: " + params.get("XP").toString(), this.skin);
		label.setPosition(Gdx.graphics.getWidth() / 2, yPos, Align.center);
		this.stage.addActor(label);

		yPos -= ROW_SIZE + 5.0f;

		label = new Label("Level: " + params.get("Level").toString(), this.skin);
		label.setPosition(Gdx.graphics.getWidth() / 2, yPos, Align.center);
		this.stage.addActor(label);


		yPos -= ROW_SIZE + 100.0f;

		TextButton btn = new TextButton("To main menu", this.skin);
		btn.setSize(300, 50);
		btn.setPosition(Gdx.graphics.getWidth() / 2 - 150, yPos);
		this.stage.addActor(btn);

		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				StateManager.Instance.setActiveState("MenuState");
			}
		});

		savePref.flush();
	}

	@Override
	public void deactivate() {
		this.stage.clear();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}
