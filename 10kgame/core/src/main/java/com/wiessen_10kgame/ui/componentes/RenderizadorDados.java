package com.wiessen_10kgame.ui.componentes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.wiessen_10kgame.core.modelo.EstadoDado;

/**
 * Componente gráfico responsable del renderizado de los dados en pantalla.
 * Separa la representación visual del modelo de datos {@link EstadoDado}.
 * Implementa {@link Disposable} para garantizar la correcta liberación de Texturas en la GPU.
 */
public class RenderizadorDados implements Disposable {

    private final Sprite[][] spritesDados = new Sprite[6][2]; // [0..5 cara][0: blanco, 1: verde]
    private final Texture[] texturas = new Texture[12];
    private float posicionBaseX = 170f;
    private float posicionBaseY = 300f;
    private float espaciado = 20f;

    public RenderizadorDados() {
        cargarTexturas();
    }

    public RenderizadorDados(float x, float y) {
        this.posicionBaseX = x;
        this.posicionBaseY = y;
        cargarTexturas();
    }

    private void cargarTexturas() {
        int textureIdx = 0;
        for (int i = 0; i < 6; i++) {
            int num = i + 1;
            Texture texBlanco = new Texture("dados/dado " + num + ".png");
            Texture texVerde = new Texture("dados/dado " + num + " verde.png");

            texturas[textureIdx++] = texBlanco;
            texturas[textureIdx++] = texVerde;

            spritesDados[i][0] = new Sprite(texBlanco);
            spritesDados[i][1] = new Sprite(texVerde);
        }
    }

    /**
     * Dibuja un dado según su estado de valor y si está marcado como verde (contable/fijo).
     *
     * @param batch SpriteBatch activo donde dibujar
     * @param dado Estado del dado
     * @param verde Si true, dibuja la versión resaltada en verde; si false, la normal
     * @param indicePosicion Posición horizontal del dado (0 a 4)
     */
    public void dibujarDado(SpriteBatch batch, EstadoDado dado, boolean verde, int indicePosicion) {
        if (dado == null) return;
        int caraIdx = dado.ordinal();
        dibujarPorIndiceCara(batch, caraIdx, verde, indicePosicion);
    }

    /**
     * Sobrecarga para dibujar pasando el índice ordinal del dado (0..5).
     */
    public void dibujarDado(SpriteBatch batch, int indiceCara, boolean verde, int indicePosicion) {
        if (indiceCara < 0 || indiceCara >= 6) return;
        dibujarPorIndiceCara(batch, indiceCara, verde, indicePosicion);
    }

    private void dibujarPorIndiceCara(SpriteBatch batch, int caraIdx, boolean verde, int indicePosicion) {
        Sprite sprite = verde ? spritesDados[caraIdx][1] : spritesDados[caraIdx][0];
        float ancho = sprite.getWidth();
        float x = posicionBaseX + (indicePosicion * ancho) + (indicePosicion * espaciado);
        float y = posicionBaseY;

        sprite.setPosition(x, y);
        sprite.draw(batch);
    }

    public void setPosicionBase(float x, float y) {
        this.posicionBaseX = x;
        this.posicionBaseY = y;
    }

    public void setEspaciado(float espaciado) {
        this.espaciado = espaciado;
    }

    @Override
    public void dispose() {
        for (Texture tex : texturas) {
            if (tex != null) {
                tex.dispose();
            }
        }
    }
}
