package com.Server10Mil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import utilidades.Dados;
import utilidades.Utiles;

public class HiloServidor extends Thread {

//	private ServerSocket server;
	private DatagramSocket conexion;
	private boolean fin = false, empieza;
	private int cantClientes = 0, turno = 0;
	private DireccionRed[] clientes = new DireccionRed[6];
	private String[] nombres = new String[6];
	private String nombresConcat;

	private int dados[] = { -1, -1, -1, -1, -1 };
	private int puntosTotales[], ultimoDado = -1, indiceTriple = -1, puntosTurno, puntajeGanar = 10000;
	private Random r = Utiles.R;

	public HiloServidor() {
		try {
			conexion = new DatagramSocket(5302, InetAddress.getByName("192.168.100.2")); //IPV4 DE LA COMPUTADORA, NO DE HAMACHI
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!fin) {
			byte[] data = new byte[1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			try {
				conexion.receive(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			recibirMensaje(dp);
		}
	}

	public void enviarMensaje(String msg, InetAddress ip, int puerto) {
		byte[] data = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
		try {
			conexion.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void terminar() {
		fin = true;
	}

	public void enviarMensajeATodos(String msg) {
		int i = 0;
		boolean salir = false;
		do {
			if (clientes[i] != null) {
				enviarMensaje(msg, clientes[i].getIp(), clientes[i].getPuerto());
			} else {
				salir = true;
			}
			i++;
		} while (i < clientes.length && !salir);

	}

	private void recibirMensaje(DatagramPacket dp) {
		String msg = (new String(dp.getData())).trim();
		System.out.println(msg);
		String[] msgConcat = msg.split("%");
		int nroCliente = -1;
		int i = 0;
		boolean salir = false;
		do {
			if (clientes[i] != null) {
				if (dp.getPort() == clientes[i].getPuerto() && dp.getAddress().equals(clientes[i].getIp())) {
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

		if (cantClientes < 6) { // Si todavia no estan conectados ambos clientes
			if (msg.equals("Conexion")) {
				clientes[cantClientes] = new DireccionRed(dp.getAddress(), dp.getPort());
				enviarMensaje("OK%" + (cantClientes + 1), clientes[cantClientes].getIp(),
						clientes[cantClientes++].getPuerto());
				hacerAdminCliente();
			}

		} else { // Si estan conectados, comienzo con las verificaciones de jugabilidad
			if (empieza) {
				if (msg.equals("ApreteDerecha")) {
//					mapa.getMt()[nroCliente][1] = 1;
				} else if (msg.equals("ApreteIzquierda")) {
//					mapa.getMt()[nroCliente][0] = 1;
				}
			}
		}
		if (msgConcat[0].equals("ClicTirarBtn")) {
			verificarUltimoDado();
			generarDadoAlAzar();
		} else if (msgConcat[0].equals("SumarPuntosTotales")) {
			System.out.println(puntosTotales[turno] + "    " + puntosTurno + "   " + turno);
			puntosTotales[turno] += puntosTurno;
			enviarMensajeATodos("SumarAcum%" + puntosTotales[turno] + "%" + turno);
			if (puntosTotales[turno] == puntajeGanar) {
				enviarMensajeATodos("HayGanadorDeJuego");
			} else if (nroCliente == turno) {
				cambiarTurno();
			}

		} else if (msgConcat[0].equals("nombre")) {
			nombres[nroCliente] = msgConcat[1];
			enviarMensaje("Hola: " + nombres[nroCliente], clientes[nroCliente].getIp(),
					clientes[nroCliente].getPuerto());
			setNombresConcat();
			if (cantClientes >= 2) {
//				empieza = true;
				enviarMensajeATodos("nombresAntes1" + nombresConcat);
			} else {
				enviarMensaje("nombresAntes1" + nombresConcat, clientes[nroCliente].getIp(),
						clientes[nroCliente].getPuerto());
			}
		} else if (msgConcat[0].equals("Empezar")) {
			empieza = true;
			puntosTotales = new int[cantClientes];
			enviarMensajeATodos("EmpezarPartida");
		} else if (msgConcat[0].equals("TurnoDeQuien?")) {
			enviarMensajeATodos("CantidaddeClientes%" + cantClientes + "%Turnode%" + turno);
		} else if (msg.equalsIgnoreCase("poweroff")) {
			enviarMensaje("Adios.", clientes[nroCliente].getIp(), clientes[nroCliente].getPuerto());
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

	private void setNombresConcat() {
		nombresConcat = null;
		for (int i = 0; i < cantClientes; i++) {
			nombresConcat = (nombresConcat == null) ? "%" + nombres[i] : nombresConcat + "%" + nombres[i];
		}
	}

	private void hacerAdminCliente() {
		if (cantClientes == 1) {
			enviarMensaje("imAdmin%", clientes[0].getIp(), clientes[0].getPuerto());
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
		for (int i = 0; i < dados.length; i++) {
			dados[i] = -1;
		}
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
		System.out.println("");
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
				dados = null;
				dados = new int[5];
				for (int i = 0; i < dados.length; i++) {
					dados[i] = -1;
				}
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
			System.out.println(puntosTurno);
			if ((puntosTotales[turno] >= 750 || (puntosTurno) >= 750)
					&& puntosTurno + puntosTotales[turno] <= puntajeGanar
					&& (!(indiceTriple != -1 && inicializarContar() == dados.length) || (indiceTriple == -1))
					&& !Dados.values()[dados[4]].isContable(dados[4])) {
				enviarMensaje("PuedePlantarse%Si", clientes[turno].getIp(), clientes[turno].getPuerto());
			} else if (puntosTotales[turno] + puntosTurno > puntajeGanar) {
				cambiarTurno();
			} else if ((indiceTriple != -1 && inicializarContar() == dados.length)
					|| Dados.values()[dados[4]].isContable(dados[4])) {
				enviarMensaje("PuedePlantarse%No", clientes[turno].getIp(), clientes[turno].getPuerto());
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
		boolean actualizarTexto = false;
		if (indiceTriple != -1 || ordenar) {
			actualizarTexto = true;
		}
		return actualizarTexto;
	}

	private int inicializarContar() {
		int contar = ultimoDado;
		if (indiceTriple != -1) {
			return contar += 3;
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
