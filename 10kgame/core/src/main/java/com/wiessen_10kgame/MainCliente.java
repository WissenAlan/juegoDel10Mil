package com.wiessen_10kgame;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.pantallas.MenuPrincipal;
import com.wiessen_10kgame.utilidades.ScreenManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class MainCliente extends Game {

    private HiloCliente hc;
    private Entrada e;

    @Override
    public void create() {
        e = new Entrada();
        Gdx.input.setInputProcessor(e);
        ScreenManager.getInstance().initialize(this, e);
        ScreenManager.getInstance().setScreen(new MenuPrincipal());
    }

    public void conectarServidor(String nombre) {
        // Cerramos cualquier conexión previa por seguridad
        desconectarServidor();

        hc = new HiloCliente("10kgame.duckdns.org",nombre);
        hc.start();
    }

    /**
     * Desconecta el hilo de red de forma segura.
     */
    public void desconectarServidor() {
        if (hc != null) {
            hc.terminar(); // HiloCliente debe encargarse de cerrar su propio Socket
            hc = null;
        }
    }

    public HiloCliente getHiloCliente() {
        return hc;
    }

    public boolean isOnline() {
        return hc != null && hc.isAlive();
    }

    @Override
    public void dispose() {
        desconectarServidor();
        ScreenManager.getInstance().dispose();
    }
}
