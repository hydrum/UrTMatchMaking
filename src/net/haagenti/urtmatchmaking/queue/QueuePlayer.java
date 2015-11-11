package net.haagenti.urtmatchmaking.queue;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;

public class QueuePlayer {
	
	private static ArrayList<QueuePlayer> allplayers = new ArrayList<QueuePlayer>();

	private NetAddress address;
	private String urtauth;
	private Region region;
	private Map map;
	
	//TODO handle prefered position
	
	public QueuePlayer(NetAddress address, String urtauth, Region region, Map map, String positionpref) {
		this.address = address;
		this.urtauth = urtauth;
		this.region = region;
		this.map = map;
		allplayers.add(this);
	}
	
	public String getUrTAuth() {
		return urtauth;
	}
	
	
	public static QueuePlayer find(String urtauth) {
		for (QueuePlayer player : allplayers) {
			if (player.getUrTAuth().equals(urtauth)) return player;			
		}
		return null;
	}

}
