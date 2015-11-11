package net.haagenti.urtmatchmaking.connection;

import java.net.InetAddress;

public class NetAddress {
	public InetAddress address;
	public int port;
	
	public NetAddress(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
}
