package com.anvil.fredo;

import java.io.IOException;

@SuppressWarnings("unused")
public class Block {

	static final int IMPULSE = 1;
	static final int CHAIN = 2;
	static final int REPEAT = 3;
	
	static final int DOWN = 0;	//Likely won't be used.
	static final int UP = 1;
	static final int NORTH = 2;
	static final int SOUTH = 3;
	static final int WEST = 4;
	static final int EAST = 5;
	
	static final int[] pattern = new int[3];
	static final int X_PAT = 0;
	static final int Y_PAT = 1;
	static final int Z_PAT = 2;
	
	static int previousX;
	static int previousY;
	static int previousZ;
	
	static int firstX;
	static int firstY;
	static int firstZ;

	static int currentBlock;
	static int set = -1;	//Gets ++ to 0 when main finishes executing
	int direction;
	int x;
	int y;
	int z;
	String command;
	String typeOfCB;
	int funcXInt;
	int funcYInt;
	int funcZInt;
	
	public Block(String cbCommand, int type) {	//Standard constructor
		
		switch (type) {
		case IMPULSE:	typeOfCB = " command_block "; break;
		case CHAIN:		typeOfCB = " chain_command_block "; break;
		case REPEAT:	typeOfCB = " repeating_command_block "; set++; currentBlock = 0; break;	//Set increments on REPEAT b/c the only instance we add a repeating block is when we have a new file. Current Block is also reset since it pertains to each individual file.
		default: 																				//if we had kept the idea that the files don't add new repeat CBs then it would have just built off the previous one, and then current block would not have been reset and set wouldn't exist
		}
		
		//Below, func<value> returns the relative position of that coord to the first block. To get it's absolute pos
		//we add it to that first block. We also assign the value to a variable when we call funcX funcY or funcZ. This is used in determining direction.
		x = (set*pattern[X_PAT]) + firstX + (funcXInt = funcX(currentBlock, pattern));	//remember, we have to add the relative values (which is what func<value> gives) to the absolutes given by main. 
		y = firstY + (funcYInt = funcY(currentBlock, pattern));							//b/c X is our decided metric for expansion, we start new lines one patX length away from the previous which is what set*patX gives us
		z = firstZ + (funcZInt = funcZ(currentBlock, pattern));
		direction = direction(funcXInt, funcYInt, funcZInt);	//The three parameters represent relative positions to the first block.
		
		command = "setblock " + x + " " + y + " " + z + typeOfCB + direction + " replace {TrackOutput:0b,auto:1b,Command:" + cbCommand + "}";
		if (type==REPEAT) {command = command.replaceFirst(",auto:1b", ""); Server.sendCommand("setblock " + x + " " + (y-1) + " " + z + " minecraft:redstone_block");}
		Server.sendCommand(command);
		currentBlock++;
	}

