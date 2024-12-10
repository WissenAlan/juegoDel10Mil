package com.wiessen_10kgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import utilidades.Entrada;
import utilidades.ScreenManager;
import utilidades.Sonidos;
import utilidades.Texto;
import utilidades.Utiles;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class MenuPrincipal implements Screen {

	private Entrada e = Utiles.e;
	private SpriteBatch b = Utiles.batch;
	private Texto titulo, jugar, salir, reglas, nombre, nombretxt;
	private boolean primeraVez = true, primeraVez2 = true, play = false, flag;
	private int aux = -1;
	private Sprite nombreSpr, barra;

	@Override
	public void show() {
		Gdx.input.setInputProcessor(e);
		titulo = new Texto(Utiles.FUENTE_MENU, 32, Color.WHITE, "EL JUEGO DEL 10.000", Utiles.COLOR_LETRA);
		titulo.setPosicion(Gdx.graphics.getWidth() / 2 - titulo.getWidth() / 2, Gdx.graphics.getHeight() - 100);
		jugar = new Texto(Utiles.FUENTE_MENU, 26, Color.WHITE, "JUGAR", Utiles.COLOR_LETRA);
		jugar.setPosicion(Gdx.graphics.getWidth() / 2 - jugar.getWidth() / 2, titulo.getY() - 160);
		reglas = new Texto(Utiles.FUENTE_MENU, 26, Color.WHITE, "REGLAS", Utiles.COLOR_LETRA);
		reglas.setPosicion(Gdx.graphics.getWidth() / 2 - reglas.getWidth() / 2, jugar.getY() - 90);
		salir = new Texto(Utiles.FUENTE_MENU, 26, Color.WHITE, "SALIR", Utiles.COLOR_LETRA);
		salir.setPosicion(Gdx.graphics.getWidth() / 2 - salir.getWidth() / 2, reglas.getY() - 90);

	}

	@Override
	public void render(float delta) {
		Utiles.limpiarPantalla();
		b.begin();
		titulo.dibujar(b);
		jugar.dibujar(b);
		reglas.dibujar(b);
		salir.dibujar(b);
		controlarMousePos();
		b.end();
	}

	private void controlarMousePos() {
		Texto variable;
		for (int i = 0; i < 3; i++) {
			variable = igualarVariable(i);
			if (play) {
				ingresarNombre();
			} else if (Utiles.mouseDentroDeTexto(e, variable.getPosicion(), variable.getWidth(), variable.getHeight())) {
				if (primeraVez) {
					Sonidos.playAudio(Sonidos.SOFTCLICK.getAudio());
					primeraVez = false;
					aux = i;
				}
				Utiles.setearColorTexto(variable, Utiles.COLOR_LETRA, Color.WHITE);
				if (e.isTouch()) {
					Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
					ejecutarAccion(variable);
				}
			} else if (!primeraVez && aux == i) {
				primeraVez = true;
				primeraVez2 = true;
				Utiles.setearColorTexto(variable, Color.WHITE, Utiles.COLOR_LETRA);
			}

		}

	}

	private void ejecutarAccion(Texto variable) {
		if (variable == salir && !play) {
			Gdx.app.exit();
		} else if (variable == reglas && !play) {
			mostrarReglas();
		} else if (variable == jugar || play) {
			play = true;
			nombre = new Texto(Utiles.FUENTE_MENU, 26, Color.WHITE);
		}
	}

	private void ingresarNombre() {
		if (primeraVez2) {
			primeraVez2 = false;
			nombreSpr = new Sprite(new Texture("sprites/ingresesunombre2.jpg"));
			nombreSpr.setPosition(Gdx.graphics.getWidth() / 2 - nombreSpr.getWidth() / 2,
					Gdx.graphics.getHeight() / 2 - nombreSpr.getHeight() / 2);
			nombretxt = new Texto(Utiles.FUENTE_MENU, 32, Color.WHITE, "INGRESE SU NOMBRE", Utiles.COLOR_LETRA);
			nombretxt.setPosicion(nombreSpr.getX() + nombreSpr.getWidth() / 2 - nombretxt.getWidth() / 2,
					nombreSpr.getY() + nombreSpr.getHeight() - 40);
			barra = new Sprite(new Texture(Utiles.BARRA_ESCRIBIR));
			barra.setSize(barra.getWidth(), 21);
			nombre.setPosicion(nombreSpr.getX() + 103, nombreSpr.getY() + 143);
		}
		nombreSpr.draw(b);
		nombretxt.dibujar(b);
		if (nombre.getTexto().length() < 16) {
			e.setEscribiendo(true);
			nombre.setTexto(e.getTexto());
			barra.setPosition(nombreSpr.getX() + 104 + nombre.getWidth(), nombreSpr.getY() + 122);
			barra.draw(b);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DEL)) {
			e.setTexto(e.getTexto().substring(0, e.getTexto().length() - 1));
			nombre.setTexto(e.getTexto());
		} else {
			e.setEscribiendo(false);
		}
		if (!nombre.getTexto().isEmpty()) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !flag) {
				flag = true;
				MainCliente.crearHilo(nombre.getTexto());
				ScreenManager.getInstance().showScreenWindow(new SalaEspera(nombre.getTexto()));
			}
		}
		nombre.dibujar(b);
	}

	private Texto igualarVariable(int i) {
		Texto variable = null;
		switch (i) {
		case 0:
			variable = jugar;
			break;
		case 1:
			variable = reglas;
			break;
		case 2:
			variable = salir;
			break;
		}
		return variable;

	}

	private void mostrarReglas() {

	}

	@Override
	public void resize(int width, int height) {
		// Resize your screen here. The parameters represent the new window size.
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		// Destroy screen's assets here.
	}
}