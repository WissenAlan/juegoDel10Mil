package com.wiessen_10kgame;

import com.badlogic.gdx.Game;

import red.HiloCliente;
import utilidades.ScreenManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class MainCliente extends Game {

	public static HiloCliente hc;

	@Override
	public void create() {
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreenWindow(new MenuPrincipal());
	}

	@Override
	public void dispose() {
		if (hc != null) {
			if (hc.isAlive()) {
				hc.terminar();
				hc.interrupt();
			}
		}
	}

	public static void crearHilo(String nombre) {
		hc = new HiloCliente();
		hc.start();
		hc.enviarMensaje("nombre%" + nombre);
	}
}