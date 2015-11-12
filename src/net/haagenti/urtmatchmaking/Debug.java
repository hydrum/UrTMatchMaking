package net.haagenti.urtmatchmaking;

public class Debug {
	
	private static boolean debug = false;
	
	public enum TAG {
		QUEUEMANAGER,
		PROTOCOL,
		MMSERVER,
		MAIN,
		DATABASE,
		PLAYER,
		MATCH,
		SERVER
	}

	public static void Log(TAG tag, String string) {
		System.out.println(Thread.currentThread().getName() + " - " + tag.name() + ": " + string);
	}
	
	public static void Error(String debugline) {
		System.out.println("ERROR: " + debugline);
	}
	
	public static void enableDebug() {
		debug = true;
	}
	
	public static void disableDebug() {
		debug = false;
	}
	
	public static boolean isDebugging() {
		return debug;
	}
	
}
