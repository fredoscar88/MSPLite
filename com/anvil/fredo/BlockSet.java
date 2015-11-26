package com.anvil.fredo;

import java.io.IOException;
import java.util.List;

public class BlockSet {

	static final int DOWN = 0;	//Likely won't be used.
	static final int UP = 1;
	static final int NORTH = 2;
	static final int SOUTH = 3;
	static final int WEST = 4;
	static final int EAST = 5;
	
	int PosX, PosY, PosZ, PatX, PatY, PatZ, DIR;
	
	FileUpdater BlockSetFU = new FileUpdater();
	Console BlockSetCons = new Console("Block set console");
	List<String> parsedLine;
	
	public BlockSet(int PosX, int PosY, int PosZ, int PatX, int PatY, int PatZ, int DIR) {
		
	}
	
	public BlockSet() throws IOException {
		
		parsedLine = BlockSetCons.PConsoleParse(BlockSetFU.getSetting(Main.mspProps, "First_Block"));
		PosX = Integer.parseInt(parsedLine.get(0));
		PosY = Integer.parseInt(parsedLine.get(1));
		PosZ = Integer.parseInt(parsedLine.get(2));
		
		parsedLine = BlockSetCons.PConsoleParse(BlockSetFU.getSetting(Main.mspProps, "Pattern"));
		PatX = Integer.parseInt(parsedLine.get(0));
		PatY = Integer.parseInt(parsedLine.get(1));
		PatZ = Integer.parseInt(parsedLine.get(2));
		
		DIR = determineDirFromStr(BlockSetFU.getSetting(Main.mspProps, "Direction"));
		
		
	}
	
	//When a new blockset is called we are going to send this information over to block. !!!. and the two below methods will send it.
	void definePos(List<String> coords) {
		PosX = Main.pInt(coords.get(0));
		PosY = Main.pInt(coords.get(1));
		PosZ = Main.pInt(coords.get(2));
	}
	void definePat(List<String> pattern) {
		PatX = Main.pInt(pattern.get(0));
		PatY = Main.pInt(pattern.get(1));
		PatZ = Main.pInt(pattern.get(2));
		
	}
	void defineDir() {
		//Unused for now since I don't support directions other than east.
	}

	void updateBlock() {
		Main.dbOutput("Updated blocks 'first' blocks and pattern");
		Block.setDefaultParams(new int[] {PosX, PosY, PosZ}, new int[] {PatX, PatY, PatZ}/*, Dir :c*/); 
	}
	
	
	int determineDirFromStr(String dir) {
		
		dir = dir.toUpperCase();
		
		switch (dir) {
		case "NORTH": return 2;
		case "SOUTH": return 3;
		case "WEST": return 4;
		case "EAST": return 5;
			default: return 5;
		}
	}
	
}
