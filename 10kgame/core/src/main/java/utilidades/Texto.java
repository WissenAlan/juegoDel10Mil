package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

public class Texto {

	private FreeTypeFontGenerator generador;
	private FreeTypeFontParameter parametro;
	private BitmapFont fuente;
	private GlyphLayout layout;
	private String texto = "";
	private Vector2 posicion;

	public Texto(String rutaFuente, int size, Color color) {
		generarTexto(rutaFuente, size, color);
	}

	public Texto(String rutaFuente, int size, Color color, String texto, Color borderColor) {
		generarTexto(rutaFuente, size, color, texto, borderColor);
	}

	private void generarTexto(String rutaFuente, int size, Color color) {
		generador = new FreeTypeFontGenerator(Gdx.files.internal(rutaFuente));
		parametro = new FreeTypeFontParameter();
		parametro.size = size;
//		parametro.borderWidth = 1;
		parametro.borderColor = color;
		parametro.color = color;
		fuente = generador.generateFont(parametro);
		layout = new GlyphLayout();
		posicion = new Vector2(0, 0);
	}

	private void generarTexto(String rutaFuente, int size, Color color, String texto, Color borderColor) {
		generador = new FreeTypeFontGenerator(Gdx.files.internal(rutaFuente));
		parametro = new FreeTypeFontParameter();
		parametro.size = size;
		parametro.borderWidth = 2;
		parametro.borderColor = borderColor;
		parametro.color = color;
		fuente = generador.generateFont(parametro);
		layout = new GlyphLayout();
		setTexto(texto);
		posicion = new Vector2();
	}

	public void dibujar(SpriteBatch b) {
		fuente.draw(b, texto, posicion.x, posicion.y);
	}

	public void setTexto(String texto) {
		this.texto = texto;
		layout.setText(fuente, texto);
	}

	public void setPosicion(Vector2 posicion) {
		this.posicion = posicion;
	}

	public Vector2 getPosicion() {
		return posicion;
	}

	public float getX() {
		return posicion.x;
	}

	public String getTexto() {
		return texto;
	}

	public float getY() {
		return posicion.y;
	}

	public void setColor(Color color) {
		this.parametro.color = color;
		fuente = generador.generateFont(parametro);
	}

	public float getWidth() {
		return layout.width;
	}

	public float getHeight() {
		return layout.height;
	}

	public Vector2 getDimension() {
		return new Vector2(layout.width, layout.height);
	}

	public void setPosicion(float x, float y) {
		this.posicion.x = x;
		this.posicion.y = y;
	}

	public void setBorderWidth(int tamanio) {
		this.parametro.borderWidth = tamanio;
		fuente = generador.generateFont(parametro);
	}

	public void setBorderColor(Color color) {
		this.parametro.borderColor = color;
		fuente = generador.generateFont(parametro);
	}

	public void dispose() {
		generador.dispose();
		fuente.dispose();
	}

	public Color getColor() {
		return parametro.color;
	}

}
