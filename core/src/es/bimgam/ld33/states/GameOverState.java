package es.bimgam.ld33.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import es.bimgam.ld33.core.Debug;

public class GameOverState extends State {

	private static final float ROW_SIZE = 40.0f;

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
