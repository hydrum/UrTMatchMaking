package net.haagenti.urtmatchmaking.match;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import net.haagenti.urtmatchmaking.connection.Database;
import net.haagenti.urtmatchmaking.queue.QueueManager;
import net.haagenti.urtmatchmaking.queue.QueuePlayer;

public class Match {
	
	private static ArrayList<Match> allmatches = new ArrayList<Match>();


	private long starttime;
	private Server server;
	public HashMap<QueuePlayer, Boolean> players;
	
	private MatchType type;
	
	public QueuePlayer[] teamred;
	public QueuePlayer[] teamblue;	
	public int elored;
	public int eloblue;
	public Map map = null;
	public int scorered = 0;
	public int scoreblue = 0;
	

	public int id = 0;
	public QueueManager queue;

	private long waitAccept = 30 * 1000; // waiting 30sec to accept
	private long acceptStartTime = 0;
	
	public Match(QueueManager queue, Server server, ArrayList<QueuePlayer> players) {
		this.server = server;
		this.queue = queue;
		this.type = queue.matchtype;
		
		starttime = System.currentTimeMillis();
		
		for (QueuePlayer player : players) {
			this.players.put(player, false);
		}
		
		requestAccept();
	}
	
	public void update() {
		if (System.currentTimeMillis() - acceptStartTime >= waitAccept) {
			checkAccept();
		}
	}
	
	
	public void requestAccept() {
		server.take(this);
		for (QueuePlayer player : players.keySet()) {
			player.joinMatch(this);
		}
		queue.protocol.requestAccept(players.keySet());
		acceptStartTime = System.currentTimeMillis();
	}


	public void playerAccept(QueuePlayer player, boolean accept) {
		players.put(player, accept);
	}
	
	public void checkAccept() {
		int acceptcounter = 0;
		for (QueuePlayer player : players.keySet()) {
			if (players.get(player)) acceptcounter++;
		}
		
		if (acceptcounter < 10) {
			returnToQueue();
			server.free();
		} else {
			startMatch();
		}
	}
	
	public void returnToQueue() {
		for (QueuePlayer player : players.keySet()) {
			player.clear();
			queue.protocol.acceptFailed(player, players.get(player));
			if (players.get(player)) {
				queue.addPlayer(server.region, player);
			}
		}
	}
	
	
	public void startMatch() {
		
		starttime = System.currentTimeMillis();
		
		// PASSWORD
		Random rand = new Random();
		int password = rand.nextInt((999999-100000) + 1) + 100000;
		System.out.println("Password: " + String.valueOf(password));
		server.password = String.valueOf(password);
		
		// MAPCHOICE
		HashMap<Map, Integer> maplist = new HashMap<Map, Integer>();

		ArrayList<QueuePlayer> sortplayers = new ArrayList<QueuePlayer>();
		sortplayers.add((QueuePlayer) players.keySet().toArray()[0]);
		for (QueuePlayer player : players.keySet()) {
			for (QueuePlayer sortplayer : sortplayers) {
				if (player.equals(sortplayer)) continue;
				else if (player.elo <= sortplayer.elo) {
					sortplayers.add(sortplayers.indexOf(sortplayer), player); 
					break;
				}				
			}
			
			// MAPCHOICE
			if (!maplist.containsKey(player.map)) {
				maplist.put(player.map, 1);
			} else {
				maplist.put(player.map, maplist.get(player.map) + 1);
			}
		}
		for (Map map1 : maplist.keySet()) {
			if (this.map == null) this.map = map1;
			else if (maplist.get(map1) > maplist.get(this.map)) {
				this.map = map1;
			}
		}
		System.out.println("Map: " + this.map.name());
		
		teamred = new QueuePlayer[5];
		teamred[0] = sortplayers.get(0);
		teamred[1] = sortplayers.get(2);
		teamred[3] = sortplayers.get(7);
		teamred[4] = sortplayers.get(9);
		elored = teamred[0].elo + teamred[1].elo + teamred[3].elo + teamred[4].elo;		

		teamblue = new QueuePlayer[5];
		teamblue[0] = sortplayers.get(1);
		teamblue[1] = sortplayers.get(3);
		teamblue[3] = sortplayers.get(6);
		teamblue[4] = sortplayers.get(8);		
		eloblue = teamblue[0].elo + teamblue[1].elo + teamblue[3].elo + teamblue[4].elo;
		
		if (eloblue <= elored) {
			teamblue[2] = sortplayers.get(4);
			teamred[2] = sortplayers.get(5);
		} else {
			teamred[2] = sortplayers.get(4);
			teamblue[2] = sortplayers.get(5);
		}
		eloblue += teamblue[2].elo;
		elored += teamred[2].elo;
		
		eloblue /= 5;
		elored /= 5;

		System.out.println("Team red:" + elored + " " + teamred.toString());
		System.out.println("Team blue:" + eloblue + " " + teamblue.toString());
		
		//id = Database.createMatch(this);
		
		setupGameserver();
		notifyPlayers();
	}
	
	public void setupGameserver() {
		queue.protocol.setupGameserver(server.getNetAddress(), id, type, map, server.password, teamred, teamblue);
	}
	
	public void notifyPlayers() {
		queue.protocol.acceptSuccess(players.keySet(), server.getPublicAddress(), server.password);
	}

	public String getGameDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return (sdf.format(new Date(starttime)));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String playerLeft(QueuePlayer player) {
		// quit match
		return "RESPONSE|PLAYERLEFT|QUIT";
	}

	public static Match find(String matchid) {
		return null;
	}

	public String result(String[] data) {
		return null;
	}

}
