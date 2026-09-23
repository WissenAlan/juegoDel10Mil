package com.wiessen_10kgame.core.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    @Test
    @DisplayName("El primer jugador registrado se convierte en administrador")
    void testPrimerJugadorEsAdmin() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        Jugador j2 = new Jugador("Carlos");

        partida.agregarJugador(j1);
        partida.agregarJugador(j2);

        assertTrue(j1.isAdmin());
        assertFalse(j2.isAdmin());
        assertEquals(2, partida.getCantidadJugadores());
    }

    @Test
    @DisplayName("Avance circular de turnos")
    void testAvanceTurnos() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        Jugador j2 = new Jugador("Carlos");
        partida.agregarJugador(j1);
        partida.agregarJugador(j2);
        partida.iniciarPartida();

        assertEquals(j1, partida.getJugadorActual());

        partida.avanzarTurno();
        assertEquals(j2, partida.getJugadorActual());

        partida.avanzarTurno();
        assertEquals(j1, partida.getJugadorActual());
    }

    @Test
    @DisplayName("Plantarse suma puntos y cambia de turno")
    void testPlantarseExitoso() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        Jugador j2 = new Jugador("Carlos");
        partida.agregarJugador(j1);
        partida.agregarJugador(j2);
        partida.iniciarPartida();

        j1.sumarPuntosRonda(600);
        boolean exito = partida.plantarJugadorActual();

        assertFalse(exito);
        assertEquals(0, j1.getPuntosTotales());

        j1.sumarPuntosRonda(200);
        exito = partida.plantarJugadorActual();

        assertTrue(exito);
        assertEquals(800, j1.getPuntosTotales());

        // El turno pasó a Carlos
        assertEquals(j2, partida.getJugadorActual());
    }

    @Test
    @DisplayName("Plantarse post-salida y cambia de turno")
    void testPlantarsePostSalida() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        partida.agregarJugador(j1);
        partida.iniciarPartida();

        j1.setPuntosTotales(800);

        j1.sumarPuntosRonda(50);
        boolean exito = partida.plantarJugadorActual();

        assertTrue(exito);
        assertEquals(850, j1.getPuntosTotales());
        assertEquals(0, j1.getPuntosRonda());
    }

    @Test
    @DisplayName("Detección de ganador al alcanzar 10.000 puntos")
    void testGanadorPartida() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        partida.agregarJugador(j1);
        partida.iniciarPartida();

        j1.setPuntosTotales(9200);
        j1.sumarPuntosRonda(800); // 9200 + 800 = 10.000
        partida.plantarJugadorActual();

        assertTrue(partida.hayGanador());
        assertEquals(j1, partida.getGanador());
        assertEquals(Partida.EstadoPartida.FINALIZADA, partida.getEstado());
    }
    @Test
    @DisplayName("Detección de pasar la meta")
    void testSuperoLaMeta() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Alan");
        partida.agregarJugador(j1);
        partida.iniciarPartida();

        j1.setPuntosTotales(9200);
        j1.sumarPuntosRonda(1000); // 9200 + 1000 = 10.200
        partida.plantarJugadorActual();

        assertFalse(partida.hayGanador());
    }
}
