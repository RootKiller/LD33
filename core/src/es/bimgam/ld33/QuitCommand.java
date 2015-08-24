package es.bimgam.ld33;

import com.badlogic.gdx.Gdx;
import es.bimgam.ld33.core.Command;
import es.bimgam.ld33.states.InGameState;
import es.bimgam.ld33.states.StateManager;

public class QuitCommand extends Command {
	public QuitCommand() {
		super("quit");
	}

	@Override
	public void run() {
		InGameState igs = StateManager.Instance.getActiveStateSafe(InGameState.class);
		if (igs != null) {
			igs.save();
		}
		Gdx.app.exit();
	}
}
