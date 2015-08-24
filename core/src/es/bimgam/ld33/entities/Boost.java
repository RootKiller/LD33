package es.bimgam.ld33.entities;

public enum Boost {
	TRIPLE_BULLETS(0, 10.0f), SHIELD(1, 20.0f);

	public final int id;
	public final float time;

	Boost(int id, float time) {
		this.id = id;
		this.time = time;
	}
}
