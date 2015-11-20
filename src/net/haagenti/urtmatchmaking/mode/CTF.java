package net.haagenti.urtmatchmaking.mode;

public class CTF extends GameType {

	@Override
	public boolean halfTime() {
		return true;
	}

	@Override
	public String name() {
		return "ctf";
	}

}
