package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.ui.componentes.Boton;
import com.wiessen_10kgame.core.modelo.EstadoDado;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.componentes.RenderizadorDados;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;
import com.wiessen_10kgame.ui.componentes.Texto;

import java.util.Arrays;

public class SalaJuego implements Screen {

	private SpriteBatch b;
	private static Texto[] jugadoresTxt;
	private static int[] dados = { -1, -1, -1, -1, -1 };
	private Boton tirarBtn, plantarBtn;
	private Texto ptsTxt, ptsTotalesTxt, ptsTotalesNro, ptsNro, turnoTxt, turnoNombre;
	private static int turno, puntosAcum = 0, indiceTriple = -1, ultimoDado = -1, puntosTotales[];
	@SuppressWarnings("unused")
	private static boolean cambiarNombre = false;
    private static boolean actualizarPuntoRonda = false;
    private static boolean hayGanardor = false;
    private static boolean puedePlantarse = false;
	private String jugador;
	private Entrada e;
	private boolean clicBtn, clicBtn2;
	private RenderizadorDados renderizadorDados;

	public SalaJuego(Texto[] jugadores, String jugador) {
		jugadoresTxt = jugadores;
		this.jugador = jugador;
		int indice = 0;
		boolean fin = false;
		do {
			if (jugadoresTxt[indice] != null) {
				indice++;
			} else {
				fin = true;
			}
		} while (indice < jugadoresTxt.length && !fin);
		puntosTotales = new int[indice];
        /*
		if (MainCliente.hc.isAdmin()) {
			MainCliente.hc.enviarMensaje("TurnoDeQuien?");
		}
         */
	}

	@Override
	public void show() {
        /*
		Gdx.input.setInputProcessor(e);
		tirarBtn = new Boton("Tirar", Color.WHITE, Color.BLACK, 30, true);
		plantarBtn = new Boton("Me planto", Color.WHITE, Color.BLACK, 22, true);
		tirarBtn.setPosition(Gdx.graphics.getWidth() - tirarBtn.getWidth() - 20, 20);
		plantarBtn.setPosition(tirarBtn.getPosicion().x, tirarBtn.getPosicion().y + 20 + plantarBtn.getHeight());
		ptsTotalesTxt = new Texto(Utiles.FUENTE_MENU, 30, Utiles.COLOR_LETRA, "Puntos totales:", Color.WHITE);
		ptsTotalesTxt.setPosicion((float) Gdx.graphics.getWidth() / 2 - ptsTotalesTxt.getWidth() / 2,
				Gdx.graphics.getHeight() - 50);
		turnoTxt = new Texto(Utiles.FUENTE_MENU, 26, Utiles.COLOR_LETRA, "Turno de:", Color.WHITE);
		turnoTxt.setPosicion(20, 20 + turnoTxt.getHeight());
		ptsTxt = new Texto(Utiles.FUENTE_MENU, 26, Color.valueOf("0eac12"), "Puntos:", Color.WHITE);
		ptsTxt.setPosicion(20, turnoTxt.getY() + turnoTxt.getHeight() + 20);

		ptsTotalesNro = new Texto(Utiles.FUENTE_MENU, 30, Utiles.COLOR_LETRA, "0", Color.WHITE);
		ptsTotalesNro.setPosicion(ptsTotalesTxt.getPosicion().x + ptsTotalesTxt.getWidth() + 10,
				ptsTotalesTxt.getPosicion().y);
		turnoNombre = new Texto(Utiles.FUENTE_MENU, 26, Utiles.COLOR_LETRA, "0", Color.WHITE);
		turnoNombre.setPosicion(turnoTxt.getPosicion().x + turnoTxt.getWidth() + 10, turnoTxt.getPosicion().y);
		ptsNro = new Texto(Utiles.FUENTE_MENU, 26, Color.valueOf("0eac12"), "0", Color.WHITE);
		ptsNro.setPosicion(ptsTxt.getPosicion().x + ptsTxt.getWidth() + 10, ptsTxt.getPosicion().y);

         */
	}

