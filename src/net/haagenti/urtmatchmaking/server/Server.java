package net.haagenti.urtmatchmaking.server;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.queue.Region;

public class Server {
	
	private Match match;
	
	private NetAddress address;
	private Region region;
	
	public String password;
	
	private boolean isTaken = false;
	
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

	public Region getRegion() {
		return region;
	}

}
