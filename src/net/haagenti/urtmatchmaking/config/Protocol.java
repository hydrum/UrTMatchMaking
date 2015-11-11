package net.haagenti.urtmatchmaking.config;

import java.util.Arrays;
import java.util.Set;

import net.haagenti.urtmatchmaking.connection.MMserver;
import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.match.MatchType;
import net.haagenti.urtmatchmaking.queue.QueueManager;
import net.haagenti.urtmatchmaking.queue.QueuePlayer;
import net.haagenti.urtmatchmaking.queue.Region;

public class Protocol {
	
	public int port;

	private QueueManager queue;
	public MMserver mmserver;
	
	public Protocol(MatchType type, int port) {
		this.port = port;
		
		queue = new QueueManager(this, type);
		Thread queueThread = new Thread(queue);
		queueThread.start();
	}
	
	public final String processPacket(String data, NetAddress address) {
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
			return queue.checkPlayerStatus(QueuePlayer.find(data[1].split(":")[1]));
		}
		return null;
	}
	
	private String processQueue(String[] data, NetAddress address) {
		if (data.length == 6) {
			if (data[3].split(":")[1].equals(queue.matchtype.name())) {
				String urtauth = data[1].split(":")[1];
				Region region = Region.valueOf(data[2].split(":")[1]);
				Map map = Map.valueOf(data[3].split(":")[1]);
				String position = data[4].split(":")[1];
				
				return queue.addPlayer(region, new QueuePlayer(address, urtauth, map, position));
			}
		}
		return "RESPONSE|QUEUE|error";	
	}
	
	private String processMatchAccept(String[] data, NetAddress address) {
		if (data.length == 3) {
			QueuePlayer.find(data[1].split(":")[1]).acceptMatch(Boolean.valueOf(data[2].split(":")[1]));
		}
		return null;	
	}
	
	private String processLeaveQueue(String[] data, NetAddress address) {
		if (data.length == 3) {
			queue.leavePlayer(QueuePlayer.find(data[1].split(":")[1]));			
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
			return Match.find(data[1].split(":")[1]).playerLeft(QueuePlayer.find(data[2].split(":")[1]));
		}
		return null;	
	}
	
	private String processResult(String[] data, NetAddress address) {
		if (data.length == 3) {
			return Match.find(data[1].split(":")[1]).result(Arrays.copyOfRange(data, 2, data.length-1));
		}
		return null;	
	}

	public void requestAccept(Set<QueuePlayer> players) {
		for (QueuePlayer player : players) {
			mmserver.send(player.address, "MATCHACCEPT|reply");
		}
	}

	public void acceptFailed(QueuePlayer player, Boolean continued) {
		if (continued) {
			mmserver.send(player.address, "MATCHACCEPT|failed|continue");
		} else {
			mmserver.send(player.address, "MATCHACCEPT|failed|break");
		}
	}

	public void setupGameserver(NetAddress address, int id, MatchType type, Map map, String password, QueuePlayer[] teamred, QueuePlayer[] teamblue) {
		String teamred_print = teamred[0].getUrTAuth();
		for (int i = 1; i < teamred.length; i++) {
			teamred_print += teamred[i].getUrTAuth();
		}
		String teamblue_print = teamblue[0].getUrTAuth();
		for (int i = 1; i < teamblue.length; i++) {
			teamblue_print += teamblue[i].getUrTAuth();
		}
		
		String print = "SERVERSETUP|matchid:" + id + "|config:" + type.name() + "|map:" + map.name() + "|password:"
						+ password + "|redteam:" + teamred_print + "|teamblue:" +teamblue_print;
		mmserver.send(address, print);
	}

	public void acceptSuccess(Set<QueuePlayer> players, NetAddress address, String password) {
		for (QueuePlayer player : players) {
			mmserver.send(player.address, "MATCHACCEPT|success|server:" + address.address + ":" + address.port + "|password:" + password);
		}
	}
	
	
	
	
	
	
	
	
	

}
