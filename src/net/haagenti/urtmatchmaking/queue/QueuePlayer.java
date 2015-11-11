package net.haagenti.urtmatchmaking.queue;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;
import net.haagenti.urtmatchmaking.match.Match;

public class QueuePlayer {
	
	private static ArrayList<QueuePlayer> allplayers = new ArrayList<QueuePlayer>();

	public NetAddress address;
	private String urtauth;
	public Map map;
	
	public long queuestart;
	private Match match = null;
	
	public int elo = 1000;
	
	//TODO handle prefered position
	
	public QueuePlayer(NetAddress address, String urtauth, Map map, String positionpref) {
		this.address = address;
		this.urtauth = urtauth;
		this.map = map;
		allplayers.add(this);
		// TODO load elo
		queuestart = System.currentTimeMillis();
	}
	
	
	
	
	
	
	
	
	
	
	

	public void clear() {
		match = null;
		
	}

	public void acceptMatch(boolean accept) {
		match.playerAccept(this, accept);
	}

	public boolean isInMatch() {
		return match == null;
	}

	public void joinMatch(Match match) {
		this.match = match;
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
