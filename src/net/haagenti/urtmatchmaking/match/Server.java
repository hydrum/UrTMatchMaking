package net.haagenti.urtmatchmaking.match;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.queue.Region;

public class Server {
	
	private Match match;
	
	private NetAddress address;
	public Region region;
	
	public String password;
	
	boolean isTaken = false;
	
	public Server(NetAddress address, Region region) {
		this.address = address;
	}
	
	public void take(Match match) {
		this.match = match;
		isTaken = true;
	}
	
	public void free() {
		this.match = null;
		isTaken = false;
	}
	
	public boolean isTaken() {
		return isTaken;
	}

	public NetAddress getNetAddress() {
		return address;
	}

	public NetAddress getPublicAddress() {
		return address;
	}

}
