package net.haagenti.urtmatchmaking.queue;

import java.util.ArrayList;
import java.util.HashMap;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.connection.Protocol;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.mode.GameType;
import net.haagenti.urtmatchmaking.player.ComparatorPlayerQueueTime;
import net.haagenti.urtmatchmaking.player.Player;
import net.haagenti.urtmatchmaking.server.Server;
import net.haagenti.urtmatchmaking.server.ServerPool;

public class QueueManager implements Runnable {

	public GameType gametype;
	public Protocol protocol;

	private HashMap<Region, ArrayList<Player>> queuelist = new HashMap<Region, ArrayList<Player>>();
	private ArrayList<Match> newMatches = new ArrayList<Match>();
	
	private boolean stop = false;
	
	private int elowidth = 10; // elo width to increase each second
	

	public QueueManager(Protocol protocol, GameType type) {
		Debug.Log(TAG.QUEUEMANAGER, "Setting up QueueManager for " + type.name());
		this.gametype = type;
		this.protocol = protocol;

		Debug.Log(TAG.QUEUEMANAGER, "Setting up Regions");
		for (Region region : Region.list) {
			queuelist.put(region, new ArrayList<Player>());
		}
	}
	
	@Override
	public void run() {
		while (!stop) {
			try {
				
				// updating matches (timed events such as Accepting)
				Match.updateAll();
				
				// each region separately
				for (Region region : queuelist.keySet()) {
					if (stop) return;
					queuelist.get(region).sort(new ComparatorPlayerQueueTime());
					//Debug.Log(TAG.QUEUEMANAGER, "Checking Players for region " + region.name());

					// copying list to be able to AxA
					// IDEA: check for each player if there is a player that fits to him
					// if they found 10, create a match and go ahead, maybe there are more matches going
					ArrayList<Player> playerToRemove = new ArrayList<Player>();
					ArrayList<Player> copyList = new ArrayList<Player>(queuelist.get(region));
					for (Player player : queuelist.get(region)) {
						if (stop) return;
						if (player.queuestart == 0) {
							playerToRemove.add(player);
							continue;
						}
						if (player.isInMatch()) continue;
						ArrayList<Player> suitablePlayers = new ArrayList<Player>();

						for (Player otherPlayer : copyList) {
							if (stop) return;
							if (otherPlayer.queuestart == 0) continue;
							if (!otherPlayer.isInMatch() && isInEloRange(player, otherPlayer)) {
								suitablePlayers.add(otherPlayer);
								if (suitablePlayers.size() == 10) {
									startMatch(region, suitablePlayers);
									break;
								}
							}
						}
					}
					// remove those players who found a new match
					for (Match match : newMatches) {
						if (stop) return;
						queuelist.get(region).removeAll(match.players.keySet());
					}
					newMatches.clear();
					
					// remove players who aren't in the queue anymore (due to time out)
					queuelist.get(region).removeAll(playerToRemove);
					playerToRemove.clear();
				}

				for (Match match : newMatches) {
					if (stop) return;
					for (Region region : queuelist.keySet()) {
						if (stop) return;
						queuelist.get(region).removeAll(match.players.keySet());
					}
				}
				newMatches.clear();

				// remove those players who found a new match
				for (Match match : newMatches) {
					if (stop) return;
					for (Region region : queuelist.keySet()) {
						if (stop) return;
						queuelist.get(region).removeAll(match.players.keySet());
					}
				}
				newMatches.clear();
				
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void startMatch(Region region, ArrayList<Player> players) {
		Debug.Log(TAG.QUEUEMANAGER, "Starting a match - region: " + region.name() + " - players: " + players.toString());
		
		// get available server
		Server server = ServerPool.getAvailableServer(region);
		
		if (server != null) {
			// server has been found
			Debug.Log(TAG.QUEUEMANAGER, "Server allocated: " + server.getPublicAddress().toString());
			Match match = new Match(this, server, players);
			newMatches.add(match);
			
		} else {
			// TODO WHAT TO DO WHEN NO SERVER AVAILABLE
			Debug.Log(TAG.QUEUEMANAGER, "No available server - do nothing.");
		}
		
	}

	private boolean isInEloRange(Player player, Player otherPlayer) {
		long time = System.currentTimeMillis();
		int eloborder1top = player.elo + ((int)(time - player.queuestart)) * elowidth;
		int eloborder1bot = player.elo - ((int)(time - player.queuestart)) * elowidth;
		
		int eloborder2top = otherPlayer.elo + ((int)(time - otherPlayer.queuestart)) * elowidth;
		int eloborder2bot = otherPlayer.elo - ((int)(time - otherPlayer.queuestart)) * elowidth;
		
		if ((eloborder1top <= eloborder2top) && (eloborder1bot <= eloborder2bot) || ((eloborder1top >= eloborder2top) && (eloborder1bot >= eloborder2bot))) {
			Debug.Log(TAG.QUEUEMANAGER, "Matching player found: " + player.getUrTAuth() + " (elo: " + player.elo + ") and " + otherPlayer.getUrTAuth() + " (elo: " + otherPlayer.elo + ")");
			return true;
		} else {
			return false;
		}
	}

	public String addPlayer(Region region, Player player) {
		queuelist.get(region).add(player);
		Debug.Log(TAG.QUEUEMANAGER, "Player added to region " + region.name() + ": " + player.getUrTAuth());
		return "RESPONSE|QUEUE|success";		
	}

	public void leavePlayer(Player player) {
		// check if player is in queue
		for (Region region : queuelist.keySet()) {
			if (stop) return;
			queuelist.get(region).remove(player);
		}
		player.queuestart = 0;
	}

	public void stop() {
		stop = true;
		
	}
}
