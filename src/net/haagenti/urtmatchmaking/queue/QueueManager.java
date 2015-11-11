package net.haagenti.urtmatchmaking.queue;

public class QueueManager {
	
	public String addPlayer(QueuePlayer player) {
		
		return "RESPONSE|QUEUE|success";		
	}

	public void leavePlayer(QueuePlayer player) {
		
	}

	public void matchAccept(QueuePlayer player, Boolean accept) {
		// TODO Auto-generated method stub
		
	}

	public String checkPlayerStatus(QueuePlayer player) {
		if (player == null) return "RESPONSE|HELLO|notinqueue";
		// player in queue/ingame?
		return null;
	}

}
