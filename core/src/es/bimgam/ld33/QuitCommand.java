package es.bimgam.ld33;

import com.badlogic.gdx.Gdx;
import es.bimgam.ld33.core.Command;

public class QuitCommand extends Command {
	public QuitCommand() {
		super("quit");
	}

	@Override
	public void run() {
		Gdx.app.exit();
	}
}
