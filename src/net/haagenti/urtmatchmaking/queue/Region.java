package net.haagenti.urtmatchmaking.queue;

public class Region {
	
	public static final Region EU = new Region("eu");
	
	public static final Region[] list = {Region.EU};
	
	private String name;
	private boolean enabled;
	
	public Region(String name) {
		this.name = name;
		this.enabled = true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disabled() {
		enabled = false;
	}
	
	public String name() {
		return name;
	}

	public static Region hasRegion(String name) {
		for (Region region : list) {
			if (region.name().equals(name)) return region;
		}
		return null;
	}
}
