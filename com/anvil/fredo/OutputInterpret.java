//MSPLite
package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//this class will serve like one purpose :I
public class OutputInterpret {
	
	List<String> message;
	Console OIC;
	int rank;
	String player;
	String msgString;
	
	static FileUpdater OIFileReader;
	
	//File playerFile;
	//String playerFilePath;
	
	public OutputInterpret() {
		
		OIFileReader = new FileUpdater();
		this.OIC = new Console("Output Interpret console");
		
	}
	
	public void Interpret(String input) throws IOException {
		Main.dbOutput("OutputInterpret, Interpret: Interpret succesfully called.");
		input = Reduce(input);
		message = InputCheck(input);
		
		//message.get(0);	//Executor of the command
		//message.get(1); //The command executed by the above
		/*Here we check to see if the executor has the rights to execute the command and if so, executes it*/
		
		if (message != null && (message.get(2) == "PLAYER")) {
			player = message.get(0);
			msgString = message.get(1);
			
			System.out.println("\"" + player + "\" entered " + "\"" + msgString + "\"");
			message = OIC.PConsoleParse(msgString);
			
			PlayerAction(player, message);
			
		}
		
		
	}
	
	public void PlayerAction(String player, List<String> cmd) throws IOException {
		//Snags the players 'rank' or rather, in this case, checks to see if they are authorized.
		//boolean temp = true;
		
		//What would be better than a single file that stores each player and their rank, give each player a file
		//and store all relevant data there. So there is a directory for player files, and then a fredo file for fredo, etc.
		try {
			
			rank = Integer.parseInt(returnPlayerSetting(player, "rank"));
			System.out.println("(OutputInterpret, PlayerAction) Rank of " + player + ": " + rank + " role: " + returnPlayerSetting(player, "role"));
		} catch (Exception e) {
			//System.out.println(player + " has no registered rank or not a registered player (or some other error occurred");
			//temp = false;
		}
		
		switch (cmd.get(0)) {
		case "!OpMe": if (rank >= 800) Server.sendCommand("op " + player); break;
		case "!MkRdStone": if (rank >= 1000) {Server.sendCommand("say Making redstone..."); Main.MakeRedstone(Main.redstoneTxtDir); Server.sendCommand("say Done!");} break;
		case "!Exit": if (rank >= 1000) {Main.running = false; /*make that a setter >:V*/ Server.stopServer();} break;
		}
	}
	
	
	
	
	
	
	static public String Reduce(String input) {
		
		input = input.substring(11);
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ']') {
				input = input.substring(i+3);	//We need to do some try catching here, as some of the strings have input out of bounds. We can't interpret that. so if it fails, have the catch jut return some bs :)
				
				return input;
			}
		}
		
		return input;
	}
	
	static private List<String> InputCheck(String input) {
		List<String> temp;
		char tempFirstChar = input.charAt(0);
		
		switch (tempFirstChar) {
		
		case '<': temp = InputPlayer(input); break;
		case '[': temp = InputOP(input); break;// this is /say input, from the server or an operator. So basically OP only stuff.
		default: temp = null;
		
		}
		
		return temp;
	}
	
	static private List<String> InputPlayer(String input) {
		List<String> temp = new ArrayList<String>();
		
		for (int i = 3; i < input.length(); i++) {
			if (input.charAt(i)== '>') {
				
				temp.add(InputRemoveTeam(input.substring(1,i))); //Gets which player entered input (temp.get(0))
				
				temp.add(input.substring(i+2));	//Gets what the player input (temp.get(1))
				//System.out.println();	//Prints a line for legibility's sake
				temp.add("PLAYER");	//Gets who entered the input (temp.get(2))
				
				return temp;
				
			}
		}
		
		return null;
	}
	static private List<String> InputOP(String input) {
		List<String> temp = new ArrayList<String>();
		
		for (int i = 3; i < input.length(); i++) {
			if (input.charAt(i)== ']') {
				temp.add(input.substring(1,i)); //Gets which operator entered input (temp.get(0))
				
				temp.add(input.substring(i+2));	//Gets what the op input
				//System.out.println();	//Prints a line for legibility's sake
				temp.add("OP");
				
				return temp;
			}
		}
		
		return null;
	}
	
	static private String InputRemoveTeam(String playerName) {
		
		Main.dbOutput("Hello");
		if (playerName.contains("\u00A7")) {	// \u00A7 is the section symbol
			Main.dbOutput("yolo");
			for (int i = 5; i < playerName.length(); i++) { 
				if (playerName.charAt(i) == '\u00A7') {
					return playerName.substring(i+2);
				}
			}
			return playerName;
		}	
		else {
			return playerName;
		}
		
		
	}
	
	static public String returnPlayerSetting(String player, String setting) throws IOException {
		String playerSetting;
		String playerFilePath;
		File playerFile;
		
		//"players" is the directory MSPPlayers
		playerFilePath = (Main.players.getName() + File.separator + player + ".txt");
		playerFile = new File(playerFilePath);
		playerSetting = OIFileReader.getSetting(playerFile, setting);
		
		return playerSetting;
	}
}

