package com.anvil.fredo;

import java.io.IOException;

public class Chunk {

	static private int AmtOfChunk;	//Not sure we want to store this information here. In fact, NO!
	
	private final int xCoord;
	private final int zCoord;
	private final int yCoord;
	
	//String fillString;
	
	//Constructs a new chunk, location determined by the only block in the chunk that is divisible by 16 on both axis
	public Chunk(int x, int y, int z) {
		xCoord = x/16;
		zCoord = z/16;
		yCoord = y;
	}
	
	public void fillChunk() throws IOException {
		
		String result;
		StringBuilder str = new StringBuilder();
		
		str.append("/fill ");
		str.append(xCoord*16);
		str.append(" ");
		str.append(yCoord);
		str.append(" ");
		str.append(zCoord*16);
		str.append(" ");
		str.append(((xCoord*16)+15));
		str.append(" ");
		str.append(yCoord);
		str.append(" ");
		str.append(((zCoord*16)+15));
		str.append(" ");
		
		if (Math.abs(xCoord) % 2 == Math.abs(zCoord) % 2) {
			str.append("quartz_block");
		} else {
			str.append("stained_hardened_clay");
		}
		
		result = str.toString();
		System.out.println(result);
		Server.sendCommand(result);
		
		
		/*if (xCoord%2 == zCoord%2) {
			
			Server.sendCommand("/fill "
					+ xCoord*16
					+ " "
					+ yCoord
					+ " "
					+ zCoord*16
					+ " "
					+ ((xCoord*16)+15)
					+ " "
					+ yCoord
					+ ""
					+ ((zCoord*16)+15)
					+ " quartz_block");
		} else {
			Server.sendCommand("/fill "
					+ xCoord*16
					+ " "
					+ yCoord
					+ " "
					+ zCoord*16
					+ " "
					+ ((xCoord*16)+15)
					+ " "
					+ yCoord
					+ ""
					+ ((zCoord*16)+15)
					+ " stained_hardened_clay");
		}*/
	}
	
	public int getX() {
		return xCoord;
	}
	public int getZ() {
		return zCoord;
	}
	public int getY() {
		return yCoord;
	}
	
	static public void setAmtOfChunk(int newAmt) {
		AmtOfChunk = newAmt;
	}
	static public int getAmtOfChunk() {
		return AmtOfChunk;
	}
	
}
