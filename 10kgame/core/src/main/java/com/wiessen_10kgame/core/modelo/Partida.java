package com.wiessen_10kgame.core.modelo;

import com.wiessen_10kgame.core.logica.CalculadorPuntos10Mil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo POJO que encapsula el estado completo de una partida del Juego del 10 Mil.
 * Gestiona la lista de participantes, el control de turnos, y la condición de victoria.
 * 100% testeable de forma independiente.
 */
public class Partida {

    public enum EstadoPartida {
        ESPERANDO_JUGADORES,
        EN_CURSO,
        FINALIZADA
    }

    private final List<Jugador> jugadores;
    private int turnoActual;
    private final int puntajeMeta;
    private final int puntajeMinimoPlantarse;
    private EstadoPartida estado;
    private Jugador ganador;

    public Partida() {
        this(CalculadorPuntos10Mil.PUNTAJE_META_DEFAULT, CalculadorPuntos10Mil.PUNTAJE_MINIMO_PLANTARSE);
    }

    public Partida(int puntajeMeta, int puntajeMinimoPlantarse) {
        this.jugadores = new ArrayList<>();
        this.turnoActual = 0;
        this.puntajeMeta = puntajeMeta;
        this.puntajeMinimoPlantarse = puntajeMinimoPlantarse;
        this.estado = EstadoPartida.ESPERANDO_JUGADORES;
        this.ganador = null;
    }

    /**
     * Agrega un jugador a la partida si aún no ha iniciado y hay cupo (máx 6).
     */
    public boolean agregarJugador(Jugador jugador) {
        if (estado != EstadoPartida.ESPERANDO_JUGADORES || jugadores.size() >= 6 || jugador == null) {
            return false;
        }
        if (jugadores.isEmpty()) {
            jugador.setAdmin(true); // El primer jugador registrado es admin
        }
        jugadores.add(jugador);
        return true;
    }

    public boolean removerJugador(int indice) {
        if (indice < 0 || indice >= jugadores.size()) {
            return false;
        }
        jugadores.remove(indice);
        if (turnoActual >= jugadores.size() && !jugadores.isEmpty()) {
            turnoActual = 0;
        }
        return true;
    }

    /**
     * Inicia la partida si hay al menos 1 o 2 jugadores.
     */
    public boolean iniciarPartida() {
        if (jugadores.isEmpty()) {
            return false;
        }
        this.estado = EstadoPartida.EN_CURSO;
        this.turnoActual = 0;
        return true;
    }

    /**
     * Devuelve el jugador que posee el turno activo.
     */
    public Jugador getJugadorActual() {
        if (jugadores.isEmpty() || turnoActual < 0 || turnoActual >= jugadores.size()) {
            return null;
        }
        return jugadores.get(turnoActual);
    }

    /**
     * Pasa el turno al siguiente participante de manera circular y reinicia la ronda anterior si no se plantó.
     */
    public void avanzarTurno() {
        if (jugadores.isEmpty()) return;

        // Si el jugador actual no se plantó, pierde los puntos de la ronda
        Jugador actual = getJugadorActual();
        if (actual != null) {
            actual.anularPuntosRonda();
        }

        turnoActual = (turnoActual + 1) % jugadores.size();
    }

    /**
     * El jugador actual decide plantarse: consolida sus puntos de ronda y verifica si ganó.
     *
     * @return true si pudo plantarse con éxito.
     */
    public boolean plantarJugadorActual() {
        Jugador actual = getJugadorActual();
        if (actual == null || estado != EstadoPartida.EN_CURSO) return false;

        if (!actual.puedePlantarse(puntajeMinimoPlantarse)) {
            return false;
        }

        actual.confirmarPuntosRonda();

        if (actual.alcanzoMeta(puntajeMeta)) {
            this.estado = EstadoPartida.FINALIZADA;
            this.ganador = actual;
        } else {
            turnoActual = (turnoActual + 1) % jugadores.size();
        }
        return true;
    }

    public boolean hayGanador() {
        return ganador != null || estado == EstadoPartida.FINALIZADA;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(int turnoActual) {
        if (turnoActual >= 0 && turnoActual < jugadores.size()) {
            this.turnoActual = turnoActual;
        }
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public int getPuntajeMeta() {
        return puntajeMeta;
    }

    public int getCantidadJugadores() {
        return jugadores.size();
    }
}
