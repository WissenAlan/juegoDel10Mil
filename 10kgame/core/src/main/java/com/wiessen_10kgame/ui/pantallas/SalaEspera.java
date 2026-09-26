package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.core.Recursos;
import com.wiessen_10kgame.core.controlador.ControladorJuego;
import com.wiessen_10kgame.core.controlador.ControladorJuegoListener;
import com.wiessen_10kgame.core.modelo.Jugador;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Boton;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.componentes.Texto;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Pantalla de sala de espera (lobby).
 * Muestra los jugadores conectados y permite al administrador iniciar la partida.
 * Desacoplada del modelo y sin variables estáticas.
 */
public class SalaEspera implements Screen, ControladorJuegoListener {

    private final ControladorJuego controlador;
    private final HiloCliente hiloCliente;

    private SpriteBatch batch;
    private Texto participantesTituloTxt;
    private Texto ipInfoTxt;
    private final List<Texto> jugadoresTxt = new ArrayList<>();
    private Boton empezarBtn;
    private Entrada entrada;
    private boolean clicBoton = false;
    private boolean cambiarASalaJuego = false;

    public SalaEspera(ControladorJuego controlador, HiloCliente hiloCliente) {
        this.controlador = controlador;
        this.hiloCliente = hiloCliente;
    }

    public SalaEspera(ControladorJuego controlador) {
        this(controlador, null);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        entrada = ScreenManager.getInstance().getEntrada();
        if (entrada != null) {
            Gdx.input.setInputProcessor(entrada);
        }

        participantesTituloTxt = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "Participantes:", Recursos.COLOR_LETRA);
        participantesTituloTxt.setPosicion(100, Gdx.graphics.getHeight() - 50);

        String ipLocal = obtenerIpLocal();
        String infoIp = "Host IP: " + ipLocal + " (o 127.0.0.1) | Puerto: 5302";
        ipInfoTxt = new Texto(Recursos.FUENTE_MENU, 18, Color.YELLOW, infoIp, Recursos.COLOR_LETRA);
        ipInfoTxt.setPosicion(100, 30);

        if (controlador != null) {
            controlador.agregarListener(this);
            reconstruirTextosJugadores(controlador.getJugadores());
        }

        empezarBtn = new Boton("Empezar", Color.WHITE, Color.BLACK, 28, false);
        empezarBtn.setPosition(Gdx.graphics.getWidth() - empezarBtn.getWidth() - 50, 50);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        if (cambiarASalaJuego) {
            cambiarASalaJuego = false;
            ScreenManager.getInstance().setScreen(new SalaJuego(controlador, hiloCliente));
            return;
        }

        actualizarBotonEmpezar();

        batch.begin();
        participantesTituloTxt.dibujar(batch);

        for (Texto txt : jugadoresTxt) {
            if (txt != null) {
                txt.dibujar(batch);
            }
        }

        if (ipInfoTxt != null) {
            ipInfoTxt.dibujar(batch);
        }

        if (controlador != null && controlador.isAdmin()) {
            empezarBtn.draw(batch);
        }
        batch.end();
    }

    private void actualizarBotonEmpezar() {
        if (controlador == null || !controlador.isAdmin()) return;

        boolean puedeEmpezar = !controlador.getJugadores().isEmpty();
        empezarBtn.setHabilitado(puedeEmpezar);

        if (entrada != null && empezarBtn.estaDentro(entrada)) {
            if (entrada.isTouch() && !clicBoton && puedeEmpezar) {
                clicBoton = true;
                Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("Empezar");
                } else {
                    controlador.iniciarPartida();
                }
            }
        }

        if (clicBoton && entrada != null && !entrada.isTouch()) {
            clicBoton = false;
        }
    }

    private synchronized void reconstruirTextosJugadores(List<Jugador> jugadores) {
        for (Texto t : jugadoresTxt) {
            if (t != null) t.dispose();
        }
        jugadoresTxt.clear();
        if (jugadores == null) return;

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            boolean esAdmin = j.isAdmin();
            String etiqueta = (i + 1) + ". " + j.getNombre() + (esAdmin ? " (Admin)" : "");
            Texto txt = new Texto(Recursos.FUENTE_MENU, 28, Color.WHITE, etiqueta, Recursos.COLOR_LETRA);
            txt.setPosicion(100, participantesTituloTxt.getY() - (60 * (i + 1)));
            jugadoresTxt.add(txt);
        }
    }

    private String obtenerIpLocal() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    // --- ControladorJuegoListener ---

    @Override
    public void onListaJugadoresActualizada(List<Jugador> jugadores) {
        reconstruirTextosJugadores(jugadores);
    }

    @Override
    public void onPartidaIniciada() {
        cambiarASalaJuego = true;
    }

    @Override
    public void onTurnoCambiado(int indiceTurno, Jugador jugadorActual, boolean esMiTurno) {}

    @Override
    public void onDadosActualizados(int[] dados, int ultimoDado, int indiceTriple) {}

    @Override
    public void onPuntosRondaActualizados(int puntosRonda) {}

    @Override
    public void onPuntosTotalesActualizados(int indiceJugador, int nuevosPuntosTotales) {}

    @Override
    public void onPuedePlantarseCambiado(boolean puedePlantarse) {}

    @Override
    public void onGanadorDeclarado(Jugador ganador, int[] puntajesFinales) {}

    @Override
    public void onMensajeEstado(String mensaje) {}

    // --- Ciclo de vida ---

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (controlador != null) {
            controlador.removerListener(this);
        }
    }

    @Override
    public void dispose() {
        if (controlador != null) {
            controlador.removerListener(this);
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (participantesTituloTxt != null) {
            participantesTituloTxt.dispose();
            participantesTituloTxt = null;
        }
        if (ipInfoTxt != null) {
            ipInfoTxt.dispose();
            ipInfoTxt = null;
        }
        for (Texto txt : jugadoresTxt) {
            if (txt != null) {
                txt.dispose();
            }
        }
        jugadoresTxt.clear();
    }
}
