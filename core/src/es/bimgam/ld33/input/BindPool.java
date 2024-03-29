package es.bimgam.ld33.input;

import java.util.ArrayList;

public class BindPool {
	private ArrayList<Bind> binds = new ArrayList<Bind>();

	public static BindPool Instance = null;

	public BindPool() {
		Instance = this;
	}

	public void release() {
		this.binds.clear();
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
