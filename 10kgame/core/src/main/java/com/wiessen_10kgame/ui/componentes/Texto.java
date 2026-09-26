package com.wiessen_10kgame.ui.componentes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Texto {

    private FreeTypeFontGenerator generador;
    private FreeTypeFontParameter parametro;
    private BitmapFont fuente;
    private GlyphLayout layout;
    private String texto = "";
    private Vector2 posicion;
    private final Map<String, BitmapFont> fuentesCache = new HashMap<>();

    public Texto(String rutaFuente, int size, Color color) {
        generarTexto(rutaFuente, size, color);
    }

    public Texto(String rutaFuente, int size, Color color, String texto, Color borderColor) {
        generarTexto(rutaFuente, size, color, texto, borderColor);
    }

    private String generarClave(Color color, Color borderColor, float borderWidth) {
        int c = color != null ? color.toIntBits() : 0;
        int bc = borderColor != null ? borderColor.toIntBits() : 0;
        return c + "_" + bc + "_" + borderWidth;
    }

    private BitmapFont obtenerOCrearFuente(Color color, Color borderColor, float borderWidth) {
        String clave = generarClave(color, borderColor, borderWidth);
        BitmapFont f = fuentesCache.get(clave);
        if (f == null) {
            parametro.color = color != null ? new Color(color) : Color.WHITE;
            parametro.borderColor = borderColor != null ? new Color(borderColor) : Color.CLEAR;
            parametro.borderWidth = borderWidth;
            f = generador.generateFont(parametro);
            fuentesCache.put(clave, f);
        }
        return f;
    }

    private void generarTexto(String rutaFuente, int size, Color color) {
        generador = new FreeTypeFontGenerator(Gdx.files.internal(rutaFuente));
        parametro = new FreeTypeFontParameter();
        parametro.size = size;
        parametro.borderColor = color != null ? new Color(color) : Color.CLEAR;
        parametro.color = color != null ? new Color(color) : Color.WHITE;
        fuente = obtenerOCrearFuente(parametro.color, parametro.borderColor, parametro.borderWidth);
        layout = new GlyphLayout();
        posicion = new Vector2(0, 0);
    }

    private void generarTexto(String rutaFuente, int size, Color color, String texto, Color borderColor) {
        generador = new FreeTypeFontGenerator(Gdx.files.internal(rutaFuente));
        parametro = new FreeTypeFontParameter();
        parametro.size = size;
        parametro.borderWidth = 2;
        parametro.borderColor = borderColor != null ? new Color(borderColor) : Color.CLEAR;
        parametro.color = color != null ? new Color(color) : Color.WHITE;
        fuente = obtenerOCrearFuente(parametro.color, parametro.borderColor, parametro.borderWidth);
        layout = new GlyphLayout();
        setTexto(texto);
        posicion = new Vector2();
    }

    public void dibujar(SpriteBatch b) {
        if (fuente != null && texto != null) {
            fuente.draw(b, texto, posicion.x, posicion.y);
        }
    }

    public void setTexto(String texto) {
        this.texto = texto != null ? texto : "";
        if (fuente != null && layout != null) {
            layout.setText(fuente, this.texto);
        }
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
        if (color == null || color.equals(this.parametro.color)) return;
        this.parametro.color = new Color(color);
        fuente = obtenerOCrearFuente(this.parametro.color, this.parametro.borderColor, this.parametro.borderWidth);
        if (layout != null && texto != null) {
            layout.setText(fuente, texto);
        }
    }

    public float getWidth() {
        return layout != null ? layout.width : 0;
    }

    public float getHeight() {
        return layout != null ? layout.height : 0;
    }

    public Vector2 getDimension() {
        return new Vector2(getWidth(), getHeight());
    }

    public void setPosicion(float x, float y) {
        this.posicion.x = x;
        this.posicion.y = y;
    }

    public void setBorderWidth(int tamanio) {
        if (this.parametro.borderWidth == tamanio) return;
        this.parametro.borderWidth = tamanio;
        fuente = obtenerOCrearFuente(this.parametro.color, this.parametro.borderColor, this.parametro.borderWidth);
        if (layout != null && texto != null) {
            layout.setText(fuente, texto);
        }
    }

    public void setBorderColor(Color color) {
        if (color == null || color.equals(this.parametro.borderColor)) return;
        this.parametro.borderColor = new Color(color);
        fuente = obtenerOCrearFuente(this.parametro.color, this.parametro.borderColor, this.parametro.borderWidth);
        if (layout != null && texto != null) {
            layout.setText(fuente, texto);
        }
    }

    public void dispose() {
        for (BitmapFont f : fuentesCache.values()) {
            if (f != null) {
                f.dispose();
            }
        }
        fuentesCache.clear();
        fuente = null;
        if (generador != null) {
            generador.dispose();
            generador = null;
        }
    }

    public Color getColor() {
        return parametro.color;
    }

    public void setColorTexto(Color colorLetra, Color colorBorde) {
        boolean letraIgual = (colorLetra == null || colorLetra.equals(this.parametro.color));
        boolean bordeIgual = (colorBorde == null || colorBorde.equals(this.parametro.borderColor));
        if (letraIgual && bordeIgual) {
            return;
        }
        if (colorLetra != null) this.parametro.color = new Color(colorLetra);
        if (colorBorde != null) this.parametro.borderColor = new Color(colorBorde);
        fuente = obtenerOCrearFuente(this.parametro.color, this.parametro.borderColor, this.parametro.borderWidth);
        if (layout != null && texto != null) {
            layout.setText(fuente, texto);
        }
    }

    public boolean mouseDentroDeTexto(Entrada entrada) {
        if (entrada == null) return false;
        return (entrada.getMouseX() > posicion.x && entrada.getMouseX() < (posicion.x + getWidth())
                && entrada.getMouseY() > posicion.y - getHeight() && entrada.getMouseY() < posicion.y);
    }
}
