package net.haagenti.urtmatchmaking.mode;

import java.util.ArrayList;

import net.haagenti.urtmatchmaking.match.Map;

public abstract class GameType {
	
	public static GameType CTF = new CTF();
	
	public static GameType[] list = {GameType.CTF};
	
	protected ArrayList<Map> maplist = new ArrayList<Map>();

	public void addMap(Map map) {
		maplist.add(map);
	}

	public void removeMap(Map map) {
		maplist.remove(map);
	}

	public Map[] getMaplist() {
		return (Map[]) maplist.toArray();
	}
	
	public abstract boolean halfTime();
	public abstract String name();

	public boolean hasMap(Map map) {
		return maplist.contains(map);
	}

}