	public Block(int[] mainCoords, int[] pattern, String cbCommand) {	//main. Specifically main.
		
		set = -1;
		
		firstX = mainCoords[0]; firstY = mainCoords[1]; firstZ = mainCoords[2];
		setFinalPattern(pattern);
		currentBlock = 0;
		new Block(cbCommand,REPEAT);
		
	}	//This constructor is for when the main file is read. there will be another constructor for all other files.
	
	
	static public int direction(int x, int y, int z) {
		//we don't have to worry about negative numbers here :)
		//What we want here are the relative values of X Y and Z
		
		//System.out.println(currentBlock + ": " + x + " " + y + " " + z + " " + (x == 0 && (y%2 ==0) && z == (pattern[Z_PAT]-1)) + " " + (x == 0 && (y%2 ==1) && z == 0));
		/*if ((x == 0 && (y%2 ==0) && z == (pattern[Z_PAT]-1)) || (x == 0 && (y%2 ==1) && z == 0)) {
			return UP;
		}*/
		//Yeah no. this all needs to be based on the pattern :<
		
//		int zMod2 = z % 2;
		
		switch (y % 2) {
		case 0:
			if ((pattern[Z_PAT]%2) == 0 && x == 0 && z == (pattern[Z_PAT]-1)) return UP;
			if ((pattern[Z_PAT]%2) == 1 && x == (pattern[X_PAT]-1) && z == (pattern[Z_PAT]-1)) return UP;
			
			if (x == (pattern[X_PAT]-1) && z%2 == 0) return SOUTH;
			if (x == 0 && z%2 == 1) return SOUTH;	//no need to include z%2==1. If x==0 and it gets past the previous one then we already know z%2==1.
			if (x == (pattern[X_PAT]-1)/* && z%2 == 1*/) return WEST;
			if (x == 0 && z%2 == 0) return EAST;
			if (/*x < pattern[X_PAT] && */z%2 == 0) return EAST;
			/*if (x < pattern[X_PAT]/* && z%2 == 0)*/ return WEST;
			
		case 1:
			if (x == 0 && z == 0) return UP;
			
			if (x == (pattern[X_PAT]-1) && z%2 == 1) return NORTH;
			if (x == 0 && z%2 == 0) return NORTH;
			if (x == (pattern[X_PAT]-1) && z%2 == 0) return WEST;
			if (x == 0/* && z%2 == 1*/) return EAST;
			if (/*x < pattern[X_PAT] && */z%2 == 0) return WEST;
			/*if (x < pattern[X_PAT]/* && z%2 == 1)*/ return EAST;
		default: return DOWN;
		}

		
	}	
		
		
	
	//Functions for determining the coordinates of the Nth block relative to the first block
	static public int funcX(int Q, int[] pattern) {
		//X is a crazy beast. It changes the most of the three coords. The values of X for a set of blocks when listed out
		//creates an incremental pattern that starts decreasing after about half.
		//EX: if the pattern is restricted to 4 blocks, the X values go 0,1,2,3,3,2,1,0 repeat. Whenever it switches counting
		//directions is when Z increments (or decreases)
	
		if (pattern[X_PAT] != 1) {
			Q = (Q % (2*pattern[X_PAT]));
		}	else	{
			Q = (Q % pattern[X_PAT]);
		}
		//So since the X falls into unique patterns of twice the pattern restriction, we double the pattern restriction
		//to determine where in this sequence it will fall.
		
		//this part determines where in the sequence it is. If it's in the first half, 0 - (x-1), it's returned
		//If in the second half, x - (2x-1), we subtract it from (2x-1) to get the "double back" effect, also witnessed
		//a bit in funcZ
		if (Q >= 0 && Q <= (pattern[X_PAT]-1)) {
			return Q;
		}
		else {
			return (((2*pattern[X_PAT])-1)-Q);
		}
		
	}
	static public int funcY(int Q, int[] pattern) {
		//Y is by far the simplest, no double backing, just a direct function of the area of xPat and zPat.
		Q = (Q/(pattern[X_PAT]*pattern[Z_PAT]));
		return Q;
	}
	static public int funcZ(int Q, int[] pattern) {
		int yMod2 = funcY(Q, pattern) % 2;
		
		if (yMod2 == 0) {
			//The position of Z is a function of pattern X. Every patX blocks, Z moves up or down.
			Q = ((Q/(pattern[X_PAT])) % pattern[Z_PAT]);
		}	
		else 	{
			//We invert the value of Z after a new Y level (when yMod2 = 1) so it doubles back on itself
			Q = invertedModulo((Q/(pattern[X_PAT])), pattern[Z_PAT]);
		}
		
		return Q;
	}
	
	
	private int moduloBlock(int x, int y) {

		return Main.modulo(x, y);
	}
	
	//Inverted modulus operation. For use when determining the position of Z, which needs to be inverted.
	static public int invertedModulo(int x, int y) {
		
		return ((y-1) - (x % y));
	}

	static private void setFinalPattern(int[] pat) {
		pattern[X_PAT] = pat[X_PAT];
		pattern[Y_PAT] = pat[Y_PAT];
		pattern[Z_PAT] = pat[Z_PAT];
	}
	
	
}
