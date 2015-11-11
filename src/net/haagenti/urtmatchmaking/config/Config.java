package net.haagenti.urtmatchmaking.config;

import java.util.Arrays;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.queue.QueueManager;
import net.haagenti.urtmatchmaking.queue.QueuePlayer;
import net.haagenti.urtmatchmaking.queue.Region;

public class Config {
	
	private ConfigType type;
	private int port;
	
	private QueueManager queue;
	
	public Config(ConfigType type, int port) {
		this.type = type;
		this.port = port;
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
			// check if gametype is the right one
			if (data[3].split(":")[1].equals(type.name())) {
				String urtauth = data[1].split(":")[1];
				Region region = Region.valueOf(data[2].split(":")[1]);
				Map map = Map.valueOf(data[3].split(":")[1]);
				String position = data[4].split(":")[1];
				
				return queue.addPlayer(new QueuePlayer(address, urtauth, region, map, position));
			}
		}
		return "RESPONSE|QUEUE|error";	
	}
	
	private String processMatchAccept(String[] data, NetAddress address) {
		if (data.length == 3) {
			queue.matchAccept(QueuePlayer.find(data[1].split(":")[1]), Boolean.valueOf(data[2].split(":")[1]));
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
	
	public final int getMMport() {
		return port;
	}
	

}
