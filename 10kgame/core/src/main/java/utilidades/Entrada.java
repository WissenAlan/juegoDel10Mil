package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import teclas.Tecla;
import teclas.TeclaDef;

public class Entrada implements InputProcessor {

	private boolean arriba = false, abajo = false, izquierda = false, derecha = false, touch = false,
			escribiendo = false, escribeMas = true;
	private int mouseX = 0;
	private int mouseY = 0;
	private int button = 0;
	private int keycode, charcode;
	private String texto = "";
	private int indice = 0;

	@Override
	public boolean keyTyped(char character) {
		if (escribiendo) {
			this.charcode = character;
			TeclaDef tecla = new TeclaDef(keycode, charcode);
			if (tecla.esIgual(Tecla.BACKSPACE)) {
				if (texto.length() > 0) {
					String cadena1 = (indice > 0) ? texto.substring(0, indice - 1) : "";
					String cadena2 = texto.substring(indice, texto.length());
					texto = cadena1 + cadena2;
					if (indice > 0) {
						indice--;
					} else {
						indice = 0;
					}
//					Sonidos.playAudio(Sonidos.KEYERASE.getAudio());
				}
			} else if (tecla.esIgual(Tecla.DEL)) {
				if (indice < texto.length()) {
					String cadena1 = (indice > 0) ? texto.substring(0, indice) : "";
					String cadena2 = texto.substring(indice + 1, texto.length());
					texto = cadena1 + cadena2;
//					Sonidos.playAudio(Sonidos.KEYERASE.getAudio());
				}
			}
			if (escribeMas) {
				if (tecla.esIgual(Tecla.ESPACIO)) {
					String cadena1 = (indice > 0) ? texto.substring(0, indice) : "";
					String cadena2 = texto.substring(indice, texto.length());
					texto = cadena1 + " " + cadena2;
					indice++;
//					Sonidos.playAudio(Sonidos.KEYCLICK.getAudio());
				} else if (charcode > 27) { // si es caracter
					String cadena1 = (indice > 0) ? texto.substring(0, indice) : "";
					String cadena2 = texto.substring(indice, texto.length());
					texto = cadena1 + String.valueOf(character) + cadena2;
					indice++;
//					Sonidos.playAudio(Sonidos.KEYCLICK.getAudio());
				}
			}
		}
		return false;
	}

	public void setEscribiendo(boolean escribiendo) {
		this.escribiendo = escribiendo;
	}

	public void setEscribeMas(boolean escribeMas) {
		this.escribeMas = escribeMas;
	}

	public boolean getEscribeMas() {
		return this.escribeMas;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public boolean keyDown(int keycode) {
		this.keycode = keycode;
		if (keycode == Keys.DOWN) {
			arriba = true;
			indice = texto.length();
		}
		if (keycode == Keys.UP) {
			abajo = true;
			indice = 0;
		}
		if (keycode == Keys.LEFT) {
			izquierda = true;
			if (indice > 0)
				indice--;
		}
		if (keycode == Keys.RIGHT) {
			derecha = true;
			if (indice < texto.length())
				indice++;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.DOWN) {
			arriba = false;
		}
		if (keycode == Keys.UP) {
			abajo = false;
		}
		if (keycode == Keys.LEFT) {
			izquierda = false;
		}
		if (keycode == Keys.RIGHT) {
			derecha = false;
		}
		return false;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
		indice = texto.length();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touch = true;
		this.button = button;
		return false;
	}

	public int getButton() {
		return button;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touch = false;
		return false;
	}

	public boolean isTouch() {
		return touch;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		boolean seMovio;
		if (mouseX != screenX || mouseY != screenY) {
			mouseX = screenX;
			mouseY = screenY;
			seMovio = true;
		} else {
			seMovio = false;
		}
		return seMovio;
	}

	public boolean mouseMovedX(int screenX) {
		boolean seMovio;
		if (mouseX != screenX) {
			mouseX = screenX;
			seMovio = true;
		} else {
			seMovio = false;
		}
		return seMovio;
	}

	public boolean mouseMovedY(int screenY) {
		boolean seMovio;
		if (mouseY != screenY) {
			mouseY = screenY;
			seMovio = true;
		} else {
			seMovio = false;
		}
		return seMovio;
	}

	public boolean isArriba() {
		return arriba;
	}

	public boolean isAbajo() {
		return abajo;
	}

	public boolean isIzquierda() {
		return izquierda;
	}

	public boolean isDerecha() {
		return derecha;
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public int getMouseY() {
		return (Gdx.graphics.getHeight() - mouseY);
	}


	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
