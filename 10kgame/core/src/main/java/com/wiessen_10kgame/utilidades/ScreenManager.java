package com.wiessen_10kgame.utilidades;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.wiessen_10kgame.ui.componentes.Entrada;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenManager {

    private static ScreenManager instance;
    private Game game;
    private Screen actual;

    //pila
    private final Deque<Screen> historial = new ArrayDeque<>();
    private Entrada entrada;

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void setScreen(Screen newScreen) {
        validarInicializacion();
        Screen currentS = game.getScreen();
        if (currentS != null) currentS.dispose();

        historial.clear();
        game.setScreen(newScreen);
    }

    /**
     * Muestra una nueva pantalla (ej. un menú o ventana emergente)
     * guardando la actual en el historial sin destruirla.
     */
    public void pushScreen(Screen newScreen) {
        validarInicializacion();

        Screen currentScreen = game.getScreen();
        if (currentScreen != null) {
            historial.push(currentScreen);
        }

        game.setScreen(newScreen);
    }

    /**
     * Vuelve a la pantalla anterior del historial y destruye la pantalla actual.
     */
    public void popScreen() {
        validarInicializacion();

        if (!historial.isEmpty()) {
            Screen currentScreen = game.getScreen();

            // Set la pantalla previa del historial
            Screen previousScreen = historial.pop();
            game.setScreen(previousScreen);

            // Destruimos la pantalla de la que venimos
            if (currentScreen != null) {
                currentScreen.dispose();
            }
        }
    }

    public void dispose() {
        while (!historial.isEmpty()) {
            Screen screen = historial.pop();
            if (screen != null) {
                screen.dispose();
            }
        }
        if (game != null && game.getScreen() != null) {
            game.getScreen().dispose();
        }
        instance = null; // Liberamos la referencia Singleton
    }

    private void validarInicializacion() {
        if (game == null) {
            throw new IllegalStateException("ScreenManager no ha sido inicializado. Llama a initialize(game) en MainCliente.create()");
        }
    }

    public void initialize(Game game, Entrada e) {
        this.game = game;
        this.entrada = e;
    }

    public Entrada getEntrada() {
        return entrada;
    }
}
