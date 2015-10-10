package com.anvil.fredo;

public class Block {

	static final int DOWN = 0;
	static final int UP = 1;
	static final int NORTH = 2;
	static final int SOUTH = 3;
	static final int WEST = 4;
	static final int EAST = 5;
	
	
	static int lastX;
	static int lastY;
	static int lastZ;
	
	int x;
	int y;
	int z;
	
	public Block(String cbCommand) {
		
	}
	
	private int moduloBlock(int x, int y) {

		return Main.modulo(x, y);
	}
	
}
