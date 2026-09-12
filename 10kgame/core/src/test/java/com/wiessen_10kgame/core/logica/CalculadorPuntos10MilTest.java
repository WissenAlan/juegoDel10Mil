package com.wiessen_10kgame.core.logica;

import com.wiessen_10kgame.core.modelo.EstadoDado;
import com.wiessen_10kgame.core.modelo.Jugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorPuntos10MilTest {

    private CalculadorPuntos10Mil calculador;

    @BeforeEach
    void setUp() {
        calculador = new CalculadorPuntos10Mil();
    }

    @Test
    @DisplayName("Tirada con dados individuales 1 y 5")
    void testDadosIndividuales() {
        // [1, 5, 2, 3, 4] -> 100 + 50 = 150
        int[] tirada = {1, 5, 2, 3, 4};
        ResultadoTirada res = calculador.calcularPuntos(tirada);

        assertEquals(150, res.getPuntosTotales());
        assertEquals(0, res.getPuntosTriple());
        assertEquals(150, res.getPuntosIndividuales());
        assertFalse(res.tieneTriple());
        assertFalse(res.esFalla());

        // Verificar dados contables
        assertTrue(res.isDadoContable(0)); // 1
        assertTrue(res.isDadoContable(1)); // 5
        assertFalse(res.isDadoContable(2)); // 2
        assertFalse(res.isDadoContable(3)); // 3
        assertFalse(res.isDadoContable(4)); // 4
    }

    @Test
    @DisplayName("Tirada con triple de 1s (1000 puntos)")
    void testTripleUnos() {
        int[] tirada = {1, 1, 1, 2, 4};
        ResultadoTirada res = calculador.calcularPuntos(tirada);

        assertEquals(1000, res.getPuntosTotales());
        assertTrue(res.tieneTriple());
        assertEquals(1, res.getValorCaraTriple());
        assertEquals(1000, res.getPuntosTriple());
        assertEquals(0, res.getPuntosIndividuales());
        assertTrue(res.isDadoContable(0));
        assertTrue(res.isDadoContable(1));
        assertTrue(res.isDadoContable(2));
        assertFalse(res.isDadoContable(3));
        assertFalse(res.isDadoContable(4));
    }

    @Test
    @DisplayName("Tirada con triple de 6s (600 puntos) más un 5")
    void testTripleSeisYUnCinco() {
        int[] tirada = {6, 2, 6, 5, 6};
        ResultadoTirada res = calculador.calcularPuntos(tirada);

        assertEquals(650, res.getPuntosTotales());
        assertTrue(res.tieneTriple());
        assertEquals(6, res.getValorCaraTriple());
        assertEquals(600, res.getPuntosTriple());
        assertEquals(50, res.getPuntosIndividuales());

        assertTrue(res.isDadoContable(0)); // 6
        assertFalse(res.isDadoContable(1)); // 2
        assertTrue(res.isDadoContable(2)); // 6
        assertTrue(res.isDadoContable(3)); // 5
        assertTrue(res.isDadoContable(4)); // 6
    }

    @Test
    @DisplayName("Tirada nula / falla (0 puntos)")
    void testFalla() {
        int[] tirada = {2, 3, 4, 6, 2};
        ResultadoTirada res = calculador.calcularPuntos(tirada);

        assertEquals(0, res.getPuntosTotales());
        assertTrue(res.esFalla());
        assertFalse(res.tieneTriple());
        for (int i = 0; i < 5; i++) {
            assertFalse(res.isDadoContable(i));
        }
    }

    @Test
    @DisplayName("Todos los dados anotaron puntos")
    void testTodosAnotaron() {
        int[] tirada = {1, 1, 1, 5, 5};
        ResultadoTirada res = calculador.calcularPuntos(tirada);

        assertEquals(1100, res.getPuntosTotales());
        assertTrue(res.todosAnotaron());
    }

    @Test
    @DisplayName("Cálculo a partir de índices legados (0..5)")
    void testCalculoDesdeIndicesLegados() {
        // [0, 4, 1, 2, 3] corresponde a las caras [1, 5, 2, 3, 4]
        int[] indices = {0, 4, 1, 2, 3};
        ResultadoTirada res = calculador.calcularPuntosDesdeIndices(indices);

        assertEquals(150, res.getPuntosTotales());
        assertTrue(res.isDadoContable(0));
        assertTrue(res.isDadoContable(1));
    }

    @Test
    @DisplayName("Cálculo a partir de enum EstadoDado")
    void testCalculoDesdeEnums() {
        EstadoDado[] dados = {EstadoDado.CINCO, EstadoDado.CINCO, EstadoDado.CINCO, EstadoDado.DOS, EstadoDado.UNO};
        ResultadoTirada res = calculador.calcularPuntos(dados);

        // Triple 5 (500) + 1 (100) = 600
        assertEquals(600, res.getPuntosTotales());
        assertTrue(res.tieneTriple());
        assertEquals(5, res.getValorCaraTriple());
    }

    @Test
    @DisplayName("Reglas para plantarse")
    void testPuedePlantarse() {
        Jugador jugador = new Jugador("Alan");

        // Sin puntos suficientes en ronda ni total
        jugador.setPuntosRonda(500);
        jugador.setPuntosTotales(0);
        assertFalse(calculador.puedePlantarse(jugador));

        // Acumula 750 en la ronda -> puede plantarse
        jugador.setPuntosRonda(750);
        assertTrue(calculador.puedePlantarse(jugador));

        // Ya tiene más de 750 puntos totales -> puede plantarse con cualquier puntaje de ronda positivo
        jugador.setPuntosTotales(1200);
        jugador.setPuntosRonda(100);
        assertTrue(calculador.puedePlantarse(jugador));

        // Excede la meta de 10.000 puntos
        jugador.setPuntosTotales(9800);
        jugador.setPuntosRonda(500); // 10300 > 10000
        assertFalse(calculador.puedePlantarse(jugador));
    }
}
