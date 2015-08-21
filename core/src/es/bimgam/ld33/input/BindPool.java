package es.bimgam.ld33.input;

import java.util.ArrayList;

public class BindPool {
	private ArrayList<Bind> binds = new ArrayList<Bind>();

	public BindPool() {
	}

	public void register(Bind bind) {
		this.binds.add(bind);
	}

	public void unregister(Bind bind) {
		this.binds.remove(bind);
	}

	public void tick() {
		for (Bind bind : this.binds) {
			bind.tick();
		}
	}
}
