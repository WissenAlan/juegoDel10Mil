package com.server10mil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.utilidades.Dados;
import main.java.utilidades.Utiles;

public class HiloServidor extends Thread {

    private final DatagramSocket conexion;
    private volatile boolean fin = false;
    private boolean empieza;
    private int cantClientes = 0, turno = 0;
    private final DireccionRed[] clientes = new DireccionRed[6];
    private final String[] nombres = new String[6];
    private String nombresConcat;

    private int[] dados = {-1, -1, -1, -1, -1};
    private int[] puntosTotales;
    private int ultimoDado = -1;
    private int indiceTriple = -1;
    private int puntosTurno;
    private final int puntajeGanar = 10000;
    private final Random r = Utiles.R;

    private static final Logger LOGGER = Logger.getLogger(LanzarServidor.class.getName());

    public HiloServidor() {
        try {
            // Escuchar en TODAS las interfaces locales (0.0.0.0).
            // El hostname DNS externo (10kgame.duckdns.org) apunta a este servidor
            // desde afuera, pero NO es una IP asignada a la interfaz de red local,
            // por lo que usarlo en el bind lanza SocketException y deja conexion = null.
            conexion = new DatagramSocket(5302);
            LOGGER.info("Servidor escuchando en el puerto 5302");
        } catch (SocketException e1) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir el socket UDP en el puerto 5302", e1);
            throw new RuntimeException("No se pudo iniciar el servidor", e1);
        }
    }


    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (!fin && conexion != null && !conexion.isClosed()) {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                conexion.receive(dp);
                recibirMensaje(dp);
            } catch (IOException e) {
                if(!fin)
                    LOGGER.log(Level.SEVERE, "Error en la recepción de datos UDP", e);
                else
                    LOGGER.info("Socket UDP cerrado correctamente para apagar el hilo.");
            }
        }
    }

    public void enviarMensaje(String msg, InetAddress ip, int puerto) {
        if (conexion == null || conexion.isClosed() || ip == null) {
            LOGGER.warning("No se puede enviar el mensaje: Socket o IP no válidos.");
            return;
        }
        try {
            byte[] data = msg.getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
            conexion.send(dp);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en la recepción de datos UDP", e);
        }
    }

    public void terminar() {
        this.fin = true;
        if (conexion != null && !conexion.isClosed()) {
            conexion.close(); // Cierra el socket para desbloquear el método receive()
        }
    }

    public void enviarMensajeATodos(String msg) {
        int i = 0;
        boolean salir = false;
        while (i < clientes.length && !salir) {
            if (clientes[i] != null) {
                enviarMensaje(msg, clientes[i].ip(), clientes[i].puerto());
            } else {
                salir = true;
            }
            i++;
        }

    }

    private void recibirMensaje(DatagramPacket dp) {
        String msg = (new String(dp.getData())).trim();
        String[] msgConcat = msg.split("%");
        int nroCliente = getNroCliente(dp);

        // --- Registro de nuevo cliente ---
        // El cliente envía "Conexion%<nombre>". Usamos msgConcat[0] para comparar
        // solo el comando, y msgConcat[1] para obtener el nombre del jugador.
        if (cantClientes < 6 && msgConcat[0].equals("Conexion") && nroCliente == cantClientes) {
            String nombreCliente = (msgConcat.length > 1) ? msgConcat[1] : "Jugador" + (cantClientes + 1);
            clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
            nombres[cantClientes] = nombreCliente;
            int nroAsignado = cantClientes;
            cantClientes++;
            enviarMensaje("OK%" + nroAsignado, clientes[nroAsignado].ip(), clientes[nroAsignado].puerto());
            hacerAdminCliente();
            // Avisar a TODOS (incluyendo el recién llegado) la lista actualizada
            setNombresConcat();
            enviarMensajeATodos("nombresAntes1" + nombresConcat);
            LOGGER.info("Cliente conectado: " + nombreCliente + " (#" + nroAsignado + "). Total: " + cantClientes);
            return; // El paquete de conexión no debe procesarse como otro comando
        }

        if (msgConcat[0].equals("ClicTirarBtn")) {
            verificarUltimoDado();
            generarDadoAlAzar();
        } else if (msgConcat[0].equals("SumarPuntosTotales")) {
            puntosTotales[turno] += puntosTurno;
            enviarMensajeATodos("SumarAcum%" + puntosTotales[turno] + "%" + turno);
            if (puntosTotales[turno] == puntajeGanar) {
                enviarMensajeATodos("HayGanadorDeJuego");
            } else if (nroCliente == turno) {
                cambiarTurno();
            }

        } else if (msgConcat[0].equals("Empezar")) {
            empieza = true;
            puntosTotales = new int[cantClientes];
            enviarMensajeATodos("EmpezarPartida");
        } else if (msgConcat[0].equals("TurnoDeQuien?")) {
            enviarMensajeATodos("CantidaddeClientes%" + cantClientes + "%Turnode%" + turno);
        } else if (msg.equalsIgnoreCase("powerOff")) {
            enviarMensaje("Adios.", clientes[nroCliente].ip(), clientes[nroCliente].puerto());
            clientes[nroCliente] = null;
            nombres[nroCliente] = null;
            cantClientes--;
            ordenarClientes(nroCliente);
            if (!empieza) {
                setNombresConcat();
                enviarMensajeATodos("nombresAntes2%" + nroCliente + nombresConcat);
                hacerAdminCliente();
            } else {
                setNombresConcat();
                enviarMensajeATodos("nombresDespues%" + nroCliente + nombresConcat);
                if (cantClientes == 1) {
                    enviarMensajeATodos("HayGanadorDeJuego");
                } else if (turno == nroCliente && nroCliente == cantClientes + 1) {
                    cambiarTurno();
                } else {
                    resetearVariables();
                    enviarMensajeATodos("ResetearVariables");
                    enviarMensajeATodos("CantidaddeClientes%" + cantClientes + "%Turnode%" + turno);
                }
            }
        }
    }

    private int getNroCliente(DatagramPacket dp) {
        int nroCliente = -1;
        int i = 0;
        boolean salir = false;
        do {
            if (clientes[i] != null) {
                if (dp.getPort() == clientes[i].puerto() && dp.getAddress().equals(clientes[i].ip())) {
                    nroCliente = i;
                    salir = true;
                } else {
                    i++;
                }
            } else {
                nroCliente = i;
                salir = true;
            }
        } while (i < clientes.length && !salir);
        return nroCliente;
    }

    private void setNombresConcat() {
        nombresConcat = null;
        for (int i = 0; i < cantClientes; i++) {
            nombresConcat = (nombresConcat == null) ? "%" + nombres[i] : nombresConcat + "%" + nombres[i];
        }
    }

    private void hacerAdminCliente() {
        if (cantClientes == 1) {
            enviarMensaje("imAdmin%", clientes[0].ip(), clientes[0].puerto());
        }
    }

    private void ordenarClientes(int nroCliente) {
        int indice = nroCliente;
        boolean salir = false;
        do {
            if (clientes[indice + 1] != null) {
                clientes[indice] = clientes[indice + 1];
                nombres[indice] = nombres[indice + 1];
            } else {
                salir = true;
            }
            indice++;
        } while (indice < clientes.length && !salir);
    }

    private void cambiarTurno() {
        if (turno == cantClientes - 1) {
            turno = 0;
        } else {
            turno++;
        }
        resetearVariables();
        enviarMensajeATodos("ResetearVariables");
        enviarMensajeATodos("CantidaddeClientes%" + cantClientes + "%Turnode%" + turno);
    }

    private void resetearVariables() {
        Arrays.fill(dados, -1);
        ultimoDado = -1;
        indiceTriple = -1;
        puntosTurno = 0;
    }

    private void generarDadoAlAzar() {
        int i = dados.length - 1;
        while (i >= 0 && dados[i] == -1) {
            dados[i] = r.nextInt(6);
            System.out.print(dados[i] + " - ");
            ultimoDado = i;
            i--;
        }
        verificarCombinaciones();
        for (int j = 0; j < dados.length; j++) {
            enviarMensajeATodos("Dados%" + dados[j] + "%" + j);
        }
        enviarMensajeATodos("UltimoDadoEs%" + ultimoDado);
    }

    private void verificarUltimoDado() {
        boolean limpiarArray = true;
        if (ultimoDado != -1) {
            for (int i = inicializarContar(); i < dados.length; i++) {
                if (!Dados.values()[dados[i]].isContable(dados[i])) {
//					dados[i].dispose();
                    dados[i] = -1;
                    limpiarArray = false;
                }
            }
            if (limpiarArray) {
                dados = new int[5];
                Arrays.fill(dados, -1);
            }
            indiceTriple = -1;
            ultimoDado = -1;
            enviarMensajeATodos("ResetearVariables%");
        }
    }

    private void verificarCombinaciones() {
        verificarTriples();
        if (sumarPuntos()) {
            enviarMensajeATodos("PuntosDeTurno%" + puntosTurno);
            if ((puntosTotales[turno] >= 750 || (puntosTurno) >= 750) && puntosTurno + puntosTotales[turno] <= puntajeGanar && (!(indiceTriple != -1 && inicializarContar() == dados.length) || (indiceTriple == -1)) && !Dados.values()[dados[4]].isContable(dados[4])) {
                enviarMensaje("PuedePlantarse%Si", clientes[turno].ip(), clientes[turno].puerto());
            } else if (puntosTotales[turno] + puntosTurno > puntajeGanar) {
                cambiarTurno();
            } else if ((indiceTriple != -1 && inicializarContar() == dados.length) || Dados.values()[dados[4]].isContable(dados[4])) {
                enviarMensaje("PuedePlantarse%No", clientes[turno].ip(), clientes[turno].puerto());
            }
        } else {
            cambiarTurno();
        }

    }

    private void verificarTriples() {
        if (ultimoDado < 3) {
            boolean salir = false;
            int i = ultimoDado;
            do {
                int iguales = 0;
                int diferentes = 0;
                int j = i + 1;
                boolean salir2 = false;
                while (j < dados.length && !salir2 && diferentes < (dados.length - (i + 2))) {
                    if (dados[i] == dados[j]) {
                        iguales++;
                        if (iguales == 2) {
                            salir = true;
                            salir2 = true;
                            indiceTriple = i;
                            enviarMensajeATodos("IndiceTripleEs%" + indiceTriple);
                            if ((i < 2 && j > (i + 2)) || i != ultimoDado) {
                                ordenarDadosTriples();
                            }
                        }
                    } else {
                        diferentes++;
                    }
                    j++;
                }
                i++;
            } while (i < dados.length - 2 && !salir);
        }
        if (indiceTriple != -1) {
            puntosTurno += Dados.values()[dados[indiceTriple]].getTriple();
        }
    }

    private void ordenarDadosTriples() {
        for (int i = ultimoDado; i < dados.length; i++) {
            if (dados[i] != dados[indiceTriple]) {
                int j = dados.length - 1;
                boolean salir = false;
                do {
                    if (dados[j] == dados[indiceTriple]) {
                        int aux;
                        aux = dados[i];
                        dados[i] = dados[j];
                        dados[j] = aux;
                        indiceTriple = i;
                        salir = true;
                    }
                    j--;
                } while (j > i && !salir);
            }
        }
        enviarMensajeATodos("IndiceTripleEs%" + indiceTriple);
    }

    private boolean sumarPuntos() {
        int contar = inicializarContar();
        boolean ordenar = false;
        for (int i = contar; i < dados.length; i++) {
            if (Dados.values()[dados[i]].isContable(dados[i])) {
                puntosTurno += Dados.values()[dados[i]].getPunto(dados[i]);
                ordenar = true;
            }
        }
        if (ordenar) {
            ordenarDadosIndividuales();
        }
        return indiceTriple != -1 || ordenar;
    }

    private int inicializarContar() {
        int contar = ultimoDado;
        if (indiceTriple != -1) {
            return contar + 3;
        }
        return contar;
    }

    private void ordenarDadosIndividuales() {
        int i = inicializarContar();
        boolean salir = false;
        do {
            if (!Dados.values()[dados[i]].isContable(dados[i])) {
                int j = i + 1;
                do {
                    if (Dados.values()[dados[j]].isContable(dados[j])) {
                        int aux;
                        aux = dados[i];
                        dados[i] = dados[j];
                        dados[j] = aux;
                        i++;
                        j = i + 1;
                    } else {
                        j++;
                    }
                } while (j < dados.length);
                salir = true;
            }
            i++;
        } while (i < dados.length - 1 && !salir);
    }
}
