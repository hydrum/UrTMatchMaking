package net.haagenti.urtmatchmaking.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.connection.MMserver;
import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.match.MatchType;
import net.haagenti.urtmatchmaking.player.Player;
import net.haagenti.urtmatchmaking.queue.QueueManager;
import net.haagenti.urtmatchmaking.queue.Region;
import net.haagenti.urtmatchmaking.server.Server;
import net.haagenti.urtmatchmaking.server.ServerPool;

public class Protocol {

	private HashMap<MatchType, QueueManager> queuemanagerlist = new HashMap<MatchType, QueueManager>();;
	
	public Protocol() {
		Debug.Log(TAG.PROTOCOL, "Setting up Protocol...");

		Debug.Log(TAG.PROTOCOL, "Initializing new QueueManager Thread");
		for (MatchType type : MatchType.values()) {
			queuemanagerlist.put(type, new QueueManager(this, type));
			new Thread(queuemanagerlist.get(type)).start();
			
		}
	}
	
	public final String processPacket(String data, NetAddress address) {
		Debug.Log(TAG.PROTOCOL, "New Packet: " + address.address.toString() + " - " + data);
		String[] sdata = data.split("|");
		if (sdata.length == 0) return null;
		
		switch (sdata[0]) {
		case "HELLO": return processHello(sdata, address);
		case "QUEUE": return processQueue(sdata, address);
		case "MATCHACCEPT": return processMatchAccept(sdata, address);
		case "LEAVEQUEUE": return processLeaveQueue(sdata, address);
		case "GETSTATUSINFO": return processGetStatusInfo(sdata, address);
		case "GETMAPLIST": return processGetMapList(sdata, address);
		case "PLAYERLEFT": return processPlayerLeft(sdata, address);
		case "RESULT": return processResult(sdata, address);
		}
		
		return null;
	}
	
	private String processHello(String[] data, NetAddress address) {
		if (data.length == 2) {
			return Player.getStatus(data[1].split(":")[1], address);
		}
		
		if (data.length == 4) {
			return "RESPONSE|HELLO|" + ServerPool.addServer(new Server(address, new NetAddress(data[1].split(":")[1], Integer.valueOf(data[2].split(":")[1])),
					data[3].split(":")[1], Region.valueOf(data[4].split(":")[1])));
		}
		return null;
	}
	
	private String processQueue(String[] data, NetAddress address) {
		if (data.length == 6) {
			for (QueueManager queue : queuemanagerlist.values()) {
				if (data[3].split(":")[1].equals(queue.matchtype.name())) {
					String urtauth = data[1].split(":")[1];
					Region region = Region.valueOf(data[2].split(":")[1]);
					Map map = Map.valueOf(data[3].split(":")[1]);
					String position = data[4].split(":")[1];
					
					Player player = Player.find(urtauth);
					
					if (player != null) {
						player.joinQueue(map, position);
						
						return queue.addPlayer(region, player);
					}
				}
			}
		}
		return "RESPONSE|QUEUE|error";
	}
	
	private String processMatchAccept(String[] data, NetAddress address) {
		if (data.length == 4) {
			Match.find(data[1].split(":")[1]).playerAccept(Player.find(data[2].split(":")[1]), Boolean.valueOf(data[3].split(":")[1]));
		}
		return null;	
	}
	
	private String processLeaveQueue(String[] data, NetAddress address) {
		if (data.length == 3) {
			for (QueueManager queue : queuemanagerlist.values()) {
				queue.leavePlayer(Player.find(data[1].split(":")[1]));		
			}
		}
		return null;	
	}
	
	private String processGetStatusInfo(String[] data, NetAddress address) {
		if (data.length == 1) {
			// TODO stats		
		}
		return null;	
	}
	
	private String processGetMapList(String[] data, NetAddress address) {
		if (data.length == 2) {
			// TODO maplist
		}
		return null;	
	}
	
	private String processPlayerLeft(String[] data, NetAddress address) {
		if (data.length == 3) {
			return Match.find(data[1].split(":")[1]).playerLeft(Player.find(data[2].split(":")[1]));
		}
		return null;	
	}
	
	private String processResult(String[] data, NetAddress address) {
		if (data.length == 3) {
			return Match.find(data[1].split(":")[1]).result(Arrays.copyOfRange(data, 2, data.length-1));
		}
		return null;	
	}
	
	
	
	
	// REQUESTS //

	public void requestAccept(int matchid, Set<Player> players) {
		for (Player player : players) {
			MMserver.send(player.address, "MATCHACCEPT|matchid:" + matchid + "|reply");
		}
	}

	public void acceptFailed(int matchid, Player player, Boolean continued) {
		if (continued) {
			MMserver.send(player.address, "MATCHACCEPT|matchid:" + matchid + "|failed|continue");
		} else {
			MMserver.send(player.address, "MATCHACCEPT|matchid:" + matchid + "|failed|break");
		}
	}

	public void setupGameserver(NetAddress qaddress, int matchid, MatchType type, Map map, String password, Player[] teamred, Player[] teamblue) {
		String teamred_print = teamred[0].getUrTAuth();
		for (int i = 1; i < teamred.length; i++) {
			teamred_print += teamred[i].getUrTAuth();
		}
		String teamblue_print = teamblue[0].getUrTAuth();
		for (int i = 1; i < teamblue.length; i++) {
			teamblue_print += teamblue[i].getUrTAuth();
		}
		
		String print = "SERVERSETUP|matchid:" + matchid + "|config:" + type.name() + "|map:" + map.name() + "|password:"
						+ password + "|redteam:" + teamred_print + "|teamblue:" +teamblue_print;
		MMserver.send(qaddress, print);
	}

	public void acceptSuccess(int matchid, Set<Player> players, NetAddress serverpubaddress, String password) {
		for (Player player : players) {
			MMserver.send(player.address, "MATCHACCEPT|matchid:" + matchid + "|success|server:" + serverpubaddress.address + ":" + serverpubaddress.port + "|password:" + password);
		}
	}
	
}
