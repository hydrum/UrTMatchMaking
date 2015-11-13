package net.haagenti.urtmatchmaking.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.config.Protocol;

public class MMserver {
	
	private static DatagramSocket serverSocket;
	private static byte[] receiveData;
	private static byte[] sendData;

	public static Protocol protocol;
	private static int port;
	
	private MMserver() { }

	public static void init(int port) {
		try {

			Debug.Log(TAG.MAIN, "initating database");
			Database.initConnection();
			
			Debug.Log(TAG.MMSERVER, "Setting up MMserver");
			
			protocol = new Protocol();
			MMserver.port = port;
			
			serverSocket = new DatagramSocket(port);
			receiveData = new byte[1024];
			sendData = new byte[1024];
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static void run() {
		while (true) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				NetAddress address = new NetAddress(receivePacket.getAddress(), receivePacket.getPort());
				String response = protocol.processPacket(new String(receivePacket.getData()), address);
				if (response != null)
					Debug.Log(TAG.MMSERVER, "Sending reply package");
					MMserver.send(address, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void send(NetAddress address, String data) {
		try {
			sendData = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address.address, address.port);
			serverSocket.send(sendPacket);	
			Debug.Log(TAG.MMSERVER, "Sent Packet to " + address.address + ":" + address.port + ": " + sendData);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
