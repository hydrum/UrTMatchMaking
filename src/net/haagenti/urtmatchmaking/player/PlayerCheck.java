package net.haagenti.urtmatchmaking.player;

import java.util.HashMap;

import net.haagenti.urtmatchmaking.connection.MMserver;
import net.haagenti.urtmatchmaking.connection.Protocol;

public class PlayerCheck implements Runnable {
	
	private static HashMap<Player, Boolean> playercheck = new HashMap<Player, Boolean>();

	private static boolean stop = false;
	
	@Override
	public void run() {
		while (!PlayerCheck.stop) {
			try {
				for (Player player : playercheck.keySet()) {
					if (PlayerCheck.stop) return;
					if (!playercheck.get(player)) {
						Player.removePlayer(player);
					}
				}
				playercheck.clear();
				for (Player player : Player.getAllPlayers()) {
					
					if (PlayerCheck.stop) return;
					if (player.isInMatch() || player.isinQueue()) continue;
					
					MMserver.send(player.address, "PLAYERALIVE");
					Thread.sleep(500);
				}
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isRunning() {
		return !stop;
	}
	
	public static void stop() {
		stop = true;
	}

	public static void setPlayerStatus(Player player) {
		if (player == null) return;
		if (playercheck.containsKey(player)) {
			playercheck.put(player, true);
		}
	}

}
