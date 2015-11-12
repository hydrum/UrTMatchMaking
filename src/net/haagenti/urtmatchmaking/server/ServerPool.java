package net.haagenti.urtmatchmaking.server;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.queue.Region;

public class ServerPool {
	
	private static ArrayList<Server> serverlist = new ArrayList<Server>();
	
	private ServerPool() { }
	
	public static boolean addServer(Server server) {
		if (server == null) return false;
		if (server.getNetAddress() == null) return false;
		if (server.getRegion() == null) return false;
		serverlist.add(server);
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
			if (server.getRegion().equals(region) && !server.isTaken()) return server;
		}
		return null;
	}
	
	public static boolean removeServer(Server server) {
		if (server.isTaken()) return false;
		
		return serverlist.remove(server);
	}

}
