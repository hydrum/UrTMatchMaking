package net.haagenti.urtmatchmaking;

import net.haagenti.urtmatchmaking.config.Config;
import net.haagenti.urtmatchmaking.config.ConfigType;
import net.haagenti.urtmatchmaking.connection.MMserver;

public class Main {

	public static void main(String[] args) {
		MMserver CTFserver = new MMserver(new Config(ConfigType.ctf, 50001));
		Thread client = new Thread(CTFserver);
		client.start();
		
		// Posibility to add other Servers
	}

}
