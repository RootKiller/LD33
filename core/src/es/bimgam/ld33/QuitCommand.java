package es.bimgam.ld33;

import com.badlogic.gdx.Gdx;
import es.bimgam.ld33.core.Command;

/**
 * Created by Eryk on 20.08.2015.
 */
public class QuitCommand extends Command {
	public QuitCommand() {
		super("quit");
	}

	@Override
	public boolean run() {
		Gdx.app.exit();
		return true;
	}
}
