package utilidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Boton {

	private boolean habilitado = true, primeraVez;
	private Texto texto;
	private Sprite texturas[] = new Sprite[2];
	private Vector2 pos;

	public Boton(String texto, Color colorLetra, Color colorBorde, int tamanio, boolean habilitado) {
		this.texto = new Texto(Utiles.FUENTE_MENU, tamanio, colorLetra, texto, colorBorde);
		this.habilitado = habilitado;
		texturas[0] = new Sprite(new Texture("sprites/boton0.png"));
		texturas[1] = new Sprite(new Texture("sprites/boton1.png"));
		pos = new Vector2();

	}

	public void setPosition(float x, float y) {
		pos = new Vector2(x, y);
		for (int i = 0; i < texturas.length; i++) {
			texturas[i].setPosition(pos.x, pos.y);
		}
		texto.setPosicion(texturas[0].getX() + ((texturas[0].getWidth() - texto.getWidth()) / 2),
				texturas[0].getY() + texto.getHeight() + ((texturas[0].getHeight() - texto.getHeight()) / 2));
	}

	public boolean estaDentro(Entrada e) {
		if (Utiles.mouseDentroDe(e, pos, getWidth(), getHeight()) && isHabilitado()) {
			if (!primeraVez) {
				Sonidos.playAudio(Sonidos.SOFTCLICK.getAudio());
				primeraVez = true;
				Utiles.setearColorTexto(texto, Utiles.COLOR_LETRA, Color.WHITE);
			}
			return true;
		} else if (primeraVez) {
			primeraVez = false;
			Utiles.setearColorTexto(texto, Color.WHITE, Color.BLACK);
		}
		return false;
	}

	public float getWidth() {
		return texturas[1].getWidth();
	}

	public float getHeight() {
		return texturas[1].getHeight();
	}

	public void draw(SpriteBatch b) {
		if (habilitado) {
			texturas[1].draw(b);
		} else {
			texturas[0].draw(b);
		}
		texto.dibujar(b);
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
		if (habilitado && texto.getColor() != Color.valueOf("ffffff")) {
			texto.setColor(Color.valueOf("ffffff"));
		} else if (!habilitado && texto.getColor() != Color.valueOf("4b4b4b")) {
			texto.setColor(Color.valueOf("4b4b4b"));
		}
	}

	public Vector2 getPosicion() {
		return pos;
	}

	public Texto getTexto() {
		return texto;
	}
}
