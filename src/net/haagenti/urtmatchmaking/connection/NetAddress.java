package net.haagenti.urtmatchmaking.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetAddress {
	public InetAddress address;
	public int port;
	
	public NetAddress(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public NetAddress(String address, int port) {
		try {
			this.address = InetAddress.getByName(address);
			this.port = port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
}
