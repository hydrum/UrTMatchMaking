package net.haagenti.urtmatchmaking.server;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.queue.Region;

public class Server {
	
	private Match match;

	private NetAddress pubAddress;
	private NetAddress qAddress;
	private Region region;
	private String rcon;
	
	public String password;
	
	private boolean isTaken = false;
	
	public Server(NetAddress pubAddress, NetAddress qAddress, String rcon, Region region) {
		this.pubAddress = pubAddress;
		this.qAddress = qAddress;
		this.setRcon(rcon);
		this.region = region;
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
		return qAddress;
	}

	public NetAddress getPublicAddress() {
		return pubAddress;
	}

	public Region getRegion() {
		return region;
	}

	public String getRcon() {
		return rcon;
	}

	public void setRcon(String rcon) {
		this.rcon = rcon;
	}

}
