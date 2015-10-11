package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MakeRedstone {

	FileUpdater rsReader;
	String fileString;
	boolean continueLineCheck = true;
	List<String> lineList = new ArrayList<String>();
	Console MkRsConsole;
	List<String> parsedLine;
	
	int[] mainFirstBlockCoords = new int[3];
	int[] mainPattern = new int[3];
	
	Block block;
	
	//Make sure not everything is in the constructor...
	public MakeRedstone(File rsFile) throws IOException {
		
		MkRsConsole = new Console("Make Redstone console");
		rsReader = new FileUpdater();
		fileString = stringPrep(rsFile);
		
		lineList = getLines(fileString);
		lineList = removeComments(lineList);
		
		
		
		//Specifically the main file
		if (rsFile.getName().toLowerCase().equals("main.txt")) {
			mainFile(lineList);
				//We start on the fourth line b/c the first two are not commands and the third was already made into a block
			for (int i = 3; i < lineList.size(); i++) {
				new Block(lineList.get(i),Block.CHAIN);
			}
		} else {
		
			//All other files
			System.out.println("Doing file \"" + rsFile.getName() + "\"");
		
			new Block(lineList.get(0),Block.REPEAT);	//We separate this from the others. When we use a REPEAT block, it
														//indicates to the Block class that a new set is being called.
			//Start on 2nd line here b/c the above already made the first line into a command
			for (int i = 1; i <lineList.size(); i++) {
				new Block(lineList.get(i),Block.CHAIN);
			}
			
			//Here we'd have the loop where we encounter the stuff line by line. Note we have to skip the First ? lines if main
		
			
		}
		
		
	}
	
	private void mainFile(List<String> lineList) {
		
		//Snags Pattern and First Block Position from the main file
		try {
			parsedLine = MkRsConsole.PConsoleParse(lineList.get(0));
			parsedLine.remove(0);
			for (int i = 0; i < 3; i++) {
				mainFirstBlockCoords[i] = Main.pInt(parsedLine.get(i));
				System.out.println("Coord: " + parsedLine.get(i));
			}
			
			parsedLine = MkRsConsole.PConsoleParse(lineList.get(1));
			parsedLine.remove(0);
			for (int i = 0; i < 3; i++) {
				mainPattern[i] = Math.abs(Main.pInt(parsedLine.get(i)));	//I don't want to find out how it reacts to a negative pattern
				System.out.println("Pattern: " + parsedLine.get(i));
			}
			
			block = new Block(mainFirstBlockCoords,mainPattern,lineList.get(2));
		}
		catch (Exception e) {
			mainFirstBlockCoords = new int[] {-64,64,-64};
			mainPattern = new int[] {2,192,16};
			block = new Block(mainFirstBlockCoords,mainPattern,"tell @p[r=2] Yeah an exception happened. You're exceptional!");
		}
	}
	
	
	public List<String> removeComments(List<String> lines) {
		int tempCondition = lines.size();
		
		for (int i = 0; i < tempCondition; i++) {
			
			if (lines.get(i).startsWith("#")) {
				lines.remove(i);
				i--;
				tempCondition--;
			}
			
		}
		
		return lines;
	}
	
	public List<String> getLines(String fileString) {
		
		List<String> tempList = new ArrayList<String>();
		
		for (int i = 0; continueLineCheck; i++) {
			
			while (fileString.startsWith(";")) {
				fileString = fileString.substring(1);
			}
			
			try {
				
				if (fileString.substring(i, i+1).equals(";")) {
					
					tempList.add(fileString.substring(0,i));
					fileString = fileString.substring(i+1);
					i=0;
					
					if (fileString.replace(";","_").equals(fileString)) {
						
						continueLineCheck = false;
						return tempList;
					}
					
					//make a new block here
				}
			}	catch (IndexOutOfBoundsException e) {
				System.out.println("This line of code should not be reached! (MakeRedstone, List<String> filler loop)");
				continueLineCheck = false;
			}
				
			
		}
		
		return null;	//Should not be reachable.
	}
	
	public String stringPrep(File file) throws IOException {
		String rsFileString;
		
		rsFileString = crossPlatformCompatibilityMaker(rsReader.read(file));
		
		rsFileString = rsFileString.replace(";", "");	//Removes any misc. semi-colons
		rsFileString = rsFileString.replace("\n", ";");	//Replaces line breaks with semi-colons
		while (rsFileString.endsWith(";")) {
			rsFileString = rsFileString.substring(0,(rsFileString.length()-1));	//Removes semi-colons at end of string
		}
		rsFileString = rsFileString.concat(";");	//Sticks a single semi-colon on the end
		
		//Replaces all the shortcuts with their proper phrases in the string for parsey goodness
		rsFileString = replaces(rsFileString);	//ATM this method does naught, as we haven't implemented replaces yet.
		
		//returns to be lexed (parsed?) into a List, composed of each line.
		return rsFileString;
	}
	public String crossPlatformCompatibilityMaker(String str) {

		return str.replaceAll(System.lineSeparator(), "\n");
	}
	public String replaces(String str) {
		
		return str;
	}
	
	
	
}


/*	System.out.println("SUBSTRING: " + fileString.substring(0, 1) + fileString.substring(1, 2));
if (fileString.substring(1,2).equals("\n")) {
	System.out.println("booyah");
}
System.out.println("CHAR AT: " + fileString.charAt(0) + fileString.charAt(1));
if (Character.toString(fileString.charAt(1)).equals("\n") ) {
	System.out.println("booyah?!?!?");
	
}*/


/*for (int i = 0; continueLineCheck; i++) {

try {
	
	if (fileString.substring(i, i+1).equals(";")) {
		
		System.out.println("A line: " + fileString.substring(0,i));
		System.out.println("should be semi-colon: " + fileString.substring(i,i+1));
		
		fileString = fileString.substring(i+1);
		i=0;
		
		if (!fileString.replace(";","_").equals(fileString)) {
			
			System.out.println("Remainder: " + fileString);
			//System.out.println("And we're done.");
		}
		
		//make a new block here
	}
}	catch (IndexOutOfBoundsException e) {
	System.out.println("And we're done!");
	continueLineCheck = false;
}
	

}*/
