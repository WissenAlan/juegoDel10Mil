package com.wiessen_10kgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.wiessen_10kgame.core.controlador.ControladorJuego;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.pantallas.MenuPrincipal;
import com.wiessen_10kgame.utilidades.ScreenManager;

/**
 * Punto de entrada principal de LibGDX en el cliente.
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

    public HiloCliente conectarServidor(String nombre, ControladorJuego controlador) {
        desconectarServidor();
        hc = new HiloCliente("10kgame.duckdns.org", nombre, controlador);
        hc.start();
        return hc;
    }

    public void conectarServidor(String nombre) {
        conectarServidor(nombre, new ControladorJuego(nombre));
    }

    /**
     * Desconecta el hilo de red de forma segura.
     */
    public void desconectarServidor() {
        if (hc != null) {
            hc.terminar();
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
