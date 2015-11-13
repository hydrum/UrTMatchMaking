package net.haagenti.urtmatchmaking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.queue.Region;

public class ServerPool {
	
	private static ArrayList<Server> serverlist = new ArrayList<Server>();
	
	private ServerPool() { }
	
	public static boolean addServer(Server server) {
		if (server == null) return false;
		if (server.getNetAddress() == null) return false;
		if (server.getPublicAddress() == null) return false;
		if (server.getRegion() == null) return false;
		serverlist.add(server);
		if (!ServerCheck.isRunning()) new Thread(new ServerCheck());
		return true;
	}
	
	public static Server getAvailableServer() {
		for (Server server : serverlist) {
			if (!server.isTaken()) return server;
		}
		return null;
	}
	
	public static Server getAvailableServer(Region region) {
		for (Server server : serverlist) {
			if (server.getRegion().equals(region) && !server.isTaken() && ServerCheck.isServerAlive(server)) return server;
		}
		return null;
	}
	
	public static Server[] getAllServers() {
		return (Server[]) serverlist.toArray();
	}
	
	public static boolean removeServer(Server server) {
		if (server.isTaken()) return false;
		
		return serverlist.remove(server);
	}
	
	public static String sendRcon(Server server, String command) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setSoTimeout(1000);
			String rcon = "xxxxrcon " + server.getRcon() + " " + command;
			
			byte[] recvBuffer = new byte[2048];
			byte[] sendBuffer = rcon.getBytes();
			
			sendBuffer[0] = (byte) 0xff;
			sendBuffer[1] = (byte) 0xff;
			sendBuffer[2] = (byte) 0xff;
			sendBuffer[3] = (byte) 0xff;
			
			Debug.Log(TAG.SERVER, rcon);
			
			DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
			DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, server.getPublicAddress().address, server.getPublicAddress().port);
			
			socket.send(sendPacket);
			socket.receive(recvPacket);
			
			socket.close();
			
			return new String(recvPacket.getData()).replace("ÿÿÿÿprint\n", "");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
