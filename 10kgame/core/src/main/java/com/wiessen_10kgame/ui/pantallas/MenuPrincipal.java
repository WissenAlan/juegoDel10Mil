package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.core.Recursos;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;
import com.wiessen_10kgame.ui.componentes.Texto;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class MenuPrincipal implements Screen {

    private SpriteBatch b;
    private Texto titulo, jugar, salir, reglas, nombre, nombretxt;
    private boolean primeraVez = true, primeraVez2 = true, play = false, flag;
    private int aux = -1;
    private Sprite nombreSpr, barra;
    private Entrada e = ScreenManager.getInstance().getEntrada();

    @Override
    public void show() {
        Gdx.input.setInputProcessor(e);
        titulo = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "EL JUEGO DEL 10.000", Recursos.COLOR_LETRA);
        titulo.setPosicion((float) Gdx.graphics.getWidth() / 2 - titulo.getWidth() / 2, Gdx.graphics.getHeight() - 100);
        jugar = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "JUGAR", Recursos.COLOR_LETRA);
        jugar.setPosicion((float) Gdx.graphics.getWidth() / 2 - jugar.getWidth() / 2, titulo.getY() - 160);
        reglas = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "REGLAS", Recursos.COLOR_LETRA);
        reglas.setPosicion((float) Gdx.graphics.getWidth() / 2 - reglas.getWidth() / 2, jugar.getY() - 90);
        salir = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "SALIR", Recursos.COLOR_LETRA);
        salir.setPosicion((float) Gdx.graphics.getWidth() / 2 - salir.getWidth() / 2, reglas.getY() - 90);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
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
            } else if (variable.mouseDentroDeTexto(e)) {
                if (primeraVez) {
                    Sonidos.playAudio(Sonidos.SOFTCLICK.getAudio());
                    primeraVez = false;
                    aux = i;
                }
                variable.setColorTexto(Recursos.COLOR_LETRA, Color.WHITE);
                if (e.isTouch()) {
                    Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                    ejecutarAccion(variable);
                }
            } else if (!primeraVez && aux == i) {
                primeraVez = true;
                primeraVez2 = true;
                variable.setColorTexto(Color.WHITE, Recursos.COLOR_LETRA);
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
            nombre = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE);
        }
    }

    private void ingresarNombre() {
        if (primeraVez2) {
            primeraVez2 = false;
            nombreSpr = new Sprite(new Texture("sprites/ingresesunombre2.jpg"));
            nombreSpr.setPosition((float) Gdx.graphics.getWidth() / 2 - nombreSpr.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - nombreSpr.getHeight() / 2);
            nombretxt = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "INGRESE SU NOMBRE", Recursos.COLOR_LETRA);
            nombretxt.setPosicion(nombreSpr.getX() + nombreSpr.getWidth() / 2 - nombretxt.getWidth() / 2, nombreSpr.getY() + nombreSpr.getHeight() - 40);
            barra = new Sprite(new Texture(Recursos.BARRA_ESCRIBIR));
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
                //MainCliente.crearHilo(nombre.getTexto());
                ScreenManager.getInstance().setScreen(new SalaEspera(nombre.getTexto()));
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
