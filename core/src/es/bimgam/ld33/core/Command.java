package es.bimgam.ld33.core;

public abstract class Command implements Runnable {
	private String name;

	public Command(String name) {
		this.name = name;
	}

	@Override
	public void run() {
	}

	public String getName() {
		return this.name;
	}
}
