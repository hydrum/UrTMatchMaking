package net.haagenti.urtmatchmaking.server;

public class ServerCheck implements Runnable {

	private static boolean stop = false;
	
	@Override
	public void run() {
		while (!ServerCheck.stop) {
			try {
				for (Server server : ServerPool.getAllServers()) {
					if (ServerCheck.stop) return;
					
					if (!ServerCheck.isServerAlive(server)) {
						ServerPool.removeServer(server);
					}
					Thread.sleep(500);
				}
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isServerAlive(Server server) {
		String response = ServerPool.sendRcon(server, "mm_enabled");
		return !(response == null || response.equalsIgnoreCase("0"));  //TODO exact response
	}
	
	public static boolean isRunning() {
		return !stop;
	}
	
	public static void stop() {
		stop = true;
	}

}
