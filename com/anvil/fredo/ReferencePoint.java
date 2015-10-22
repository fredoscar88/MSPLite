package com.anvil.fredo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReferencePoint {
	
//	static int rpNum = -1;
	static List<String> rpList = new ArrayList<String>();
	static HashSet<String> rpSet = new HashSet<String>();
	
	static String[] stringArray;
	
//	String name;
	int num;
	static int[][] activateCoords = new int[9999999][3];
	
	public ReferencePoint(String name) {
		
		if (rpSet.add(name)) {
			
			rpList.add(name);
			num = rpList.lastIndexOf(name);
			
			activateCoords[num] = getCoordsInt(num);
			//Thing is, we don't even need to really have a class for all of this. We just need a formula to snag coords
			//which Block provides us with
		}
		else {
			Main.dbOutput("This reference point already exists!");
		}
	}
	
	static public String getCoordsString(String reference) {
		StringBuilder sb = new StringBuilder();
		int tempIndex = rpList.lastIndexOf(reference);
		
		sb.append(activateCoords[tempIndex][0]);
		sb.append(" ");
		sb.append(activateCoords[tempIndex][1]);
		sb.append(" ");
		sb.append(activateCoords[tempIndex][2]);
		
		return sb.toString();
	}
	static public int getRPIndex(String reference) {
		return rpList.lastIndexOf(reference);
	}
	
	private int[] getCoordsInt(int setNum) {
		
		return Block.getRPCoords(setNum);
	}
	
	static public void resetReferencePoint() {
		rpList = new ArrayList<String>();
		rpSet = new HashSet<String>();
		
	}
	
}
