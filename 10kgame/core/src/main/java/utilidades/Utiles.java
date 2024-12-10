package utilidades;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Utiles {

	public static final Random R = new Random();
	public static SpriteBatch batch = new SpriteBatch();
	public static final Entrada e = new Entrada();
	public static final String FUENTE_MENU = "fuentes/04B_30__.TTF";
	public static final Color COLOR_LETRA = Color.valueOf("7b0707");
	public static final String BARRA_ESCRIBIR = "sprites/escribirIndice.png";

	public static void limpiarPantalla() {
		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public static void setEntrada(Entrada e) {
		Gdx.input.setInputProcessor(e);
	}

	public static boolean mouseDentroDe(Entrada entradas, Vector2 posicion, float width, float height) {
		if (entradas.getMouseX() > posicion.x && entradas.getMouseX() < (posicion.x + width)
				&& (entradas.getMouseY() > posicion.y && entradas.getMouseY() < (posicion.y + height))) {
			return true;
		}
		return false;
	}

	public static boolean mouseDentroDeTexto(Entrada entradas, Vector2 posicion, float width, float height) {
		if (entradas.getMouseX() > posicion.x && entradas.getMouseX() < (posicion.x + width)
				&& (entradas.getMouseY() > posicion.y - height
						&& entradas.getMouseY() < (posicion.y + height) - height)) {
			return true;
		}
		return false;
	}

	public static void setearColorTexto(Texto variable, Color colorLetra, Color colorBorde) {
		variable.setColor(colorLetra);
		variable.setBorderColor(colorBorde);
	}

}
