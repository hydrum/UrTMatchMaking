package net.haagenti.urtmatchmaking.match;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.connection.Database;
import net.haagenti.urtmatchmaking.mode.GameType;
import net.haagenti.urtmatchmaking.player.Player;
import net.haagenti.urtmatchmaking.queue.QueueManager;
import net.haagenti.urtmatchmaking.server.Server;

public class Match {
	
	private static ArrayList<Match> allmatches = new ArrayList<Match>();


	private long starttime;
	private Server server;
	public HashMap<Player, Boolean> players;
	
	private GameType type;
	
	public Player[] teamred;
	public Player[] teamblue;	
	public int elored;
	public int eloblue;
	public Map map = null;
	public int scorered = 0;
	public int scoreblue = 0;
	

	public int id = 0;
	public QueueManager queue;
	
	private boolean updating;
	private long waitAccept = 30 * 1000; // waiting 30sec to accept
	private long acceptStartTime = 0;
	
	public Match(QueueManager queue, Server server, ArrayList<Player> players) {
		Debug.Log(TAG.MATCH, "Setting up Match");
		this.server = server;
		this.queue = queue;
		this.type = queue.gametype;
		
		starttime = System.currentTimeMillis();

		Debug.Log(TAG.MATCH, "Requesting ID");
		id = Database.getMatchID();
		
		for (Player player : players) {
			this.players.put(player, false);
		}
		
		requestAccept();
	}
	
	public void update() {
		if ((System.currentTimeMillis() - acceptStartTime >= waitAccept || getAcceptCount() == 10) && updating ) {
			checkAccept();
		}
	}
	
	
	public void requestAccept() {
		Debug.Log(TAG.MATCH, "Requesting Accept from players");
		server.take(this);
		for (Player player : players.keySet()) {
			player.joinMatch(this);
		}
		queue.protocol.requestAccept(id, players.keySet());
		acceptStartTime = System.currentTimeMillis();
		
		updating = true;
	}


	public void playerAccept(Player player, boolean accept) {
		Debug.Log(TAG.MATCH, "PlayerAccept respond: " + player.getUrTAuth() + " with " + accept);
		players.put(player, accept);
	}
	
	public void checkAccept() {
		int acceptcounter = getAcceptCount();
		updating = false;

		Debug.Log(TAG.MATCH, "Accept ended, total accepts: " + acceptcounter);
		if (acceptcounter < 10) {
			returnToQueue();
			server.free();
		} else {
			startMatch();
		}
	}
	
	private int getAcceptCount() {
		int acceptcounter = 0;
		for (Player player : players.keySet()) {
			if (players.get(player)) acceptcounter++;
		}
		return acceptcounter;
	}
	
	public void returnToQueue() {
		Debug.Log(TAG.MATCH, "Returning accepted players to queue due to overall acceptance failure");
		for (Player player : players.keySet()) {
			player.backToQueue(System.currentTimeMillis() - acceptStartTime);
			queue.protocol.acceptFailed(id, player, players.get(player));
			if (players.get(player)) {
				queue.addPlayer(server.getRegion(), player);
			}
		}
	}
	
	
	public void startMatch() {

		Debug.Log(TAG.MATCH, "Starting Match");
		
		starttime = System.currentTimeMillis();
		
		// PASSWORD
		Random rand = new Random();
		int password = rand.nextInt((999999-100000) + 1) + 100000;
		Debug.Log(TAG.MATCH, "Password: " + String.valueOf(password));
		server.password = String.valueOf(password);
		
		// MAPCHOICE
		HashMap<Map, Integer> maplist = new HashMap<Map, Integer>();

		ArrayList<Player> sortplayers = new ArrayList<Player>();
		sortplayers.add((Player) players.keySet().toArray()[0]);
		for (Player player : players.keySet()) {
			for (Player sortplayer : sortplayers) {
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
		Debug.Log(TAG.MATCH, "Map: " + this.map.name());
		
		teamred = new Player[5];
		teamred[0] = sortplayers.get(0);
		teamred[1] = sortplayers.get(2);
		teamred[3] = sortplayers.get(7);
		teamred[4] = sortplayers.get(9);
		elored = teamred[0].elo + teamred[1].elo + teamred[3].elo + teamred[4].elo;		

		teamblue = new Player[5];
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

		Debug.Log(TAG.MATCH, "Team red:" + elored + " " + teamred.toString());
		Debug.Log(TAG.MATCH, "Team blue:" + eloblue + " " + teamblue.toString());
		
		setupGameserver();
		notifyPlayers();
	}
	
	public void setupGameserver() {
		Debug.Log(TAG.MATCH, "Setting up Gameserver");
		queue.protocol.setupGameserver(server.getNetAddress(), id, type, map, server.password, teamred, teamblue);
	}
	
	public void notifyPlayers() {
		Debug.Log(TAG.MATCH, "Notifying Players");
		queue.protocol.acceptSuccess(id, players.keySet(), server.getPublicAddress(), server.password);
	}

	public String getGameDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return (sdf.format(new Date(starttime)));
	}
	
	
	
	
	
	
	public String playerLeft(Player player) {
		// quit match
		return "RESPONSE|PLAYERLEFT|quit";
	}

	public static Match find(String matchid) {
		for (Match match : allmatches) {
			if (match.id == Integer.valueOf(matchid)) return match;
		}
		return null;
	}

	public String result(String[] data) {
		return null;
	}

	public static void updateAll() {
		for (Match match : allmatches) {
			match.update();
		}
	}

	public long getStartTime() {
		return starttime;
	}

}
