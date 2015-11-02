package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//@SuppressWarnings("unused")
public class MakeRedstone {

	FileUpdater rsReader = new FileUpdater();
	
	String fileString;
	boolean continueLineCheck = true;
	List<String> lineList = new ArrayList<String>();
	Console MkRsConsole;
	List<String> parsedLine;
	
	int[] mainFirstBlockCoords = new int[3];
	int[] mainPattern = new int[3];
	
	Block block;
	
	private boolean conditional;
	
	boolean referencePointAddTo = false;
	int referencePointIndex;
	
	boolean isBlock;
	
	RedstoneFile mainRsFile;
	
	
	//The fresh face of MakeRedstone (the constructor below is legacy as of Alpha 1.0.1)
	public MakeRedstone(File rsDir, HashSet<String> excludedFiles) throws IOException/*, NullPointerException */{
		try {
//			System.out.println("The fresh face of MakeRedstone engaged!");
			MkRsConsole = new Console("Fresh face of MkRdStone");
			ReferencePoint.resetReferencePoint();
			
			//ew legacy code (TODO)
			File mainFile = new File(rsDir.getName() + File.separator + "main.txt");
			mainRsFile = new RedstoneFile(mainFile);
			
			lineList = mainRsFile.returnLines();	//list of main file
			
			//Sets the first block and defines the pattern for every block hence (mainFile method)
			mainFile(lineList);	//Default calls the repeat block. I have confusingly named this the same thing as a variable.
			lineList.remove(0); lineList.remove(0);	//gets rid of the two three lines, those lines aren't commands, and the third waas already set but is by default ignored in createredstone since it is the repeat one.
//			System.out.println("first command :D but not because that was already set. " + lineList.get(0));	//informs me what is being set :<
			
			createRedstone(mainFile, lineList);	//The repeat block should be set before this is called for each file, after main.
			
			
			//Basically everything above this comment is specifically for mainFile. :I
			//Loop for the rest of the files
			fileListLoop(rsDir, excludedFiles);
			
			
			
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("No main redstone file in directory, or some other error occurred.");
		}
		
	}
	
	public MakeRedstone(File rsDir, HashSet<String> excludedFiles, boolean A101) throws IOException {	//The boolean literally exists just to distinguish this from the above constructor which Im not removing yet since I still need this to be functional
		MkRsConsole = new Console("MakeRedstone function- Updated last: Alpha 1.0.1");
		fileListLoop(rsDir, excludedFiles, A101);
		
	}
	
	public void fileListLoop(File dir, HashSet<String> excludedFiles, boolean A101) throws IOException {
		
		RedstoneFile tempFile;	
		//reminder that we might make RedstoneFile extend File
		for (File file : dir.listFiles()) {
			referencePointAddTo = false;
			
			if (file.isFile() && excludedFiles.add(file.getName())) {
				tempFile = new RedstoneFile(file);
				//So instead of making the first block at the outset here, we are going to run createRedstone on this file to get access to that juicy juicy interpretLine
				new Block(tempFile.returnLines().get(0),Block.REPEAT, false);	//yep. Repeat block is set b4 the file begins.
				createRedstone(file, tempFile.returnLines());
			}
			
			if (file.isDirectory()) {
				fileListLoop(file, excludedFiles);	//Should directories get their own default params???? : <<<<<
			}
			
		}
	}
	
	
	//Legacy
	public void fileListLoop(File dir, HashSet<String> excludedFiles) throws IOException {
		
		RedstoneFile tempFile;
		for (File file : dir.listFiles()) {
			referencePointAddTo = false;
			
			if (file.isFile() && excludedFiles.add(file.getName())) {
				tempFile = new RedstoneFile(file);
				new Block(tempFile.returnLines().get(0),Block.REPEAT, false);	//yep. Repeat block is set b4 the file begins.
				createRedstone(file, tempFile.returnLines());
			}
			
			if (file.isDirectory()) {
				fileListLoop(file, excludedFiles);
			}
			
		}
	}
	
