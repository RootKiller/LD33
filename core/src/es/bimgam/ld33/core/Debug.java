package es.bimgam.ld33.core;

public class Debug {
	public static final boolean RENDER_PHYSICS = false;
	public static final boolean TEST_UI_MENU = false;
	public static final boolean ALLOW_CHEATS = false;

	public static void Log(String message) {
		System.out.println("[debug] " + message);
	}

	public static void Log(Object object) {
		System.out.println("[debug] " + object.toString());
	}

	public static void Assert(boolean condition, String message) {
		if (! condition) {
			Log("[assert] " + message);
		}
	}
}
