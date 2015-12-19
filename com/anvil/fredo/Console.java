//MSPLite
package com.anvil.fredo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//THIS IS WHERE WE MODULARIZE THE CONSOLE, BUT NOT NOW. FOR NOW IM JUST GOING TO MAKE A NEW ONE IN SERVER AND SAVE THIS SHIT FOR LATER.


public class Console {

	Scanner ConsoleReader;
	List<String> cmd;
	String ConsoleInput;
	String Parse;
	String ConsoleCmd; //May be deprecated
	
	String ConsoleStartMessage;
	
	public Console(String name) {
		ConsoleStartMessage = name;
		
		/*System.out.println(ConsoleStartMessage);
		System.out.println("Type \"?\" or \"help\" for help.");*/
		
	}
	
	//Please note: WHEN we move over to a UI we won't be handling input to the console the same way.
	//It will come from a text field rather than the console itself, and will output to the same pane that
	//The server is outputting from, prefixed probably by [MCServerPal]
	//We probably don't have to change much in this class, thank goodness. All we change is how we input to
	//this class.
	List<String> ConsoleRun() throws IOException, InterruptedException {
		ConsoleReader = new Scanner(System.in);
		
		if (ConsoleInput == null) {
			
			System.out.print(": ");
			ConsoleInput = ConsoleReader.nextLine();
			//ConsoleInput = ConsoleInput.toLowerCase();
				
			ConsoleParseCmd(ConsoleInput);	//soon... (divides the string into a list of the commands syntax)
			ConsoleInput = null;	//For when we use the string divvier
			//ConsoleParse(ConsoleInput);	//Here is where the string should be divied up
			// ^ Deprecated
				
		} else {
			ConsoleParseCmd(ConsoleInput);
		}
		
		/*if (cmd.get(0).equals("stop")) {	//Ehhhhhhhhhhhhhhh maybe not in this class
			System.out.println("The main thread has been stopped! I decided not to opt to returning to just running the command console");
			canRun = false;
		}*/
		
		return cmd;
	}
	
	List<String> ConsoleRun (String cmdInput) throws IOException, InterruptedException {
		
		ConsoleInput = cmdInput;
		
		return ConsoleRun();
	}
	
	//Not sure how much I like how this is void. I feel it should be List<String>
	private void ConsoleParseCmd(String ConsoleInput) {
		
		boolean jRun;
		boolean iRun;
		//System.out.println("CPC ran");
		cmd = new ArrayList<String>();
		
		try {
			iRun = true;
			for (int i =0; iRun; i++) {
				jRun=true;
				for (int j = 0; jRun; j++) {
					if (ConsoleInput.replace(" ", "_").equals(ConsoleInput)) {
						cmd.add(ConsoleInput);
						iRun = false;
						jRun = false;
					}
					
					if (ConsoleInput.substring(j, j+1).equals(" ")) {
						cmd.add(/*i, */ConsoleInput.substring(0, j));
						ConsoleInput = ConsoleInput.substring(j+1);
						jRun = false;
					}
					
				}
				
			}
			
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println("My conjecture is the substring ran when it shouldn't have, in Console"
					+ "\nHopefully this caught exception won't force the program into crashing");
			cmd = new ArrayList<String>();
			cmd.add("help");
		}
		
	}
	
	public List<String> PConsoleParse(String ServerInput) {
		List<String> temp;
		
		boolean jRun;
		boolean iRun;
		//System.out.println("CPC ran");
		temp = new ArrayList<String>();
		
		try {
			iRun = true;
			
			for (int i =0; iRun; i++) {
				jRun=true;
				for (int j = 0; jRun; j++) {
					if (ServerInput.replace(" ", "_").equals(ServerInput)) {
						temp.add(ServerInput);
						iRun = false;
						jRun = false;
						return temp;
					}
					
					if (ServerInput.substring(j, j+1).equals(" ")) {
						temp.add(/*i, */ServerInput.substring(0, j));
						ServerInput = ServerInput.substring(j+1);
						jRun = false;
					}
					
				}
				
			}
			
		} finally {
			/*System.out.println(cmd.size());
			for (int i = 0;i < cmd.size();i++) {
				System.out.print(cmd.get(i) + " ");
			}
			System.out.println();*/
		}
		
		return null;
	}

	public String PConsoleStitch(List<String> cmd) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0 ;i < cmd.size(); i++) {
			sb.append(" ");
			sb.append(cmd.get(i));

		}
//		sb.deleteCharAt(sb.length());
		
		return sb.toString().trim();
	}
	
}

//All of the below is blah. Atm we're gunna change it to where any command can grab the syntax from the console. It's not the best solution (for ex. it doesn't really allow for multiple option flags, that would require constant parsing (i.e "--<optionname>" flags))
		//Here, we'd send ConsoleCmd to be checked against a list of command words to perform console actions.
		//This is dependent on the command or action word preceding it. Some action words incorporate the succeeding 
		//words so whatever method reads these and performs actions will account for that. I think I should save this
		//console stuff somewhere :I
		//Action words that don't require succeeding words should wipe ConsoleInput to null, and invalid input should
		//inform the user of the invalid command.
		//For text adventure games we might need something different, something also capable of unlocking new command
		//words, with an adaptive help menu
