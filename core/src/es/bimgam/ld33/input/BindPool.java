package es.bimgam.ld33.input;

import java.util.ArrayList;

/**
 * Created by Eryk on 20.08.2015.
 */
public class BindPool {
	private ArrayList<Bind> binds = new ArrayList<Bind>();

	public BindPool() {
	}

	public void register(Bind bind) {
		binds.add(bind);
	}

	public void tick() {
		for (Bind bind : binds) {
			bind.tick();
		}
	}
}
