//MSPLite

//HUGE DISCLAIMER TO MYSELF
/*
 * Er, this was not built to utilize all benefits of OOP. I'm not using named instances of the singular server,
 * rather I just use a server class very staticly so this will not be feasible to port to MSP full, but we can still
 * build off of it. It will be a bit hard is all.
 */

package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

//@SuppressWarnings("unused")
public class Main {

	//How much of this shit is unused. I mean seriously.
	
	static final String VERSION = "Alpha V1.1.0";
	
	static String ConsoleInput/* = "start"*/; 	//temporarily defaults to start //(not right now)
	static String ConsoleCmd;	//
	static Scanner ConsoleReader;	//Scanner to acquire input
	static boolean running;	//If the main thread is running. If false should close all other threads.
	static List<String> cmd;
	static boolean debug = false;
	
	//static File dirServers;	//Servers directory
	//static File dirRes;	//Resources directory
	//static File dataServerData;	//General server data file	(Contains meta data like # of servers)
	static File eulaTXT;	//EULA text file
	static File logs;
	static File redstoneTxtDir;
	static File playerRank;
	static File players;
	static File testPacket;
	static File redstoneOutputFile;
	static File redstoneMainFile;
	static File mspProps;
	
	//static BufferedWriter fileWriter;	//May not be needed
	static String fileReadoutValue;
	
	static FileUpdater MainThreadFileUpdater;
	
	static Console mainConsole;
	
	static Server server;
	
	static String worldName;
	
	
	//I eventually want the main class to open a new console so I don't have to run the program from
	//	a batch file.
	public static void main(String[] args) throws IOException, InterruptedException {
		
		try {
			worldName = args[1];
		} catch (IndexOutOfBoundsException e) {
			worldName = "world";
		}
		
		//Rename class to FileAccess?
		MainThreadFileUpdater = new FileUpdater();
		addDirectories();
		//NOTE If no args are specified in running the server (i.e you double click MSP.jar) it will cut off when
		//it tries to find args[0]. No server will start, and the program crashes. This is intentional
		//behavior as right now a console should be used to start the program as it is useless without a GUI.
//		For the above it is now going to read from a general MSP properties folder. No biggy. Note to self, separate redstone into world files for easyness
		System.out.println("Running MCServerPal! " + VERSION);
//		System.out.println(args[0]);
//		AutoStart(args[0]);
		
		cmd = new ArrayList<String>();
		
		mainConsole = new Console("Main Console");
	
//		May need try/catch around this
//		Also tempted to add a String[] return type to the console class just cus
		List<String> tempList = mainConsole.PConsoleParse(MainThreadFileUpdater.getSetting(mspProps, "Startup_Arguments"));
		AutoStart(tempList);
		
		mainRunning(mainConsole);
		
		output("MCServerPal closing");
		//Here's where we stop the server
		
	}
	
	/*public static void main(String[] args) throws IOException {
		makeSpawnChunks(100,64,100,5);
	}*/
	
	static void mainRunning(Console console) throws IOException, InterruptedException {
		
		running = true;
		
		System.out.println("Type \"?\" or \"help\" for help.");
		while (running) {
			//I might want to put the try catch here, but am not doing so RN so I can find the problem easier.
			cmd.clear();
			cmd = console.ConsoleRun();
			ConsoleAction(cmd);
			
			
		}
		
	}
	
	static void ConsoleAction (List<String> cmd) throws IOException, InterruptedException {
		
		try {
			switch (cmd.get(0)) {
			case "?":
			case "help": help(cmd); break;
			case "stop":
			case "exit": running=false; Server.stopServer(); break; //Exits console, stops all servers (by sending stop commands to their own ConsoleAction menus, we'll create a function for this). Uh yeah. Also waits for user input to close.
			case "MakeRedstone": MakeRedstone(); break;
			case "MakeSpawnChunks": makeSpawnChunks(pInt(cmd.get(1)),pInt(cmd.get(2)),pInt(cmd.get(3)),pInt(cmd.get(4))); break;
			case "ping": Server.sendCommand("say Pong!"); break;
			case "debug": debug = !debug; System.out.println(debug); break;
			case "ReplaceRedstone": replaceRedstone(); break;
//			case "getrank": output(OutputInterpret.returnPlayerSetting(cmd.get(1), "rank")); break;
//			case "getrole":	output(OutputInterpret.returnPlayerSetting(cmd.get(1), "role")); break;
//			case "server":
//			case "testpacket": output(MainThreadFileUpdater.read(testPacket)); Server.sendCommand(MainThreadFileUpdater.read(testPacket)); break;
			
			default : System.out.println("Type \"Help\" or \"?\" for help"); Main.ConsoleInput = null;
			}
		} catch (IndexOutOfBoundsException e) {
			//TEMPORARY DEBUG
			//e.printStackTrace();
			System.out.println("[MCServerPal] Error: Missing syntax");
			getSyntax(cmd.get(0));
			//e.printStackTrace();	//NO. MAYBE write to an output log. MAYBE.
		}
		
	}
	
