package com.wiessen_10kgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import utilidades.Boton;
import utilidades.Entrada;
import utilidades.ScreenManager;
import utilidades.Sonidos;
import utilidades.Texto;
import utilidades.Utiles;

public class SalaEspera implements Screen {

	private boolean primeraVez = false, clicBtn = false;
	private static boolean crear, empezar = false;
	private String jugador;
	public static String jugadores[] = new String[6];
	private int i = 0;
	private SpriteBatch b = Utiles.batch;
	private static Texto jugadoresTxt[] = new Texto[6];
	private Texto participantesTxt;
	private Boton empezarBtn;
	private Entrada e = Utiles.e;

	public SalaEspera(String jugador) {
		this.jugador = jugador;
	}

	@Override
	public void show() {
		participantesTxt = new Texto(Utiles.FUENTE_MENU, 32, Color.WHITE, "Participantes:", Utiles.COLOR_LETRA);
		participantesTxt.setPosicion(100, Gdx.graphics.getHeight() - 50);
		Gdx.input.setInputProcessor(e);
	}

	@Override
	public void render(float delta) {
		Utiles.limpiarPantalla();
		b.begin();
		if (MainCliente.hc.isAdmin()) {
			procesarAdmin();
		}
		if (crear) {
			crearTexto();
			crear = false;
		}
		if (empezar) {
			empezar = false;
			ScreenManager.getInstance().showScreenWindow(new SalaJuego(jugadoresTxt, jugador));
		}

		mostrarJugadores();
		b.end();

	}

	private void procesarAdmin() {
		if (!primeraVez) {
			primeraVez = true;
			empezarBtn = new Boton("Empezar", Color.WHITE, Color.BLACK, 28, true);
			empezarBtn.setPosition(Gdx.graphics.getWidth() - empezarBtn.getWidth() - 50, 50);
		}
		if (jugadoresTxt[1] == null && empezarBtn.isHabilitado()) {
			empezarBtn.setHabilitado(false);
		} else if (jugadoresTxt[1] != null && !empezarBtn.isHabilitado()) {
			empezarBtn.setHabilitado(true);
		}
		empezarBtn.draw(b);
		if (empezarBtn.estaDentro(e)) {
			if (e.isTouch() && !clicBtn) {
				clicBtn = true;
				Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
				MainCliente.hc.enviarMensaje("Empezar");
			}
		}
	}

	private void mostrarJugadores() {
		participantesTxt.dibujar(b);
		i = 0;
		boolean flag = false;
		do {
			if (jugadoresTxt[i] != null) {
				jugadoresTxt[i].dibujar(b);
			} else {
				flag = true;
			}
			i++;
		} while (!flag && i < jugadoresTxt.length);
	}

	public void crearTexto() {
		int u = 0;
		boolean fin = false;
		do {
			if (jugadoresTxt[u] == null && jugadores[u] != null) {
				jugadoresTxt[u] = new Texto(Utiles.FUENTE_MENU, 28, Color.WHITE, jugadores[u], Utiles.COLOR_LETRA);
				jugadoresTxt[u].setPosicion(100, participantesTxt.getPosicion().y - (80 * (u + 1)));
			} else if (jugadores[u] == null) {
				fin = true;
			}
			u++;
		} while (!fin && u < jugadoresTxt.length);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {

	}

	public static void crear() {
		crear = true;
	}

	public static void empezar() {
		empezar = true;
	}

	public static void setJugadores(int indice, String jp) {
		SalaEspera.jugadores[indice] = jp;
		jugadoresTxt[indice] = null;
	}

	public static String[] getJugadores() {
		return jugadores;
	}

}
