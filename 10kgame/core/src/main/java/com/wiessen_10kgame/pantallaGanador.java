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

public class pantallaGanador implements Screen {

	private Texto jugadoresTxt[], ganadorTxt;
	private int turno, puntosTotales[];
	private boolean clicBtn = false;
	private SpriteBatch b = Utiles.batch;
	private Boton salirbtn;
	private Entrada e = Utiles.e;

	public pantallaGanador(Texto[] jugadoresTxt, int turno, int[] puntosTotales) {
		this.jugadoresTxt = jugadoresTxt.clone();
		this.turno = turno;
		this.puntosTotales = puntosTotales.clone();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(e);
		ganadorTxt = new Texto(Utiles.FUENTE_MENU, 32, Color.WHITE, "Ganador es: " + jugadoresTxt[turno].getTexto(),
				Utiles.COLOR_LETRA);
		ganadorTxt.setPosicion(Gdx.graphics.getWidth() / 2 - ganadorTxt.getWidth() / 2, Gdx.graphics.getHeight() - 100);
		for (int i = 0; i < puntosTotales.length; i++) {
			System.out.println(jugadoresTxt[i].getTexto() + ": " + puntosTotales[i]);
			jugadoresTxt[i] = new Texto(Utiles.FUENTE_MENU, 28, Color.WHITE,
					jugadoresTxt[i].getTexto() + ": " + puntosTotales[i], Utiles.COLOR_LETRA);
			jugadoresTxt[i].setPosicion(100, ganadorTxt.getY() - 100 - (i * 50));
		}
		salirbtn = new Boton("Salir", Color.WHITE, Color.BLACK, 30, true);
		salirbtn.setPosition(Gdx.graphics.getWidth() - salirbtn.getWidth() - 20, 20);
	}

	@Override
	public void render(float delta) {
		Utiles.limpiarPantalla();
		verificarSalirBtn();
		b.begin();
		dibujarTextos();
		salirbtn.draw(b);
		b.end();
	}

	private void verificarSalirBtn() {
		if (salirbtn.estaDentro(e)) {
			if (e.isTouch() && !clicBtn) {
				salirbtn.setHabilitado(false);
				clicBtn = true;
				Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
				MainCliente.hc.terminar();
				ScreenManager.getInstance().showScreenWindow(new MenuPrincipal());
			}
		}
		if (clicBtn && !e.isTouch()) {
			clicBtn = false;
		}
	}

	private void dibujarTextos() {
		ganadorTxt.dibujar(b);
		for (int i = 0; i < puntosTotales.length; i++) {
			jugadoresTxt[i].dibujar(b);
		}
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

}
