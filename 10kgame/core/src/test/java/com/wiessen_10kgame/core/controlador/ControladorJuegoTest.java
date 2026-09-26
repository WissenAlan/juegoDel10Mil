package com.wiessen_10kgame.core.controlador;

import com.wiessen_10kgame.core.modelo.Jugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControladorJuegoTest {

    private ControladorJuego controlador;

    @BeforeEach
    void setUp() {
        controlador = new ControladorJuego("Alan");
    }

    @Test
    @DisplayName("Inicialización con nombre de jugador local")
    void testInicializacion() {
        assertEquals("Alan", controlador.getNombreJugadorLocal());
        assertFalse(controlador.isAdmin());
        assertFalse(controlador.isHayGanador());
        assertFalse(controlador.isPuedePlantarse());
        assertEquals(0, controlador.getPuntosRonda());
    }

    @Test
    @DisplayName("Sincronización de lista de jugadores desde nombres")
    void testActualizarListaNombres() {
        controlador.setAdmin(true);
        controlador.actualizarListaNombres(Arrays.asList("Alan", "Carlos", "Matias"));

        List<Jugador> jugadores = controlador.getJugadores();
        assertEquals(3, jugadores.size());
        assertEquals("Alan", jugadores.get(0).getNombre());
        assertTrue(jugadores.get(0).isAdmin());
        assertEquals("Carlos", jugadores.get(1).getNombre());
        assertFalse(jugadores.get(1).isAdmin());
    }

    @Test
    @DisplayName("Cliente no admin (ej. Tom) ve al creador (Alan) como Admin y a sí mismo no admin")
    void testClienteNoAdminSincronizaLista() {
        ControladorJuego controladorTom = new ControladorJuego("Tom");
        assertFalse(controladorTom.isAdmin());

        controladorTom.actualizarListaNombres(Arrays.asList("Alan", "Tom"));

        List<Jugador> jugadores = controladorTom.getJugadores();
        assertEquals(2, jugadores.size());
        assertEquals("Alan", jugadores.get(0).getNombre());
        assertTrue(jugadores.get(0).isAdmin(), "Alan debe ser Admin en la pantalla de Tom");
        assertEquals("Tom", jugadores.get(1).getNombre());
        assertFalse(jugadores.get(1).isAdmin(), "Tom no debe ser Admin");
        assertFalse(controladorTom.isAdmin(), "El controlador local de Tom no debe tener permisos de admin");
    }

    @Test
    @DisplayName("Detección de mi turno")
    void testEsMiTurno() {
        controlador.actualizarListaNombres(Arrays.asList("Alan", "Carlos"));
        controlador.iniciarPartida();

        // Turno 0 es Alan
        controlador.setTurno(0);
        assertTrue(controlador.isMiTurno());

        // Turno 1 es Carlos
        controlador.setTurno(1);
        assertFalse(controlador.isMiTurno());
    }

    @Test
    @DisplayName("Actualización y reinicio de dados y variables de ronda")
    void testActualizacionDadosYRonda() {
        int[] tirada = {1, 1, 1, 5, 2};
        controlador.actualizarDados(tirada);
        controlador.setUltimoDado(0);
        controlador.setIndiceTriple(1);
        controlador.actualizarPuntosRonda(1050);
        controlador.setPuedePlantarse(true);

        assertArrayEquals(tirada, controlador.getDados());
        assertEquals(0, controlador.getUltimoDado());
        assertEquals(1, controlador.getIndiceTriple());
        assertEquals(1050, controlador.getPuntosRonda());
        assertTrue(controlador.isPuedePlantarse());
        assertEquals(3, controlador.getIndiceContar()); // ultimoDado (0) + 3

        // Reinicio de ronda
        controlador.reiniciarVariablesRonda();
        assertEquals(-1, controlador.getUltimoDado());
        assertEquals(-1, controlador.getIndiceTriple());
        assertEquals(0, controlador.getPuntosRonda());
        assertFalse(controlador.isPuedePlantarse());
    }

    @Test
    @DisplayName("Actualización de puntos totales acumulados")
    void testActualizarPuntosTotales() {
        controlador.actualizarListaNombres(Arrays.asList("Alan", "Carlos"));
        controlador.actualizarPuntosTotales(1500, 0);
        controlador.actualizarPuntosTotales(2300, 1);

        assertEquals(1500, controlador.getJugadores().get(0).getPuntosTotales());
        assertEquals(2300, controlador.getJugadores().get(1).getPuntosTotales());
    }

    @Test
    @DisplayName("Notificación de eventos a través del listener desacoplado")
    void testNotificacionesListener() {
        final List<String> eventos = new ArrayList<>();

        controlador.agregarListener(new ControladorJuegoListener() {
            @Override
            public void onListaJugadoresActualizada(List<Jugador> jugadores) {
                eventos.add("LISTA:" + jugadores.size());
            }

            @Override
            public void onPartidaIniciada() {
                eventos.add("INICIADA");
            }

            @Override
            public void onTurnoCambiado(int indiceTurno, Jugador jugadorActual, boolean esMiTurno) {
                eventos.add("TURNO:" + indiceTurno);
            }

            @Override
            public void onDadosActualizados(int[] dados, int ultimoDado, int indiceTriple) {
                eventos.add("DADOS");
            }

            @Override
            public void onPuntosRondaActualizados(int puntosRonda) {
                eventos.add("PTS_RONDA:" + puntosRonda);
            }

            @Override
            public void onPuntosTotalesActualizados(int indiceJugador, int nuevosPuntosTotales) {
                eventos.add("PTS_TOTAL:" + indiceJugador + "=" + nuevosPuntosTotales);
            }

            @Override
            public void onPuedePlantarseCambiado(boolean puedePlantarse) {
                eventos.add("PLANTARSE:" + puedePlantarse);
            }

            @Override
            public void onGanadorDeclarado(Jugador ganador, int[] puntajesFinales) {
                eventos.add("GANADOR:" + ganador.getNombre());
            }

            @Override
            public void onMensajeEstado(String mensaje) {
                eventos.add("MSG:" + mensaje);
            }
        });

        controlador.actualizarListaNombres(Arrays.asList("Alan", "Carlos"));
        controlador.iniciarPartida();
        controlador.actualizarPuntosRonda(350);
        controlador.actualizarPuntosTotales(1200, 0);
        controlador.declararGanador();

        assertTrue(eventos.contains("LISTA:2"));
        assertTrue(eventos.contains("INICIADA"));
        assertTrue(eventos.contains("PTS_RONDA:350"));
        assertTrue(eventos.contains("PTS_TOTAL:0=1200"));
        assertTrue(eventos.contains("GANADOR:Alan"));
    }
}
