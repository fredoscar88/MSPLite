package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//to-do: add shortcuts
public class RedstoneFile {

	static FileUpdater RSFileReader = new FileUpdater();
	String fileContents;
	
	boolean continueLineCheck;
	
	List<String> lineList;
	
	public RedstoneFile(File file) throws IOException {
		
		lineList = new ArrayList<String>();
		continueLineCheck = true;
		
		fileContents = RSFileReader.read(file);
		Main.dbOutput("RS FILE " + file.getName());
		/*Main.output(fileContents + " <<<< RedstoneFile constructor message");*/
		fileContents = stringPrep(fileContents);
		
		lineList = getLines(fileContents);
		lineList = removeComments(lineList);
		
	}
	
	public List<String> returnLines() {
		
		return lineList;
	}
	
	
	//Removes comments
	private List<String> removeComments(List<String> lines) {
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
	
	//Parses out the string received from a file. Normally we could just read the file line by line, but we can't here,
	//we can only look at the string and interpret line breaks.
	private List<String> getLines(String fileString) {
		
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
				System.out.println("My guess is the file was empty.");
				continueLineCheck = false;
			}
				
			
		}
		System.out.println("Somehow this borked. MakeRedstone getLines method.");
		return null;	//Should not be reachable.
	}

	
	
	
	
	private String stringPrep(String rsFileString) throws IOException {
		
		
		rsFileString = crossPlatformCompatibilityMaker(rsFileString);	//Ensures that the line breaks are \n and not \r\n or whatever.
		
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
	//Because microsoft loves it's non-standard line breaks, we have to replace the system's default breaks with \n
	private String crossPlatformCompatibilityMaker(String str) {
		if (str != null) {
			return str.replaceAll(System.lineSeparator(), "\n");
		}
		else {
			Main.dbOutput("OI! There is no string here! (Redstone file, crossPlatformCompatibilityMaker");
			return null;
		}
	}
	//Eventually we will have a textfile full of strings that will get replacement treatment.
	private String replaces(String str) {
		
		return str;
	}

	
	
}