	static int pInt(String sInt) {
		return Integer.parseInt(sInt);
	}
	
	
	//-------------------------------------------------------------------------------------------------------
	//(REMOVE)The whole start server is getting overhauled. Until I remove this comment, we're focusing on setting up a new server.

	//Adds in any necessary directories, should they not exist
	static void addDirectories() throws IOException {
//(TODO)Add output if MSP adds directories/files
		
		eulaTXT = new File("eula.txt");
		if (eulaTXT.createNewFile()) {
			MainThreadFileUpdater.write(eulaTXT, "eula=true");
		}
		
//		testPacket = new File("TestPacket.txt");
//		testPacket.createNewFile();
		
		/*logs = new File("logs");
		logs.mkdirs();*/
	
		
		//When a player joins for the first time, we can probably add them as rank 0 to the file using file updater.
		//We'd use OIUpdater of course, no need to create conflict. I have to add a method to add a setting.
		players = new File("MSPPlayers");
		players.mkdirs();
		
		mspProps = new File("MSP.properties");
		if (mspProps.createNewFile()) {
			MainThreadFileUpdater.write(mspProps, "#Pattern=2 64 16"
					+ "\n#First_Block=-48 64 -48"
					+ "\n#Direction=EAST"
					+ "\nRedstoneDir=Redstone"
					+ "\n#^ As of now these are not checked. I'm only laying the ground work for the future,"
					+ "\n#I just wanted to get this update out quick-like to get on github -fredo"
//					+ "\nsilent-mode=false"
					+ "\nStartup_Arguments=java -Xms1024M -Xmx1024M -jar server.jar");
		}
		
		
		//new File("MSPPlayers" + File.separator + "fredo.txt").createNewFile();
		
		//playerRank = new File("MSPPlayers" + File.separator + "PlayerRank.txt");
		//playerRank.createNewFile();
		
		/*File servProps = new File("server.properties");
		if (servProps.createNewFile()) {
			MainThreadFileUpdater.write(servProps,"generator-settings="
					+ "\nop-permission-level=4"
					+ "\nresource-pack-hash="
					+ "\nlevel-name=" + worldName
					+ "\nallow-flight=false"
					+ "\nannounce-player-achievements=true"
					+ "\nserver-port=25565"
					+ "\nmax-world-size=29999984"
					+ "\nlevel-type=DEFAULT"
					+ "\nlevel-seed="
					+ "\nforce-gamemode=false"
					+ "\nserver-ip="
					+ "\nnetwork-compression-threshold=256"
					+ "\nmax-build-height=256"
					+ "\nspawn-npcs=true"
					+ "\nwhite-list=false"
					+ "\nspawn-animals=true"
					+ "\nhardcore=false"
					+ "\nsnooper-enabled=true"
					+ "\nonline-mode=true"
					+ "\nresource-pack="
					+ "\npvp=true"
					+ "\ndifficulty=1"
					+ "\nenable-command-block=true"
					+ "\ngamemode=0"
					+ "\nplayer-idle-timeout=0"
					+ "\nmax-players=20"
					+ "\nspawn-monsters=true"
					+ "\ngenerate-structures=true"
					+ "\nview-distance=10"
					+ "\nmotd=An MSP Minecraft Server");
		}*/
		
	}

	static void output (String message) {
		System.out.println("[MCServerPal] " + message);
		//Server.sendCommand(message);
	}
	static void dbOutput(String message) {
		
		if (debug) System.out.println("DEBUG: " + message);
	}
	
	//maybe use this one with MakeSpawnChunks?
	static public int modulo(int x, int y) {
		
		int result;
		
		if (x < 0) {
			result = y + (x % y);
			if (result % 16 == 0) {
				result = 0;
				System.out.println("Bing bong!");	//<---WHY
			}
		}
		else {
			result = (x % y);
		}
		
		return result;
		
	}
	
