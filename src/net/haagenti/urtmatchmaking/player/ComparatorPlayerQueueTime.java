package net.haagenti.urtmatchmaking.player;

import java.util.Comparator;

public class ComparatorPlayerQueueTime implements Comparator<Player> {

	@Override
	public int compare(Player player1, Player player2) {
		if (player1.queuestart < player2.queuestart)
			return -1;
		else if (player1.queuestart > player2.queuestart)
			return 1;
		else
			return 0;
	}

}
