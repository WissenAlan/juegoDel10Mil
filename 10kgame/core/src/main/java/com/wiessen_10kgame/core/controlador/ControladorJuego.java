package com.wiessen_10kgame.core.controlador;

import com.wiessen_10kgame.core.logica.CalculadorPuntos10Mil;
import com.wiessen_10kgame.core.modelo.Jugador;
import com.wiessen_10kgame.core.modelo.Partida;
import com.wiessen_10kgame.red.PartidaListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controlador principal de la lógica del juego.
 * Gestiona el estado de la partida, los turnos, dados y puntajes.
 * Desacopla la vista (pantallas LibGDX) de la lógica de dominio y de la capa de red.
 * Es 100% Java estándar sin dependencias de LibGDX ni de red.
 * <p>
 * Implementa {@link PartidaListener} para recibir eventos de red directamente
 * desde {@code HiloCliente} sin que éste dependa de esta clase concreta.
 * </p>
 */
public class ControladorJuego implements PartidaListener {

    private final String nombreJugadorLocal;
    private boolean admin;
    private final Partida partida;
    private final CalculadorPuntos10Mil calculador;

    private int[] dados = new int[]{-1, -1, -1, -1, -1};
    private int ultimoDado = -1;
    private int indiceTriple = -1;
    private int puntosRonda = 0;
    private boolean puedePlantarse = false;
    private boolean hayGanador = false;

    private final List<ControladorJuegoListener> listeners = new CopyOnWriteArrayList<>();

    public ControladorJuego(String nombreJugadorLocal) {
        this.nombreJugadorLocal = nombreJugadorLocal != null ? nombreJugadorLocal.trim() : "";
        this.admin = false;
        this.partida = new Partida();
        Jugador local = new Jugador(this.nombreJugadorLocal);
        this.partida.agregarJugador(local);
        local.setAdmin(false); // No asumir admin hasta que el servidor lo confirme con imAdmin
        this.calculador = new CalculadorPuntos10Mil();
    }

