package net.haagenti.urtmatchmaking;

import net.haagenti.urtmatchmaking.config.Protocol;
import net.haagenti.urtmatchmaking.connection.MMserver;
import net.haagenti.urtmatchmaking.match.MatchType;

public class Main {

	public static void main(String[] args) {
		MMserver CTFserver = new MMserver(new Protocol(MatchType.ctf, 50001));
		Thread ctfThread = new Thread(CTFserver);
		ctfThread.start();
		
		// Posibility to add other Servers
	}

}
