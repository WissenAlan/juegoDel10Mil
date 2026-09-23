package com.wiessen_10kgame.core.controlador;

import com.wiessen_10kgame.core.modelo.Jugador;
import java.util.List;

/**
 * Interfaz para escuchar eventos del ciclo de vida del juego y cambios de estado.
 * Las pantallas gráficas (o mocks en tests unitarios) implementan esta interfaz
 * para actualizar su presentación sin acoplar la lógica de dominio.
 */
public interface ControladorJuegoListener {

    /** Notificado cuando cambia la lista de jugadores conectados en la sala */
    void onListaJugadoresActualizada(List<Jugador> jugadores);

    /** Notificado cuando la partida ha comenzado formalmente */
    void onPartidaIniciada();

    /** Notificado cuando cambia el turno de juego */
    void onTurnoCambiado(int indiceTurno, Jugador jugadorActual, boolean esMiTurno);

    /** Notificado cuando cambian los dados en la mesa */
    void onDadosActualizados(int[] dados, int ultimoDado, int indiceTriple);

    /** Notificado cuando cambian los puntos de la ronda actual */
    void onPuntosRondaActualizados(int puntosRonda);

    /** Notificado cuando cambian los puntos totales acumulados de un jugador */
    void onPuntosTotalesActualizados(int indiceJugador, int nuevosPuntosTotales);

    /** Notificado cuando se habilita o deshabilita la opción de plantarse */
    void onPuedePlantarseCambiado(boolean puedePlantarse);

    /** Notificado cuando hay un ganador y la partida finaliza */
    void onGanadorDeclarado(Jugador ganador, int[] puntajesFinales);

    /** Notificado ante cualquier mensaje informativo o de error */
    void onMensajeEstado(String mensaje);
}