/*
public class OutputInterpret {
	//It's console output, but player/etc. input
	int serverID;
	File OutputR;
	BufferedWriter bw;
	String input;
	String txt;
	
	BufferedReader fileReader;
	String output;
	boolean scriptRun;
	
	String playerName;
	int cmdIssuer;
	final int PLAYER_ISSUED_TXT = 0;
	
	public OutputInterpret(int id) throws IOException {
		serverID = id;
		OutputR = new File("OutputReduced.txt");
		bw = new BufferedWriter(new FileWriter(OutputR));
		
		System.out.println("Output Interpreter starting up");
		Server.sendCommand("say hello");
		
		playerName = "";
	
	}
	
	public void Interpret(String serverInput) throws IOException {
		
		if (!Server.getRunning(serverID)) {return;}
		
		//System.out.println("Successfully ran interpret!");
		
		try {
			
			this.input = serverInput;
			Reduce();				//removes nonuseful information from input
			bw.write(input);		//Writes reduced input to output log
			bw.newLine();			//"resets" buffered writer
			bw.flush();
			
		} catch (Exception e) {
			System.out.println("The server probably threw an error and the interpreter wasn't able to figure it out");
		}
		
		//System.out.println(input);	//Prints reduced information to the console
		
		//For player issued commands (The '<' indicates a player just spoke. Tellraws don't get sent to the input stream so they shouldn't be a bother.)
		if (input.charAt(0) == '<') {
			
			cmdIssuer = PLAYER_ISSUED_TXT;
			
			for (int i = 3; i < input.length(); i++) {
				if (input.charAt(i)== '>') {
					playerName = input.substring(1, i);	//Gets which player entered input
					
					txt = input.substring(i+2);	//Gets what the player inputted
					//System.out.println();	//Prints a line for legibility's sake
					
				}
			}
			
			if (!txt.startsWith("!")) {return;} //Cuts interpretation if the player hasn't sent a command
			//else :V
			
			scriptRun = true;
			File scriptExclaim = new File("ExclaimScript");
			fileReader = new BufferedReader(new FileReader(scriptExclaim));
			
			while (scriptRun) {
			
				try {
					output = fileReader.readLine();
					output = output.replace("@playername", playerName);
					
					if (output.startsWith("print")) {
						output = output.substring(5).trim();
						System.out.println(output);
					} else {
				
						Server.sendCommand(output);
					
					}
				
				} catch (NullPointerException e) {
					scriptRun = false;
					System.out.println("A player has executed the \"!\" ");
				}
				
			}
			//If a command HAS been spoken, we'd at this point find out what the command was and check player permissions
			//For now, anytime a player enters the ! it will run a testing script (to practice scripts and just because)
			//Server.sendCommand("say YO!!!!! " + playerName + "! How YOU doin'?");
		}
		
		
		
		
		//we have to interpret the command AND the player THEN see if the player is allowed to use it
		//Hell player perms might reset too, we have to keep it updated. Say they're in a game, the !TP command
		//would not be the same.
		
		//input = input.substring(33);
		//System.out.println(input);
		//input = input.startsWith("");
		
		
		
	}
	
	private void Reduce() throws IOException {
		
		input = input.substring(11);
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ']') {
				input = input.substring(i+3);	//We need to do some try catching here, as some of the strings have input out of bounds. We can't interpret that. so if it fails, have the catch jut return some bs :)
				bw.write(input);
				bw.newLine();
				return;
			}
		}
		
	}
	
	
	//Check the players' allowed commands
	private boolean canExec(Player pl, String command) {
		
		return false;
	}
	
}
*/
