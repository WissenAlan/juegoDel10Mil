package utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public enum Dados {

	UNO(new Sprite(new Texture("dados/dado 1.png")), new Sprite(new Texture("dados/dado 1 verde.png")), 1),
	DOS(new Sprite(new Texture("dados/dado 2.png")), new Sprite(new Texture("dados/dado 2 verde.png")), 2),
	TRES(new Sprite(new Texture("dados/dado 3.png")), new Sprite(new Texture("dados/dado 3 verde.png")), 3),
	CUATRO(new Sprite(new Texture("dados/dado 4.png")), new Sprite(new Texture("dados/dado 4 verde.png")), 4),
	CINCO(new Sprite(new Texture("dados/dado 5.png")), new Sprite(new Texture("dados/dado 5 verde.png")), 5),
	SEIS(new Sprite(new Texture("dados/dado 6.png")), new Sprite(new Texture("dados/dado 6 verde.png")), 6);

	private Sprite texturas[] = new Sprite[2];
	private int numero;
	private final float X = 170, Y = 300;

	private Dados(Sprite blanco, Sprite verde, int numero) {
		texturas[0] = blanco;
		texturas[1] = verde;
		for (int i = 0; i < texturas.length; i++) {
			texturas[i].setPosition(X, Y);
		}
		this.numero = numero;
	}

	public int getNumero() {
		return numero;
	}

	public int getPunto(int dados) {
		if (dados + 1 == 1) {
			return 100;
		} else if (dados + 1 == 5) {
			return 50;
		} else {
			return 0;
		}
	}

	public int getTriple() {
		if (numero == 1) {
			return 1000;
		} else {
			return numero * 100;
		}
	}

	public void dibujarDado(SpriteBatch b, boolean verde, int i) {
		for (int j = 0; j < texturas.length; j++) {
			texturas[j].setPosition(X + (i * texturas[j].getWidth()) + (i * 20), Y);
		}
		if (!verde) {
			texturas[0].draw(b);
		} else {
			texturas[1].draw(b);
		}
	}

	public boolean isContable(int dados) {
		if (dados + 1 == 1 || dados + 1 == 5) {
			return true;
		}
		return false;
	}

}
