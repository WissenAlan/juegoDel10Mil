package com.wiessen_10kgame.core.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    @Test
    @DisplayName("Inicialización y estado base de jugador")
    void testInicializacion() {
        Jugador j = new Jugador("Lucas");
        assertEquals("Lucas", j.getNombre());
        assertEquals(0, j.getPuntosTotales());
        assertEquals(0, j.getPuntosRonda());
        assertFalse(j.isAdmin());
    }

    @Test
    @DisplayName("Acumular y confirmar puntos de ronda")
    void testConfirmarPuntosRonda() {
        Jugador j = new Jugador("Lucas");
        j.sumarPuntosRonda(250);
        j.sumarPuntosRonda(150);
        assertEquals(400, j.getPuntosRonda());
        assertEquals(0, j.getPuntosTotales());

        int total = j.confirmarPuntosRonda();
        assertEquals(400, total);
        assertEquals(400, j.getPuntosTotales());
        assertEquals(0, j.getPuntosRonda());
    }

    @Test
    @DisplayName("Anular puntos de ronda por falla")
    void testAnularPuntosRonda() {
        Jugador j = new Jugador("Lucas");
        j.setPuntosTotales(1000);
        j.sumarPuntosRonda(300);

        j.anularPuntosRonda();
        assertEquals(0, j.getPuntosRonda());
        assertEquals(1000, j.getPuntosTotales());
    }

    @Test
    @DisplayName("Verificación de meta alcanzada")
    void testAlcanzoMeta() {
        Jugador j = new Jugador("Lucas");
        j.setPuntosTotales(9900);
        assertFalse(j.alcanzoMeta(10000));

        j.setPuntosTotales(10000);
        assertTrue(j.alcanzoMeta(10000));

        j.setPuntosTotales(10500);
        assertFalse(j.alcanzoMeta(10000));
    }
}
