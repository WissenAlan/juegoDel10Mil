package com.wiessen_10kgame.red;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

/**
 * Hilo de red del cliente. Escucha paquetes UDP del servidor y los transforma en
 * llamadas al {@link PartidaListener} siempre en el hilo principal de LibGDX.
 * <p>
 * <b>No conoce a ninguna pantalla ni al {@code ControladorJuego} directamente.</b>
 * Solo depende de la interfaz {@link PartidaListener}.
 * </p>
 */
public class HiloCliente extends Thread {

    private DatagramSocket conexion;
    private InetAddress ipServer;
    private boolean fin = false;
    private int nroJugador;
    private final String nombreJugador;
    private PartidaListener listener;

    // -----------------------------------------------------------------------
    // Construcción
    // -----------------------------------------------------------------------

    /**
     * Constructor principal: abre el socket y envía el mensaje de conexión inicial.
     *
     * @param host         dirección del servidor
     * @param nombreJugador nombre con el que el cliente se identifica
     * @param listener     receptor de eventos de red (normalmente un {@code ControladorJuego})
     */
    public HiloCliente(String host, String nombreJugador, PartidaListener listener) {
        this.nombreJugador = nombreJugador;
        this.listener = listener;
        try {
            ipServer = InetAddress.getByName(host);
            conexion = new DatagramSocket();
            enviarMensaje("Conexion%" + nombreJugador);
        } catch (UnknownHostException e) {
            System.err.println("Host no encontrado: " + host);
        } catch (IOException e) {
            if (Gdx.app != null) {
                Gdx.app.error("RED", "Error de comunicación en el socket", e);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Getters / Setters del listener
    // -----------------------------------------------------------------------

    public void setListener(PartidaListener listener) {
        this.listener = listener;
    }

    public PartidaListener getListener() {
        return listener;
    }

    public int getNroJugador() {
        return nroJugador;
    }

    // -----------------------------------------------------------------------
    // Ciclo de vida del hilo
    // -----------------------------------------------------------------------

    @Override
    public void run() {
        byte[] data = new byte[1024];
        while (!fin) {
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.receive(dp);
                    recibirMensaje(dp);
                } else {
                    fin = true;
                }
            } catch (IOException e) {
                if (!fin && Gdx.app != null) {
                    Gdx.app.error("RED", "Servidor desconectado :/", e);
                }
                fin = true;
            }
        }
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    // -----------------------------------------------------------------------
    // Procesamiento de mensajes
    // -----------------------------------------------------------------------

    /** Extrae el string del datagrama y delega en {@link #procesarMensaje}. */
    public void recibirMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData(), 0, dp.getLength()).trim();
        procesarMensaje(msg, dp.getAddress());
    }

    /**
     * Parsea el mensaje recibido y llama al método correspondiente del {@link PartidaListener}
     * en el hilo principal de LibGDX mediante {@code Gdx.app.postRunnable}.
     *
     * @param msg       texto del paquete recibido
     * @param remitente dirección IP del remitente (puede ser null en tests)
     */
    public void procesarMensaje(String msg, InetAddress remitente) {
        if (msg == null || msg.isEmpty()) return;

        String[] partes = msg.split("%");
        String comando = partes[0];

        ejecutarEnHiloPrincipal(() -> {
            if (listener == null) return;

            switch (comando) {
                case "imAdmin":
                    listener.onAdmin();
                    break;

                case "nombresAntes1": {
                    List<String> nombres = new ArrayList<>();
                    for (int i = 1; i < partes.length; i++) nombres.add(partes[i]);
                    listener.onNombresActualizados(nombres, true);
                    break;
                }

                case "nombresAntes2": {
                    List<String> nombres = new ArrayList<>();
                    for (int i = 2; i < partes.length; i++) nombres.add(partes[i]);
                    listener.onNombresActualizados(nombres, false);
                    break;
                }

                case "nombresDespues":
                    listener.onJugadorRemovido(Integer.parseInt(partes[1]));
                    break;

                case "OK":
                    InetAddress srv = remitente;
                    if (srv != null) ipServer = srv;
                    nroJugador = Integer.parseInt(partes[1]);
                    listener.onConexionOk(srv, nroJugador);
                    break;

                case "EmpezarPartida":
                    listener.onPartidaEmpezada();
                    break;

                case "CantidaddeClientes":
                    if (partes.length > 3 && partes[2].equals("Turnode")) {
                        listener.onTurnoCambiado(Integer.parseInt(partes[3]));
                    }
                    break;

                case "PuntosDeTurno":
                    listener.onPuntosRondaActualizados(Integer.parseInt(partes[1]));
                    break;

                case "SumarAcum":
                    listener.onPuntosTotalesActualizados(
                        Integer.parseInt(partes[1]),
                        Integer.parseInt(partes[2])
                    );
                    break;

                case "HayGanadorDeJuego":
                    listener.onGanadorDeclarado();
                    break;

                case "Dados":
                    listener.onDadoActualizado(
                        Integer.parseInt(partes[2]),
                        Integer.parseInt(partes[1])
                    );
                    break;

                case "PuedePlantarse":
                    listener.onPuedePlantarse(partes[1].equalsIgnoreCase("Si"));
                    break;

                case "IndiceTripleEs":
                    listener.onIndiceTriple(Integer.parseInt(partes[1]));
                    break;

                case "ResetearVariables":
                    listener.onVariablesReiniciadas();
                    break;

                case "UltimoDadoEs":
                    listener.onUltimoDado(Integer.parseInt(partes[1]));
                    break;

                default:
                    // Comando desconocido: ignorar silenciosamente
                    break;
            }
        });
    }

    // -----------------------------------------------------------------------
    // Envío de mensajes
    // -----------------------------------------------------------------------

    /** Envía un mensaje UDP al servidor. */
    public void enviarMensaje(String msg) {
        if (ipServer == null || conexion == null || conexion.isClosed()) return;

        byte[] data = msg.getBytes();
        int puerto = 5302;
        DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
        try {
            conexion.send(dp);
        } catch (IOException e) {
            if (Gdx.app != null) {
                Gdx.app.error("RED", "Error enviando mensaje por socket: " + msg, e);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Cierre
    // -----------------------------------------------------------------------

    /** Detiene el hilo y cierra el socket de forma segura. */
    public void terminar() {
        fin = true;
        enviarMensaje("powerOff");
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    // -----------------------------------------------------------------------
    // Helpers internos
    // -----------------------------------------------------------------------

    /**
     * Ejecuta la acción en el hilo principal de LibGDX si está disponible;
     * de lo contrario la ejecuta directamente (útil en tests sin LibGDX).
     */
    private void ejecutarEnHiloPrincipal(Runnable accion) {
        if (Gdx.app != null) {
            Gdx.app.postRunnable(accion);
        } else {
            accion.run();
        }
    }
}
