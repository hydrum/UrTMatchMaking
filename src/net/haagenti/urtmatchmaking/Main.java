package net.haagenti.urtmatchmaking;

import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.config.Protocol;
import net.haagenti.urtmatchmaking.connection.Database;
import net.haagenti.urtmatchmaking.connection.MMserver;
import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.MatchType;
import net.haagenti.urtmatchmaking.queue.Region;
import net.haagenti.urtmatchmaking.server.Server;
import net.haagenti.urtmatchmaking.server.ServerPool;

public class Main {

	public static void main(String[] args) {
		Debug.Log(TAG.MAIN, "initating servers");
		ServerPool.addServer(new Server(new NetAddress("127.0.0.1", 27960), Region.eu));
		Debug.Log(TAG.MAIN, "initating database");
		Database.initConnection();

		Debug.Log(TAG.MAIN, "Starting MMserver thread for ctf");
		MMserver CTFserver = new MMserver(new Protocol(MatchType.ctf, 50001));
		Thread ctfThread = new Thread(CTFserver);
		ctfThread.start();
		
	}

}
