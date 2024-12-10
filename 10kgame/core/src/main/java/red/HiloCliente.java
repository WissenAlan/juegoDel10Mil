package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.wiessen_10kgame.SalaEspera;
import com.wiessen_10kgame.SalaJuego;

public class HiloCliente extends Thread {

	private DatagramSocket conexion;
	private InetAddress ipServer;
	private final int PUERTO = 5302;
	private boolean fin = false, admin = false;
	private int nroJugador, turno, dados[] = new int[5];

	public HiloCliente() {
		try {
			ipServer = InetAddress.getByName("192.168.100.2");
			conexion = new DatagramSocket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		enviarMensaje("Conexion");
	}

	@Override
	public void run() {
		while (!fin) {
			byte[] data = new byte[1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			try {
				conexion.receive(dp);
			} catch (IOException e) {
				System.err.println("Server disconect :/");
				fin = true;
			}
			recibirMensaje(dp);
		}
	}

	private void recibirMensaje(DatagramPacket dp) {
		String msg = (new String(dp.getData())).trim();
		String[] msgParametrizado = msg.split("%");
		System.out.println(msg);
		if (msgParametrizado[0].equalsIgnoreCase("imadmin")) {
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
			SalaJuego.actualizarPuntoTotal(Integer.parseInt(msgParametrizado[1]),
					Integer.parseInt(msgParametrizado[2]));
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
		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, PUERTO);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void terminar() {
		fin = true;
		enviarMensaje("poweroff");
	}

	public boolean isAdmin() {
		return admin;
	}

	public int getTurno() {
		return turno;
	}
}
