package com.wiessen_10kgame.lwjgl3;

import com.wiessen_10kgame.MainCliente;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		return new Lwjgl3Application(new MainCliente(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Juego10Mil");
		configuration.setWindowedMode(800, 600);
		configuration.setWindowIcon("dado128.png", "dado64.png", "dado32.png", "dado16.png");
		configuration.setResizable(false);
		return configuration;
	}
}