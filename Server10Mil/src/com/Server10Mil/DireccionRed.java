package com.Server10Mil;

import java.net.InetAddress;

public class DireccionRed {

	private InetAddress ip;
	private int puerto;

	public DireccionRed(InetAddress ip, int puerto) {
		this.ip = ip;
		this.puerto = puerto;
	}

	public int getPuerto() {
		return puerto;
	}

	public InetAddress getIp() {
		return ip;
	}

}
