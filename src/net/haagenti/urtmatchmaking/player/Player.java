package net.haagenti.urtmatchmaking.player;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.connection.NetAddress;
import net.haagenti.urtmatchmaking.match.Map;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.server.ServerCheck;

public class Player {
	
	private static ArrayList<Player> allplayers = new ArrayList<Player>();

	public NetAddress address;
	private String urtauth;
	public Map map;

	public long queuestart = 0;
	private Match match = null;
	
	public int elo = 1000;
	
	
	private long suspensionTime = 0;
	
	//TODO handle prefered position
	
	public Player(NetAddress address, String urtauth) {
		this.address = address;
		this.urtauth = urtauth;
		
		allplayers.add(this);
		if (!ServerCheck.isRunning()) new Thread(new ServerCheck());
		// TODO load elo, load suspension
	}
	
	public void joinQueue(Map map, String positionpref) {
		this.map = map;
		queuestart = System.currentTimeMillis();
	}

	public void backToQueue(long timeInAccept) {
		match = null;
		queuestart += (timeInAccept);
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
	
	public boolean isinQueue() {
		return queuestart == 0;
	}
	
	public boolean isSuspended() {
		return (System.currentTimeMillis() - suspensionTime) < 0;
	}
	
	public static Player find(String urtauth) {
		for (Player player : allplayers) {
			if (player.getUrTAuth().equals(urtauth)) return player;			
		}
		return null;
	}
	
	public static String getStatus(String urtauth, NetAddress address) {
		Player player = Player.find(urtauth);
		if (player == null) {
			player = new Player(address, urtauth);
		} else {
			if (player.isinQueue()) {
				player.queuestart = 0;
			}
		}

		if (player.isInMatch()) return "RESPONSE|HELLO|ingame";
		if (player.isSuspended()) return "RESPONSE|HELLO|suspended|1337millisecs";
		
		return "RESPONSE|HELLO|nothing";
	}

	public static void removePlayer(Player player) {
		allplayers.remove(player);
	}

	public static Player[] getAllPlayers() {
		return (Player[]) allplayers.toArray();
	}

}

