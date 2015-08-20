package es.bimgam.ld33.core;

/**
 * Created by Eryk on 20.08.2015.
 */
public abstract class Command {
	private String name;

	public Command(String name) {
		this.name = name;
	}

	public boolean run() {
		return false;
	}

	public String getName() {
		return this.name;
	}
}