	//Ew legacy code, remove pls :VVVV
	private void mainFile(List<String> lineList) throws IOException {
		
		//Snags Pattern and First Block Position from the main file
		try {
			

			if (lineList.get(0).startsWith("POS")) {
				
				parsedLine = MkRsConsole.PConsoleParse(lineList.get(0));
				parsedLine.remove(0);
				for (int i = 0; i < 3; i++) {
					mainFirstBlockCoords[i] = Main.pInt(parsedLine.get(i));
//					System.out.println("Coord: " + parsedLine.get(i));
				}																//POS getter
			}
			else {
				mainFirstBlockCoords = new int[] {-64,64,-64};
			}
//			----------------------------------------------------------------------------------------

			if (lineList.get(1).startsWith("PAT")) {
				parsedLine = MkRsConsole.PConsoleParse(lineList.get(1));
				parsedLine.remove(0);
				for (int i = 0; i < 3; i++) {
					mainPattern[i] = Math.abs(Main.pInt(parsedLine.get(i)));	//I don't want to find out how it reacts to a negative pattern
//					System.out.println("Pattern: " + parsedLine.get(i));		
				}																//PAT getter

			}
			else {
				mainPattern = new int[] {2,192,16};
			}
//			System.out.println("First command? " + lineList.get(2));
			block = new Block(mainFirstBlockCoords,mainPattern,lineList.get(2));	//The first actual line of stuff. This is also a REPEAT CB
		}
		catch (Exception e) {
			e.printStackTrace();
			mainFirstBlockCoords = new int[] {-64,64,-64};
			mainPattern = new int[] {2,192,16};
			block = new Block(mainFirstBlockCoords,mainPattern,"tell @p[r=2] Yeah an exception happened. You're exceptional!");
		}
	}
	
	private void createRedstone (File file, List<String> list) throws IOException {
		
		System.out.println("Doing file \"" + file.getName() + "\"");
		
		//Start on 2nd line here b/c the above already made the first line into a command
		Main.dbOutput(file.getName() + " amt. of lines:  " + list.size() );
		for (int i = 1; i < list.size(); i++) {
			conditional = false;
			Main.dbOutput("COMMAND: " + list.get(i));
			interpretLine(list.get(i));
			//if interpretLine fails, just comment it out and uncomment out below
//			new Block(list.get(i),Block.CHAIN, conditional);
		}
	}
	
	//This is SOOOOO messy. Maybe, just, mah beh, we should separate these out to separate functions to clean it the fuck up. Even though that doesn't really make too much sense.
	private void interpretLine(String line) throws IOException {
//(TODO)Right, so I think at this point we could interpret comments and line breaks here. Im not sure we need to go through several complicated file preparations.
		isBlock = true;
		
//(TODO)We need to include a warning with CONDITIONAL, because it will not automatically depend on the previous block and players need to be aware of that when doing patterns!
		if (line.startsWith("CONDITIONAL ")) {conditional = true; line = line.substring(12);}	//Errr, this needs to NOT return;, and instead remove the CONDITIONAL from the start of the line (TODO)
		if (line.startsWith("INIT ")) {
			isBlock = false; 
			line = line.substring(5); 
//			System.out.println("Everything is going alright! (MakeRedstone, interpretLine): " + line);
//			rsReader may not be initialized
			rsReader.write(Main.redstoneOutputFile, line);
		}
		if (line.contains("-> ")) {
			Main.dbOutput("Reference point referenced! (MakeRedstone, interpretLine)");
			String referencePoint = line.substring(line.lastIndexOf("->") + 3);
			
			Main.dbOutput("The point referenced is: " + referencePoint);
			new ReferencePoint(referencePoint);
			Main.dbOutput(ReferencePoint.getCoordsString(referencePoint));
			//functional V: we should make sure this works
			line = line.substring(0, line.lastIndexOf("->")-1);
			line = line.concat(" /setblock " + ReferencePoint.getCoordsString(referencePoint) + " minecraft:redstone_block");
		}
		if (line.startsWith("*")) {
			//Here is where we declare what a reference point points to :) i.e the chain CBs after a ref pt. Note that this is direction dependent but for the moment we assume EAST
			isBlock = false;
			
			referencePointAddTo = true;	//For the remainder of this file, this is true.
			String referencePoint = line.substring(1, line.length());
			new ReferencePoint(referencePoint);
			referencePointIndex = ReferencePoint.getRPIndex(referencePoint);
			
			Block.currentBlock = 0; //Make this into, like, a setter. And not a direct, reach-into-class-to-chance sort of deal. (TODO)
			//Note it's 0 and not -1 like when a new makeRedstone is called.
			new Block("setblock ~-1 ~ ~ minecraft:stained_glass 14", Block.IMPULSE, false, referencePointIndex);
		}
		if (line.startsWith("DELAY ")) {
			line = line.substring(6/*line.lastIndexOf(" ")*/);	
			int delay = Main.pInt(line);
//			System.out.println(delay);
			Block.delayRP(referencePointIndex, delay);
			
			isBlock = false;
		}
		
		Main.dbOutput(line);
		
		//If we're not adding to a reference point and the line we encountered is a single block, then..
		if (!referencePointAddTo && isBlock) new Block(line,Block.CHAIN, conditional);
		else if (isBlock) new Block(line,Block.CHAIN, conditional, referencePointIndex);
		
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
