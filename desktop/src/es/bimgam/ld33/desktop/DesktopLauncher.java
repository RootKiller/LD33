package es.bimgam.ld33.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import es.bimgam.ld33.LD33;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 780;
		config.resizable = false;
		config.vSyncEnabled = false;
		new LwjglApplication(new LD33(), config);
	}
}
