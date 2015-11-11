package net.haagenti.urtmatchmaking.queue;

import java.util.ArrayList;
import java.util.HashMap;

import net.haagenti.urtmatchmaking.config.Protocol;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.match.MatchType;
import net.haagenti.urtmatchmaking.match.Server;

public class QueueManager implements Runnable {

	public MatchType matchtype;
	public Protocol protocol;

	private HashMap<Region, ArrayList<QueuePlayer>> queuelist;
	private ArrayList<Match> allMatches = new ArrayList<Match>();
	private ArrayList<Match> newMatches = new ArrayList<Match>();
	private ArrayList<Server> serverlist = new ArrayList<Server>();
	
	
	private int elowidth = 10; // elo width to increase each second
	

	public QueueManager(Protocol protocol, MatchType type) {
		this.matchtype = type;
		this.protocol = protocol;
		
		for (Region region : Region.values()) {
			queuelist.put(region, new ArrayList<QueuePlayer>());
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				
				for (Match match : allMatches) {
					match.update();
				}
				
				
				for (Region region : queuelist.keySet()) {
					
					ArrayList<QueuePlayer> copyList = new ArrayList<QueuePlayer>(queuelist.get(region));
					
					for (QueuePlayer player : queuelist.get(region)) {
						if (player.isInMatch()) continue;
						ArrayList<QueuePlayer> suitablePlayers = new ArrayList<QueuePlayer>();
						
						for (QueuePlayer otherPlayer : copyList) {
							if (!otherPlayer.isInMatch() && isInEloRange(player, otherPlayer)) {
								suitablePlayers.add(otherPlayer);
								if (suitablePlayers.size() == 10) {
									startMatch(region, suitablePlayers);
									break;
								}
							}
						}
					}
				}

				for (Match match : newMatches) {
					for (Region region : queuelist.keySet()) {
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

	private void startMatch(Region region, ArrayList<QueuePlayer> players) {
		
		// get available server
		Server server = null;
		for (Server server1 : serverlist) {
			if (server1.region == region && !server1.isTaken()) {
				server = server1;
				break;
			}
		}
		
		if (server != null) {
			// server has been found
			Match match = new Match(this, server, players);
			allMatches.add(match);
			newMatches.add(match);
			
		} else {
			// TODO WHAT TO DO WHEN NO SERVER AVAILABLE
		}
		
	}

	private boolean isInEloRange(QueuePlayer player, QueuePlayer otherPlayer) {
		long time = System.currentTimeMillis();
		int eloborder1top = player.elo + ((int)(time - player.queuestart)) * elowidth;
		int eloborder1bot = player.elo - ((int)(time - player.queuestart)) * elowidth;
		
		int eloborder2top = otherPlayer.elo + ((int)(time - otherPlayer.queuestart)) * elowidth;
		int eloborder2bot = otherPlayer.elo - ((int)(time - otherPlayer.queuestart)) * elowidth;
		
		if ((eloborder1top <= eloborder2top) && (eloborder1bot <= eloborder2bot) || ((eloborder1top >= eloborder2top) && (eloborder1bot >= eloborder2bot))) {
			return true;
		} else {
			return false;
		}
	}

	public String addPlayer(Region region, QueuePlayer player) {
		
		return "RESPONSE|QUEUE|success";		
	}

	public void leavePlayer(QueuePlayer player) {
		
	}

	public String checkPlayerStatus(QueuePlayer player) {
		if (player == null) return "RESPONSE|HELLO|notinqueue";
		// player in queue/ingame?
		return null;
	}

}
