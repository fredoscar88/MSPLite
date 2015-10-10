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
	
	//Make sure not everything is in the constructor...
	public MakeRedstone(File rsFile) throws IOException {
		
		MkRsConsole = new Console("Make Redstone console");
		rsReader = new FileUpdater();
		fileString = stringPrep(rsFile);
		
		lineList = getLines(fileString);
		
		if (rsFile.getName().toLowerCase().equals("main.txt")) {
			System.out.println("\\o/");
			parsedLine = MkRsConsole.PConsoleParse(lineList.get(0));
			
			try {
				System.out.println(parsedLine.get(1) + " " + parsedLine.get(2) + " " + parsedLine.get(3));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println();
		//Here we'd have the loop where we encounter the stuff line by line
		for (int i = 0; i < lineList.size(); i++) {
			System.out.println(lineList.get(i));
		}
		
		
		
		
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
	
	public String replaces(String str) {
		
		return str;
	}
	
	public String crossPlatformCompatibilityMaker(String str) {

		return str.replaceAll(System.lineSeparator(), "\n");
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