    public void agregarListener(ControladorJuegoListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removerListener(ControladorJuegoListener listener) {
        listeners.remove(listener);
    }

    public void limpiarListeners() {
        listeners.clear();
    }

    // --- Métodos de actualización de estado ---

    public void setAdmin(boolean admin) {
        this.admin = admin;
        Jugador local = getJugadorLocal();
        if (local != null) {
            local.setAdmin(admin);
        }
        notificarListaJugadoresActualizada();
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getNombreJugadorLocal() {
        return nombreJugadorLocal;
    }

    public Partida getPartida() {
        return partida;
    }

    public CalculadorPuntos10Mil getCalculador() {
        return calculador;
    }

    /**
     * Sincroniza la lista de jugadores a partir de un arreglo de nombres recibidos del servidor.
     */
    public void actualizarListaNombres(List<String> nombres) {
        if (nombres == null) return;

        List<Jugador> jugadoresActuales = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            String nom = nombres.get(i);
            if (nom != null && !nom.trim().isEmpty()) {
                Jugador j = new Jugador(nom.trim());
                j.setAdmin(i == 0); // El jugador 0 del servidor siempre es el creador/admin de la sala
                jugadoresActuales.add(j);
            }
        }

        // Reemplazar jugadores en la partida
        while (partida.getCantidadJugadores() > 0) {
            partida.removerJugador(0);
        }
        for (int i = 0; i < jugadoresActuales.size(); i++) {
            Jugador j = jugadoresActuales.get(i);
            partida.agregarJugador(j);
            j.setAdmin(i == 0);
        }

        notificarListaJugadoresActualizada();
    }

    public void iniciarPartida() {
        partida.iniciarPartida();
        notificarPartidaIniciada();
        notificarTurnoCambiado();
    }

    public void setTurno(int indiceTurno) {
        partida.setTurnoActual(indiceTurno);
        reiniciarVariablesRonda();
        Arrays.fill(dados, -1);
        notificarTurnoCambiado();
    }

    public void actualizarPuntosRonda(int puntos) {
        this.puntosRonda = puntos;
        Jugador actual = partida.getJugadorActual();
        if (actual != null) {
            actual.setPuntosRonda(puntos);
        }
        notificarPuntosRonda();
    }

    public void actualizarPuntosTotales(int puntaje, int indiceJugador) {
        if (indiceJugador >= 0 && indiceJugador < partida.getCantidadJugadores()) {
            partida.getJugadores().get(indiceJugador).setPuntosTotales(puntaje);
            notificarPuntosTotales(indiceJugador, puntaje);
        }
    }

    public void actualizarDados(int[] nuevosDados) {
        if (nuevosDados != null) {
            System.arraycopy(nuevosDados, 0, this.dados, 0, Math.min(nuevosDados.length, this.dados.length));
            notificarDados();
        }
    }

    public void actualizarDadoIndividual(int indice, int valor) {
        if (indice >= 0 && indice < dados.length) {
            dados[indice] = valor;
            notificarDados();
        }
    }

    public void setPuedePlantarse(boolean puede) {
        this.puedePlantarse = puede;
        notificarPuedePlantarse();
    }

    public void setIndiceTriple(int indice) {
        this.indiceTriple = indice;
        notificarDados();
    }

    public void setUltimoDado(int indice) {
        this.ultimoDado = indice;
        notificarDados();
    }

    public void reiniciarVariablesRonda() {
        this.ultimoDado = -1;
        this.indiceTriple = -1;
        this.puntosRonda = 0;
        this.puedePlantarse = false;
        notificarPuntosRonda();
        notificarPuedePlantarse();
    }

    public void declararGanador() {
        this.hayGanador = true;
        Jugador ganador = partida.getJugadorActual();
        int[] puntajes = new int[partida.getCantidadJugadores()];
        for (int i = 0; i < puntajes.length; i++) {
            puntajes[i] = partida.getJugadores().get(i).getPuntosTotales();
        }
        for (ControladorJuegoListener l : listeners) {
            l.onGanadorDeclarado(ganador, puntajes);
        }
    }

    public void removerJugador(int indice) {
        partida.removerJugador(indice);
        notificarListaJugadoresActualizada();
    }

    // --- Consultas del estado actual ---

    public boolean isMiTurno() {
        Jugador actual = partida.getJugadorActual();
        return actual != null && actual.getNombre().equalsIgnoreCase(nombreJugadorLocal);
    }

    public Jugador getJugadorLocal() {
        for (Jugador j : partida.getJugadores()) {
            if (j.getNombre().equalsIgnoreCase(nombreJugadorLocal)) {
                return j;
            }
        }
        return null;
    }

    public List<Jugador> getJugadores() {
        return partida.getJugadores();
    }

    public int getTurnoActual() {
        return partida.getTurnoActual();
    }

    public int[] getDados() {
        return dados.clone();
    }

    public int getUltimoDado() {
        return ultimoDado;
    }

    public int getIndiceTriple() {
        return indiceTriple;
    }

    public int getPuntosRonda() {
        return puntosRonda;
    }

    public boolean isPuedePlantarse() {
        return puedePlantarse;
    }

    public boolean isHayGanador() {
        return hayGanador;
    }

    public int getIndiceContar() {
        int contar = ultimoDado;
        if (indiceTriple != -1) {
            contar += 3;
        }
        return contar;
    }

    // --- Notificaciones a listeners ---

    private void notificarListaJugadoresActualizada() {
        List<Jugador> lista = partida.getJugadores();
        for (ControladorJuegoListener l : listeners) {
            l.onListaJugadoresActualizada(lista);
        }
    }

    private void notificarPartidaIniciada() {
        for (ControladorJuegoListener l : listeners) {
            l.onPartidaIniciada();
        }
    }

    private void notificarTurnoCambiado() {
        int t = partida.getTurnoActual();
        Jugador actual = partida.getJugadorActual();
        boolean miTurno = isMiTurno();
        for (ControladorJuegoListener l : listeners) {
            l.onTurnoCambiado(t, actual, miTurno);
        }
    }

    private void notificarDados() {
        int[] d = dados.clone();
        for (ControladorJuegoListener l : listeners) {
            l.onDadosActualizados(d, ultimoDado, indiceTriple);
        }
    }

    private void notificarPuntosRonda() {
        for (ControladorJuegoListener l : listeners) {
            l.onPuntosRondaActualizados(puntosRonda);
        }
    }

    private void notificarPuntosTotales(int indiceJugador, int pts) {
        for (ControladorJuegoListener l : listeners) {
            l.onPuntosTotalesActualizados(indiceJugador, pts);
        }
    }

    private void notificarPuedePlantarse() {
        for (ControladorJuegoListener l : listeners) {
            l.onPuedePlantarseCambiado(puedePlantarse);
        }
    }

    // =========================================================================
    // Implementación de PartidaListener
    // Cada método traduce un evento de red a la lógica del dominio.
    // Son invocados desde HiloCliente ya en el hilo principal de LibGDX.
    // =========================================================================

    @Override
    public void onAdmin() {
        setAdmin(true);
    }

    @Override
    public void onNombresActualizados(java.util.List<String> nombres, boolean incluirPrimero) {
        if (!incluirPrimero && !nombres.isEmpty()) {
            // nombresAntes2: el primer elemento original es el índice, no un nombre
            // La lista ya llegó filtrada desde HiloCliente (i >= 2)
        }
        actualizarListaNombres(nombres);
    }

    @Override
    public void onJugadorRemovido(int indice) {
        removerJugador(indice);
    }

    @Override
    public void onConexionOk(InetAddress servidor, int nroJugador) {
        // Confirmación recibida del servidor
    }

    @Override
    public void onPartidaEmpezada() {
        iniciarPartida();
    }

    @Override
    public void onTurnoCambiado(int indiceTurno) {
        setTurno(indiceTurno);
    }

    @Override
    public void onPuntosRondaActualizados(int puntos) {
        actualizarPuntosRonda(puntos);
    }

    @Override
    public void onPuntosTotalesActualizados(int puntaje, int indiceJugador) {
        actualizarPuntosTotales(puntaje, indiceJugador);
    }

    @Override
    public void onGanadorDeclarado() {
        declararGanador();
    }

    @Override
    public void onDadoActualizado(int indice, int valor) {
        actualizarDadoIndividual(indice, valor);
    }

    @Override
    public void onPuedePlantarse(boolean puede) {
        setPuedePlantarse(puede);
    }

    @Override
    public void onIndiceTriple(int indice) {
        setIndiceTriple(indice);
    }

    @Override
    public void onVariablesReiniciadas() {
        reiniciarVariablesRonda();
    }

    @Override
    public void onUltimoDado(int indice) {
        setUltimoDado(indice);
    }
}
