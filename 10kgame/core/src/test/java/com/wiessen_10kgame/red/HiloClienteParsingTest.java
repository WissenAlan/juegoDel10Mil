package com.wiessen_10kgame.red;

import com.wiessen_10kgame.core.controlador.ControladorJuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HiloClienteParsingTest {

    private ControladorJuego controlador;
    private HiloCliente hiloCliente;

    @BeforeEach
    void setUp() {
        controlador = new ControladorJuego("Alan");
        // Instanciar sin conectar socket pasando null o sin iniciar thread
        hiloCliente = new HiloCliente("127.0.0.1", "Alan", controlador);
    }

    @Test
    @DisplayName("Procesar mensaje imAdmin")
    void testMensajeImAdmin() {
        assertFalse(controlador.isAdmin());
        hiloCliente.procesarMensaje("imAdmin", null);
        assertTrue(controlador.isAdmin());
    }

    @Test
    @DisplayName("Procesar mensaje nombresAntes1")
    void testMensajeNombresAntes1() {
        hiloCliente.procesarMensaje("nombresAntes1%Alan%Carlos%Matias", null);
        assertEquals(3, controlador.getJugadores().size());
        assertEquals("Alan", controlador.getJugadores().get(0).getNombre());
        assertEquals("Carlos", controlador.getJugadores().get(1).getNombre());
        assertEquals("Matias", controlador.getJugadores().get(2).getNombre());
    }

    @Test
    @DisplayName("Procesar mensaje EmpezarPartida")
    void testMensajeEmpezarPartida() {
        hiloCliente.procesarMensaje("nombresAntes1%Alan%Carlos", null);
        hiloCliente.procesarMensaje("EmpezarPartida", null);
        assertEquals(com.wiessen_10kgame.core.modelo.Partida.EstadoPartida.EN_CURSO, controlador.getPartida().getEstado());
    }

    @Test
    @DisplayName("Procesar mensaje CantidaddeClientes con turno")
    void testMensajeTurno() {
        hiloCliente.procesarMensaje("nombresAntes1%Alan%Carlos", null);
        hiloCliente.procesarMensaje("EmpezarPartida", null);
        hiloCliente.procesarMensaje("CantidaddeClientes%2%Turnode%1", null);

        assertEquals(1, controlador.getTurnoActual());
        assertFalse(controlador.isMiTurno()); // Turno de Carlos (índice 1)
    }

    @Test
    @DisplayName("Procesar mensaje Dados individual")
    void testMensajeDados() {
        hiloCliente.procesarMensaje("Dados%5%2", null); // valor 5 en índice 2
        assertEquals(5, controlador.getDados()[2]);
    }

    @Test
    @DisplayName("Procesar mensaje PuntosDeTurno y SumarAcum")
    void testMensajePuntos() {
        hiloCliente.procesarMensaje("nombresAntes1%Alan%Carlos", null);
        hiloCliente.procesarMensaje("PuntosDeTurno%450", null);
        assertEquals(450, controlador.getPuntosRonda());

        hiloCliente.procesarMensaje("SumarAcum%1200%0", null);
        assertEquals(1200, controlador.getJugadores().get(0).getPuntosTotales());
    }

    @Test
    @DisplayName("Procesar mensaje PuedePlantarse")
    void testMensajePuedePlantarse() {
        assertFalse(controlador.isPuedePlantarse());
        hiloCliente.procesarMensaje("PuedePlantarse%Si", null);
        assertTrue(controlador.isPuedePlantarse());

        hiloCliente.procesarMensaje("PuedePlantarse%No", null);
        assertFalse(controlador.isPuedePlantarse());
    }
}