	@Override
	public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
		verificarGanador();
		verificarCambioNombre();
		verificarPuntoRonda();
		b.begin();
		if (jugadoresTxt[turno] != null && puntosTotales != null) {
			if (jugador.equalsIgnoreCase(jugadoresTxt[turno].getTexto())) {
				if (!tirarBtn.isHabilitado()) {
					tirarBtn.setHabilitado(true);
				}
				turnoJugador();
			}
			if (puedePlantarse && !plantarBtn.isHabilitado()) {
				plantarBtn.setHabilitado(true);
			} else if (!puedePlantarse && plantarBtn.isHabilitado()) {
				plantarBtn.setHabilitado(false);
			}
			mostrarDados();
			dibujarBotones();
			dibujarTextos();
		}
		b.end();
	}

	private void verificarPuntoRonda() {
		if (actualizarPuntoRonda) {
			ptsNro.setTexto("" + puntosAcum);
			actualizarPuntoRonda = false;
		}
	}

	private void verificarCambioNombre() {
		if (cambiarNombre && jugadoresTxt[turno] != null && puntosTotales.length > turno) {
			cambiarNombre = false;
			turnoNombre.setTexto(jugadoresTxt[turno].getTexto());
			ptsTotalesNro.setTexto(Integer.toString(puntosTotales[turno]));
			ptsNro.setTexto("0");
			if (tirarBtn.isHabilitado()) {
				tirarBtn.setHabilitado(false);
			}
			if (plantarBtn.isHabilitado()) {
				plantarBtn.setHabilitado(false);
			}
			if (puedePlantarse) {
				puedePlantarse = false;
			}
			reiniciarVariables();
			dados = new int[5];
            Arrays.fill(dados, -1);
		}
	}

	private void verificarGanador() {
		if (hayGanardor) {
			hayGanardor = false;
			ScreenManager.getInstance().setScreen(new PantallaGanador(jugadoresTxt, turno, puntosTotales));
		}
	}

	private void turnoJugador() {
		if (tirarBtn.estaDentro(e)) {
			if (e.isTouch() && !clicBtn) {
				tirarBtn.setHabilitado(false);
				clicBtn = true;
				Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
				//MainCliente.hc.enviarMensaje("ClicTirarBtn");
			}
		} else if (plantarBtn.estaDentro(e)) {
			if (e.isTouch() && !clicBtn2) {
				clicBtn2 = true;
				tirarBtn.setHabilitado(false);
				plantarBtn.setHabilitado(false);
				Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
				//MainCliente.hc.enviarMensaje("SumarPuntosTotales");
			}
		}
		if (clicBtn && !e.isTouch()) {
			clicBtn = false;
		}
		if (clicBtn2 && !e.isTouch()) {
			clicBtn2 = false;
		}
	}

	private void mostrarDados() {
		if (dados[0] != -1 && ultimoDado != -1) {
			if (renderizadorDados == null) {
				renderizadorDados = new RenderizadorDados();
			}
			int contar = inicializarContar();
			for (int i = 0; i < ultimoDado; i++) {
				renderizadorDados.dibujarDado(b, dados[i], true, i);
			}
			if (indiceTriple != -1) {
				for (int i = ultimoDado; i < contar; i++) {
					renderizadorDados.dibujarDado(b, dados[i], true, i);
				}
			}
			for (int i = contar; i < dados.length; i++) {
				renderizadorDados.dibujarDado(b, dados[i], EstadoDado.values()[dados[i]].isContable(dados[i]), i);
			}
		}
	}

	private void dibujarTextos() {
		ptsTotalesTxt.dibujar(b);
		ptsTotalesNro.dibujar(b);
		turnoNombre.dibujar(b);
		turnoTxt.dibujar(b);
		ptsTxt.dibujar(b);
		ptsNro.dibujar(b);
	}

	private void dibujarBotones() {
		plantarBtn.draw(b);
		tirarBtn.draw(b);
	}

	public static void setTurno(int turno) {
		SalaJuego.turno = turno;
		cambiarNombre = true;
	}

	public static void actualizarPuntoRonda(int puntos) {
		actualizarPuntoRonda = true;
		puntosAcum = puntos;
	}

	public static void actualizarPuntoTotal(int puntaje, int turnoJP) {
		puntosTotales[turnoJP] = puntaje;
	}

	private int inicializarContar() {
		int contar = ultimoDado;
		if (indiceTriple != -1) {
			return contar += 3;
		}
		return contar;
	}

	public static void HayGanador() {
		hayGanardor = true;
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
		if (renderizadorDados != null) {
			renderizadorDados.dispose();
			renderizadorDados = null;
		}
	}

	public static void actualizarDados(int[] dadosRec) {
		dados = dadosRec;
        boolean actualizarDados = true;
	}

	public static void puedePlantarse(boolean c) {
		puedePlantarse = c;
	}

	public static void setIndiceTriple(int i) {
		indiceTriple = i;
	}

	public static void reiniciarVariables() {
		ultimoDado = -1;
		indiceTriple = -1;
	}

	public static void setUltimoDado(int i) {
		ultimoDado = i;
	}

	public static void setJugadores(int i, String jugador) {
		jugadoresTxt[i].setTexto(jugador);
	}

	public static void recrearPuntosTotales(int nroCliente) {
		int[] auxPuntosTotales = new int[puntosTotales.length - 1];
		for (int j = nroCliente; j < puntosTotales.length - 1; j++) {
			puntosTotales[j] = puntosTotales[j + 1];
			System.out.println(puntosTotales[j] + "  " + j);
		}
        System.arraycopy(puntosTotales, 0, auxPuntosTotales, 0, auxPuntosTotales.length);
		puntosTotales = null;
		puntosTotales = new int[auxPuntosTotales.length];
        System.arraycopy(auxPuntosTotales, 0, puntosTotales, 0, auxPuntosTotales.length);
        for (int puntosTotale : puntosTotales) {
            System.out.println(puntosTotale);
        }
	}

}
