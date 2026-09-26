package com.wiessen_10kgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.server10mil.HiloServidor;
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
    private HiloServidor hs;
    private Entrada e;

    @Override
    public void create() {
        e = new Entrada();
        Gdx.input.setInputProcessor(e);
        ScreenManager.getInstance().initialize(this, e);
        ScreenManager.getInstance().setScreen(new MenuPrincipal());
    }

    /**
     * Inicia un HiloServidor en la máquina host (si no está ya corriendo)
     * y conecta el HiloCliente local a 127.0.0.1.
     */
    public HiloCliente crearSalaYConectar(String nombre, ControladorJuego controlador) {
        desconectarServidor();
        try {
            hs = new HiloServidor();
            hs.start();
            Gdx.app.log("HOST", "HiloServidor iniciado exitosamente en puerto 5302.");
        } catch (Exception ex) {
            Gdx.app.error("HOST", "Aviso al iniciar HiloServidor (¿puerto 5302 ya ocupado?): " + ex.getMessage());
        }
        hc = new HiloCliente("127.0.0.1", nombre, controlador);
        hc.start();
        return hc;
    }

    /**
     * Conecta el HiloCliente a una sala remota o local existente.
     */
    public HiloCliente unirseASala(String host, String nombre, ControladorJuego controlador) {
        desconectarServidor();
        String destino = (host != null && !host.trim().isEmpty()) ? host.trim() : "127.0.0.1";
        hc = new HiloCliente(destino, nombre, controlador);
        hc.start();
        return hc;
    }

    public HiloCliente conectarServidor(String nombre, ControladorJuego controlador) {
        return unirseASala("127.0.0.1", nombre, controlador);
    }

    public void conectarServidor(String nombre) {
        conectarServidor(nombre, new ControladorJuego(nombre));
    }

    /**
     * Desconecta tanto el cliente como el servidor local (si existía) de forma segura.
     */
    public void desconectarServidor() {
        if (hc != null) {
            hc.terminar();
            hc = null;
        }
        if (hs != null) {
            hs.terminar();
            hs = null;
        }
    }

    public HiloCliente getHiloCliente() {
        return hc;
    }

    public HiloServidor getHiloServidor() {
        return hs;
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
