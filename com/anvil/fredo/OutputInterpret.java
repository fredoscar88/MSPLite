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
//		Main.dbOutput("OutputInterpret, Interpret: Interpret succesfully called.");
		input = Reduce(input);
		message = InputCheck(input);
		
		//message.get(0);	//Executor of the command
		//message.get(1); //The command executed by the above
		/*Here we check to see if the executor has the rights to execute the command and if so, executes it*/
		
		if (message != null && (message.get(2) == "PLAYER")) {
			player = message.get(0);
			Main.dbOutput(player + " <-- OI, Interpret");
			msgString = message.get(1);
			
			Main.dbOutput("\"" + player + "\" entered " + "\"" + msgString + "\"");
			message = OIC.PConsoleParse(msgString);
			Main.dbOutput("OI Interpret " + msgString);
			PlayerAction(player, message);
			
		}
		
		
	}
	
	public void PlayerAction(String player, List<String> cmd) throws IOException {
		//Snags the players 'rank' or rather, in this case, checks to see if they are authorized.
		//boolean temp = true;
		
		//What would be better than a single file that stores each player and their rank, give each player a file
		//and store all relevant data there. So there is a directory for player files, and then a fredo file for fredo, etc.
		
		rank = 0; //resets rank, so there's no carry over
		
		Main.dbOutput("OI PlayerAction " + cmd.get(0));
		try {
			
			rank = Integer.parseInt(returnPlayerSetting(player, "rank"));
			Main.dbOutput("(OutputInterpret, PlayerAction) Rank of " + player + ": " + rank /*+ " role: " + returnPlayerSetting(player, "role")*/);
		} catch (Exception e) {
//			System.out.println(player + " has no registered rank, is not a registered player, or some other error occurred");
			//temp = false;
		}
		
		Main.dbOutput("OI PlayerAction 2 " + cmd.get(0));
		commandRun(cmd, player, rank);
		/*switch (cmd.get(0)) {
		case "!OpMe": if (rank >= 1000) Server.sendCommand("op " + player); break;
		case "!MkRdStone": if (rank >= 800) {Main.MakeRedstone();} break;
		case "!RplRdStone": if (rank >= 800) {Main.replaceRedstone();} break;
		case "!Exit": if (rank >= 1000) {Main.running = false; /*make that a setter >:V*/ /*Server.stopServer();} break;
		}*/
	}
	private void commandRun(List<String> cmd, String player, int rank) throws IOException {
		
		Main.dbOutput("OI commandRun: " + cmd.get(0));
		
		File MSPPData = Main.MSPPData;
		
		String enabledSetting = OIFileReader.getSetting(MSPPData, (cmd.get(0) + "-Enabled"));	//Whether this command is enabled
		try {Main.dbOutput("OI, commandRun 2: " + cmd.get(0));} catch (NullPointerException e) {}
		boolean enabled;
		
		try {
			enabled = Main.boolInterpretFromStr(enabledSetting);
			if (!enabled) {
				Server.sendCommand("tell " + player + " That command is not enabled.".replaceAll(" ", " \u00A7r\u00A7c"));
				return;
			}
		}
		catch (NullPointerException e) {
			Main.dbOutput("Not a command!");
			return;
		}
		
		int cmdRank = Main.pInt(OIFileReader.getSetting(MSPPData, (cmd.get(0) + "-Rank")));		//Required minimum rank to run
		String cmdToRun = OIFileReader.getSetting(MSPPData, (cmd.get(0) + "-Cmd"));
		
		Main.dbOutput("OI commandRun 3: " + cmdToRun);
		
		cmdToRun = cmdToRun.replaceAll("player", player);										//Command to run
		try {cmdToRun = cmdToRun.replaceAll("param2", cmd.get(1));} catch (IndexOutOfBoundsException e) {/*There is no second word to this command!*/}
		if (cmdToRun.startsWith("SCRIPT")) {
			try {new Script(cmd.get(1)).run();} catch (IndexOutOfBoundsException e) {/*There is no second word to this command!*/}
			enabled = false; //this is to skip the below rows
		}
		
		//if enabled is false, then we won't get here anyway: we should be stopped by the try/catch above
		if (enabled && (rank >= cmdRank)) {
			switch(cmdToRun) {
			case "MAKEREDSTONE": Main.MakeRedstone(); break;
			case "REPLACEREDSTONE": Main.replaceRedstone(); break;
			case "EXIT": Main.running = false; Server.stopServer(); break;
				default: Server.sendCommand(cmdToRun);
			}
		}
		else if (rank < cmdRank) {
			Server.sendCommand("tell " + player + " You are not allowed to use this command".replaceAll(" ", " \u00A7r\u00A7c"));

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
		
		case '*': temp = InputPlayerMe(input); break;
		case '<': temp = InputPlayer(input); break;
		case '[': temp = InputOP(input); break;// this is /say input, from the server or an operator. So basically OP only stuff.
		default: temp = null;
		
		}
		
		return temp;
	}
	
	static private List<String> InputPlayerMe(String input) {
		List<String> temp = new ArrayList<String>();
		
		for (int i = 3; i < input.length(); i++) {
			if (input.charAt(i) == ' ') {
				
				temp.add(InputRemoveTeam(input.substring(2,i)));	//player's name
				temp.add(input.substring(i + 1));					//Player's message
				temp.add("PLAYER");									//Type of entity that entered command
				
				return temp;
			}
		}
		
		return null;
	}
	static private List<String> InputPlayer(String input) {
		List<String> temp = new ArrayList<String>();
		
		for (int i = 3; i < input.length(); i++) {
			if (input.charAt(i)== '>') {
				
				temp.add(InputRemoveTeam(input.substring(1,i))); //Gets which player entered input (temp.get(0))
				
				temp.add(input.substring(i+2));	//Gets what the player input (temp.get(1))

				temp.add("PLAYER");	//Gets who entered the input (temp.get(2))
				
				return temp;
				
			}
		}
		
		return null;
	}
	static private List<String> InputOP(String input) {
		List<String> temp = new ArrayList<String>();
		
		for (int i = 1; i < input.length(); i++) {
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
//	^We will probably need to use this here to determine when a server has been stopped
	
	static private String InputRemoveTeam(String playerName) {
		
		
		if (playerName.contains("\u00A7")) {	// \u00A7 is the section symbol
			Main.dbOutput("yolo");
			//neuveau
			Main.dbOutput("OI, InputRemoveTeam: " + playerName);
			
			
			//neuveau
			if (playerName.contains("[")) {
				for (int i = 5; i < playerName.length(); i++) { 
					if (playerName.charAt(i) == '\u00A7') {
						return playerName.substring(i+2);
					}
				}
			}
			else {
				Main.dbOutput("----------------------"
						+ "\n" + playerName);
				playerName = playerName.substring((playerName.indexOf("\u00A7") + 2), playerName.length());
				Main.dbOutput(playerName);
				playerName = playerName.substring(0, playerName.lastIndexOf("\u00A7"));
				Main.dbOutput(playerName
						+ "\n----------------------");
				//I should just be able to do a replace("\uAA07 + ."), . being the meta character class referring to any character.
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
