package net.haagenti.urtmatchmaking.match;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.queue.QueuePlayer;

public class Match {
	
	private static ArrayList<Match> allmatches = new ArrayList<Match>();
	
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