	//Automatically starts the server, put into a method because why not. Bear in mind since this is MSPLite there is
	//only one server to start.
//	Legacy
	static void AutoStart(String jarname) throws IOException, InterruptedException {
		
//		new Server(jarname);
		Server.sendCommand("say MSPLite " + VERSION);
		//Temporary backdoor access :> (just so I don't have to annoyingly do this)
		//Server.sendCommand("op fredo");
		//FirstTimeStartup script
		//generic startup script
	/*	Server.sendCommand("gamerule doMobSpawning false");
		Server.sendCommand("gamerule doDaylightCycle false");
		Server.sendCommand("gamerule doEntityDrops false");
		Server.sendCommand("gamerule doTileDrops false");
		Server.sendCommand("gamerule mobGriefing false");*/
		//Server.sendCommand("gamerule sendCommandFeedback false");
	/*	Server.sendCommand("gamerule logAdminCommands false");
		Server.sendCommand("gamerule commandBlockOutput false");*/
		
		//Here's where we'd access the script class and run the startup script
		//Tempted to change name of MainThreadFileUpdater and that class entirely really to something saying
		//"FileAccess" as it is more of an access thing.
		
	}
	static void AutoStart(List<String> servArgs) throws IOException, InterruptedException {
		
		new Server(servArgs);
		Server.sendCommand("say MSPLite " + VERSION);
	
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	
	//Console actions, stuff from ConsoleAction() V:

	
	static void help(List<String> cmd) {
		//Not a string. This help menu specifically prints to the main console and therefore no need to do that.
		
		
		try {
			
			getSyntax(cmd.get(1));
			return;
			
		} catch (Exception e) {
			System.out.println("Type \"help <command>\" for more information on a specific command");
			System.out.println("List of commands:"
					//+ "\ncreate"
					//+ "\nstart"
					//+ "\nautostart"
					+ "\nMakeSpawnChunks"
					+ "\nMakeRedstone"
					+ "\nReplaceRedstone"
					+ "\nping"
					+ "\nexit");
		}
		
	}
	static void getSyntax(String command) throws IOException {
		
		switch (command) {
		/*case "create":
			System.out.println("Usage: \"create <server name>\""
					+ "\nCreates a new server with given name");
			break;*/
		/*case "start":
			System.out.println("Usage: \"start <server name>\""
					+ "\nStarts the server that has the given name");
			break;*/
		/*case "autostart":
			System.out.println("Usage: \"autostart <server name | \"RETURN\"| \"REMOVE\">\""
					+ "\nFor server name, Sets the server to be started upon opening of MCServerPal"
					+ "\nFor \"RETURN\", returns the current autostarted server"
					+ "\nFor \"REMOVE\", clears the autostart option for servers (no server will automatically start)");
			break;*/
		case "?":
		case "help": System.out.println("Think about what you've just done."); break;
		case "MakeSpawnChunks":
			System.out.println("Usage: \"MakeSpawnChunks <X>,<Y>,<Z> <Size>\""
					+ "\nFills a region of chunks centered on the chunk containing X Y Z"
					+ "\nin a square with the dimension specified by Size. Sets world spawn to X Y Z"
					+ "\nSecretly it's X Y+1 Z so you don't spawn in a block as if that'd happen");
			break;
		case "MakeRedstone":
			System.out.println("Usage: \"MakeRedstone\""
					+ "\nConverts the redstone text files into a list"
					+ "\nof block set commands then executes them");
			break;
		case "exit":
			System.out.println("Usage: \"exit\""
					+ "\nExits MCServerPal");
			break;
		case "ReplaceRedstone":
			System.out.println("Usage: \"ReplaceRedstone\""
					+ "\nReads the output.txt file to the server (does not go through the process of making it)");
			
			break;
		case "ping":
			System.out.println("Usage: \"ping\""
					+ "\nRequests a response from the server"
					+ "\n(Debug tool to see what console window is tied to which server)");
			break;
			default: output("Not a command");
		
		}
	}
	
	static void makeSpawnChunks(final int x, final int y, final int z, final int size) throws IOException {
		
		int AmtOfChunk = size*size;	//**MIGHT BE UNNECESSARY**
		int lowestRelChunkInt;
		Chunk.setAmtOfChunk(AmtOfChunk); //Probably not going to store that in Chunk (statically)

		lowestRelChunkInt = getLowestRelativeChunkBySize(size);	//X and Z positions of the lowest chunk relative to the Origin, expressed as positive.
		Server.sendCommand("setworldspawn " + x + " " + (y+1)+ " " + z);
		System.out.println(lowestRelChunkInt);
		
		Chunk Origin = new Chunk(x,y,z);
		System.out.println("OrigX: " + Origin.getX() + ", OrigZ:" + Origin.getZ());
		Chunk lowestChunk = new Chunk((Origin.getX() - lowestRelChunkInt)*16, y, (Origin.getZ() - lowestRelChunkInt)*16);	//Starting point for sets
		System.out.println("LowX: " + lowestChunk.getX() + ", LowZ:" + lowestChunk.getZ());
		
		for (int i = 0; i <= (size-1); i++) {
			
			for (int j = 0; j <= (size-1); j++) {
				
				Chunk tempChunk = new Chunk((i + lowestChunk.getX()) * 16, y, (j + lowestChunk.getZ()) * 16);
				//Chunk tempChunk = new Chunk((i*16),y,(j*16));
				tempChunk.fillChunk();
				
			}
			
		}
		/*for (int i = -lowestRelChunkInt; i <= (lowestChunk.getX() + (size-1)); i++) {
			
			for (int j = -lowestRelChunkInt; j <= (lowestChunk.getZ() + (size-1)); j++) {
				
				Chunk tempChunk = new Chunk((i*16),y,(j*16));
				tempChunk.fillChunk();
				
			}
			
		}*/
		
		Server.sendCommand("/setblock " + x + " " + y + " " + z + " gold_block");
		
	}
	static int getLowestRelativeChunkBySize(int size) {
		return ((size-1)/2);
	}
	
	static void MakeRedstone() throws IOException {
		
//		redstoneTxtDir = new File("Redstone");
//		redstoneTxtDir.mkdirs();
		
		File dir = new File(MainThreadFileUpdater.getSetting(mspProps, "RedstoneDir"));
		Main.dbOutput(dir.getAbsolutePath());
		dir.mkdirs();
		
		redstoneOutputFile = new File(dir.getName() + File.separator + "output.txt");
		redstoneOutputFile.createNewFile();
		
		redstoneMainFile = new File(dir.getName() + File.separator + "main.txt");
		redstoneMainFile.createNewFile();
		
		
		try {
			//Calls the constructor of MakeRedstone into action for every file, starting with main.
			
			//List of files that won't be included. I might make this a bit more malleable, i.e read from a file.
			HashSet<String> excludedFiles = new HashSet<String>();
			excludedFiles.add("documentation.txt");
			excludedFiles.add("NOTES.txt");
//			excludedFiles.add("main.txt");
			excludedFiles.add("replaces.txt");
			excludedFiles.add("output.txt");
			
//			----------------------
//			----------------------
			Server.sendCommand("Making redstone...");
			
			MainThreadFileUpdater.clearFile(redstoneOutputFile);
			//note to self: include turning off the sendCommandFeedback here, and turn it on at the end of the file so it isn't so spammy
//			new MakeRedstone(dir, excludedFiles);
			new MakeRedstone(dir, excludedFiles, true);
			MainThreadFileUpdater.writeNoLineBreak(redstoneOutputFile, "say Done!");
			
			Server.sendCommand(MainThreadFileUpdater.read(redstoneOutputFile));
//			----------------------
//			----------------------
//			and here, here we would read from the output file
			
			
			
			/*File mainFile = new File (dir.getAbsolutePath() + File.separator + "main.txt");
			new MakeRedstone(mainFile);
			
			for (File file : dir.listFiles()) {
				
				//would have used hashset but cba.
				if (file.isFile() && excludedFiles.add(file.getName())) {
					new MakeRedstone(file);	//this is the MAKEREDSTONE CLASS constructor
					
				}
			}
			//move onto directories
			for (File file : dir.listFiles()) {
				
				if (file.isDirectory()) {
					MakeRedstone(file);	//this is the MAIN CLASS method
					
				}
			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in creating redstone! Please check the redstone files for problems!");
		}
		
		
	}
	static void replaceRedstone() throws IOException {
		Server.sendCommand(MainThreadFileUpdater.read(redstoneOutputFile));
	}

	
}
