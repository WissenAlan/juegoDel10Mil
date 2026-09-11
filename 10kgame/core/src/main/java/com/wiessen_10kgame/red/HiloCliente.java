package com.wiessen_10kgame.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

import com.badlogic.gdx.Gdx;
import com.wiessen_10kgame.ui.pantallas.SalaEspera;
import com.wiessen_10kgame.ui.pantallas.SalaJuego;

public class HiloCliente extends Thread {

    private DatagramSocket conexion;
    private InetAddress ipServer;
    private boolean fin = false, admin = false;
    private int nroJugador, turno, dados[] = new int[5];
    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;

    public HiloCliente(String host, String nombreJugador) {
        try {
            ipServer = InetAddress.getByName(host);
            conexion = new DatagramSocket();
            enviarMensaje("Conexion%" + nombreJugador);
        } catch (UnknownHostException e) {
            System.err.println("Host no encontrado: " + host);
        } catch (IOException e) {
            Gdx.app.error("RED", "Error de comunicación en el socket", e);
        }
        enviarMensaje("Conexion");
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        while (!fin) {
            DatagramPacket dp = new DatagramPacket(data, data.length);

            try {
                conexion.receive(dp);
                recibirMensaje(dp);
            } catch (IOException e) {
                if (!fin) Gdx.app.error("RED", "Servidor desconectado :/", e);
                fin = true;
            }
            if (conexion != null && !conexion.isClosed()) conexion.close();
        }
    }

    private void recibirMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData(), 0, dp.getLength()).trim();
        String[] msgParametrizado = msg.split("%");
        if (msgParametrizado[0].equalsIgnoreCase("imAdmin")) {
            admin = true;
        } else if (msgParametrizado[0].equalsIgnoreCase("nombresAntes1")) {
            for (int i = 1; i < msgParametrizado.length; i++) {
                SalaEspera.setJugadores(i - 1, msgParametrizado[i]);
            }
            SalaEspera.crear();
        } else if (msgParametrizado[0].equalsIgnoreCase("nombresAntes2")) {
            int j = Integer.parseInt(msgParametrizado[1]);
            boolean salir = false;
            do {
                if (SalaEspera.getJugadores()[j] != null) {
                    SalaEspera.setJugadores(j, null);
                } else {
                    salir = true;
                }
                j++;
            } while (j < SalaEspera.getJugadores().length && !salir);
            for (int i = 2; i < msgParametrizado.length; i++) {
                SalaEspera.setJugadores((i - 2), msgParametrizado[i]);
                System.out.println((i - 2) + "   " + msgParametrizado[i]);
            }
            SalaEspera.crear();
        } else if (msgParametrizado[0].equalsIgnoreCase("nombresDespues")) {
            for (int i = 2; i < msgParametrizado.length; i++) {
                SalaJuego.setJugadores(i - 2, msgParametrizado[i]);
            }
            SalaJuego.recrearPuntosTotales(Integer.parseInt(msgParametrizado[1]));
        } else if (msgParametrizado[0].equals("OK")) {
            ipServer = dp.getAddress();
            setNroJugador(Integer.parseInt(msgParametrizado[1]));
            System.out.println("Conectado. " + nroJugador);
        } else if (msgParametrizado[0].equals("EmpezarPartida")) {
            int indice = 0;
            boolean fin = false;
            do {
                if (SalaEspera.getJugadores()[indice] != null) {
                    SalaEspera.empezar();
                } else {
                    fin = true;
                }
                indice++;
            } while (indice < SalaEspera.getJugadores().length && !fin);
        } else if (msgParametrizado[0].equals("CantidaddeClientes")) {
            if (msgParametrizado[2].equals("Turnode")) {
                SalaJuego.setTurno(Integer.parseInt(msgParametrizado[3]));
//				turno = Integer.parseInt(msgParametrizado[3]);
            }
        } else if (msgParametrizado[0].equals("PuntosDeTurno")) {
            SalaJuego.actualizarPuntoRonda(Integer.parseInt(msgParametrizado[1]));
        } else if (msgParametrizado[0].equals("SumarAcum")) {
            SalaJuego.actualizarPuntoTotal(Integer.parseInt(msgParametrizado[1]), Integer.parseInt(msgParametrizado[2]));
        } else if (msgParametrizado[0].equals("HayGanadorDeJuego")) {
            SalaJuego.HayGanador();
        } else if (msgParametrizado[0].equals("Dados")) {
            for (int i = 0; i < dados.length; i++) {
                dados[Integer.parseInt(msgParametrizado[2])] = Integer.parseInt(msgParametrizado[1]);
            }
            SalaJuego.actualizarDados(dados);
        } else if (msgParametrizado[0].equals("PuedePlantarse")) {
            if (msgParametrizado[1].equals("Si")) {
                SalaJuego.puedePlantarse(true);
            } else {
                SalaJuego.puedePlantarse(false);
            }
        } else if (msgParametrizado[0].equals("IndiceTripleEs")) {
            SalaJuego.setIndiceTriple(Integer.parseInt(msgParametrizado[1]));
        } else if (msgParametrizado[0].equals("ResetearVariables")) {
            SalaJuego.reiniciarVariables();
        } else if (msgParametrizado[0].equals("UltimoDadoEs")) {
            SalaJuego.setUltimoDado(Integer.parseInt(msgParametrizado[1]));
        } else {
            System.out.println("Servidor dice: " + msg);
        }
    }

    private void setNroJugador(int nro) {
        nroJugador = nro;
    }

    public void enviarMensaje(String msg) {
        if (ipServer == null || conexion == null) return;

        byte[] data = msg.getBytes();
        int puerto = 5302;
        DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
        try {
            conexion.send(dp);
        } catch (IOException e) {
            Gdx.app.error("RED", "Servidor desconectado:/", e);
        }
    }

    public void terminar() {
        fin = true;
        enviarMensaje("powerOff");
        if (conexion != null && !conexion.isClosed()) conexion.close();
    }

    public boolean isAdmin() {
        return admin;
    }

    public int getTurno() {
        return turno;
    }
}
