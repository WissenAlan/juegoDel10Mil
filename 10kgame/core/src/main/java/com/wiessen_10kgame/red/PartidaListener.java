package com.wiessen_10kgame.red;

import java.net.InetAddress;
import java.util.List;

/**
 * Interfaz de callbacks que {@link HiloCliente} invoca cuando llega un paquete de red.
 * <p>
 * Desacopla completamente la capa de red ({@code HiloCliente}) de la capa de dominio
 * ({@code ControladorJuego}) y de la UI. Todos los métodos son llamados ya en el
 * hilo principal de LibGDX (mediante {@code Gdx.app.postRunnable}).
 * </p>
 *
 * <p>Implementaciones típicas: {@code ControladorJuego}.</p>
 */
public interface PartidaListener {

    /**
     * El servidor indicó que este cliente es el administrador de la sala.
     */
    void onAdmin();

    /**
     * El servidor envió la lista actualizada de jugadores.
     *
     * @param nombres         nombres de los jugadores en orden de turno
     * @param incluirPrimero  {@code true} si el primer elemento de la lista debe incluirse
     *                        (mensaje {@code nombresAntes1}); {@code false} si se omite
     *                        (mensaje {@code nombresAntes2})
     */
    void onNombresActualizados(List<String> nombres, boolean incluirPrimero);

    /**
     * Un jugador se desconectó y debe eliminarse de la lista.
     *
     * @param indice índice del jugador a eliminar
     */
    void onJugadorRemovido(int indice);

    /**
     * El servidor aceptó la conexión.
     *
     * @param servidor    dirección IP del servidor que respondió
     * @param nroJugador  número de jugador asignado por el servidor
     */
    void onConexionOk(InetAddress servidor, int nroJugador);

    /**
     * El servidor indicó que la partida comienza.
     */
    void onPartidaEmpezada();

    /**
     * El turno cambió.
     *
     * @param indiceTurno índice del jugador cuyo turno comienza
     */
    void onTurnoCambiado(int indiceTurno);

    /**
     * Puntos de la ronda actual actualizados.
     *
     * @param puntos puntos de la ronda actual
     */
    void onPuntosRondaActualizados(int puntos);

    /**
     * Puntos totales de un jugador actualizados.
     *
     * @param puntaje        nuevo total de puntos
     * @param indiceJugador  índice del jugador al que corresponden
     */
    void onPuntosTotalesActualizados(int puntaje, int indiceJugador);

    /**
     * El servidor declaró un ganador y la partida termina.
     */
    void onGanadorDeclarado();

    /**
     * Un dado individual fue actualizado.
     *
     * @param indice índice del dado (0-4)
     * @param valor  cara del dado (0-5)
     */
    void onDadoActualizado(int indice, int valor);

    /**
     * Indica si el jugador activo puede o no plantarse.
     *
     * @param puede {@code true} si se habilita la opción de plantarse
     */
    void onPuedePlantarse(boolean puede);

    /**
     * Índice del triple en el arreglo de dados.
     *
     * @param indice índice a partir del cual está el triple, o -1 si no hay
     */
    void onIndiceTriple(int indice);

    /**
     * El servidor ordenó reiniciar las variables de ronda.
     */
    void onVariablesReiniciadas();

    /**
     * Índice del último dado fijo de tiradas anteriores.
     *
     * @param indice índice del último dado fijo
     */
    void onUltimoDado(int indice);
}
