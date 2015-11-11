package net.haagenti.urtmatchmaking.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import net.haagenti.urtmatchmaking.config.Config;

public class MMserver implements Runnable {
	
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	private byte[] sendData;
	
	public Config cfg;
	
	public MMserver(Config cfg) {
		this.cfg = cfg;
		setup();
	}

	public void setup() {
		try {
			serverSocket = new DatagramSocket(cfg.getMMport());
			receiveData = new byte[1024];
			sendData = new byte[1024];
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				NetAddress address = new NetAddress(receivePacket.getAddress(), receivePacket.getPort());
				String response = cfg.processPacket(new String(receivePacket.getData()), address);
				if (response != null)
					send(address, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(NetAddress address, String data) {
		try {
			sendData = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address.address, address.port);
			serverSocket.send(sendPacket);	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
