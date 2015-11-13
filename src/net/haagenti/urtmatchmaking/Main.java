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
		ServerPool.addServer(new Server(new NetAddress("127.0.0.1", 27960), new NetAddress("127.0.0.1", 1337), "1337rcon", Region.eu));
		
		Debug.Log(TAG.MAIN, "Starting MMserver");
		MMserver.init(1337);
		MMserver.run();
		
	}

}
