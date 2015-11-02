//MSPLite
package com.anvil.fredo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/*	For some unknown reason, the torun.jar is looking at folders in the SCI folder, not the Server folder. I don't know why.
 * Like, I really don't know.. :V
 * 
 */

//Right I think we can store the servers in a List or Set, to reference 'em. I hope. Yeah we can!
//For MSPLite we only are using ONE server meaning no need for any lists.

//@SuppressWarnings("unused")
public class Server {
	
	static OutputThread OT;
	static ServerErrorStreams EOT;
	
	static PrintWriter cmdSend;
	static Process p;
	
	boolean running;
	
	
	//Yo dawg, don't put everything into a constructor. We need to make the output method separate.
	
//	(LEGACY)
	public Server(String jarname) throws IOException, InterruptedException {

		//What this class should do:
		//-Start the server
		//-Set up a thread for output stream
		
		running = true;
		startServer(jarname);
		
		System.out.println("Server has been started.");
	}
	public Server(List<String> servArgs) throws IOException, InterruptedException {

		//What this class should do:
		//-Start the server
		//-Set up a thread for output stream
		
		running = true;
		startServer(servArgs);
		
		System.out.println("Server has been started.");
	}

//	(LEGACY)
	//Im not sure where exactly Im starting the server with the changed working directory, or maybe Im not idk.	
	static void startServer(String jarname) throws IOException {
		
		File dir = new File(jarname);
		System.out.println(dir.getAbsolutePath());
		//ProcessBuilder server = new ProcessBuilder("java","-Xms1024M","-Xmx1024M","-jar", dir.getAbsolutePath()/*,"nogui"*/);	//nogui only if we print the input stream to the console which we won't.
		ProcessBuilder server = new ProcessBuilder("java","-server","-d64","-Xmx1024M","-Xms1024M","-jar", dir.getAbsolutePath(),"nogui");
		//It's possible that in ProcessBuilder we might just be able to put jarname in place of dir.getAbsolutePath or
		//at least not use the absolute path
		//System.out.println("directory: " + server.directory()); //<(REMOVE)debug should be null
		//We need to be able to support more arguments for running the jar
		//What I might do is add an "arguments" text file that will be put in place of the ("java","-jar",...) stuff
		//with default memory options and it will enable people to use their own arguments
		
		
		
		p = server.start();
		cmdSend = new PrintWriter(p.getOutputStream());
		
		
		OT = new OutputThread(p, "Output Thread");
		OT.start();
		
		EOT = new ServerErrorStreams(p, "Error Output Thread");
		EOT.start();
		
//		^^^^^^^^ Removed for Alpha 1.0
//		adedd back for, you know, it was a bit silly to remove
	}
	static void startServer(List<String> servArgs) throws IOException {
		
//		File dir = new File(jarname);
//		System.out.println(dir.getAbsolutePath());
		//ProcessBuilder server = new ProcessBuilder("java","-Xms1024M","-Xmx1024M","-jar", dir.getAbsolutePath()/*,"nogui"*/);	//nogui only if we print the input stream to the console which we won't.
//		ProcessBuilder server = new ProcessBuilder("java","-server","-d64","-Xmx1024M","-Xms1024M","-jar", dir.getAbsolutePath(),"nogui");
		
		ProcessBuilder server = new ProcessBuilder(servArgs);
		
		//It's possible that in ProcessBuilder we might just be able to put jarname in place of dir.getAbsolutePath or
		//at least not use the absolute path
		//System.out.println("directory: " + server.directory()); //<(REMOVE)debug should be null
		//We need to be able to support more arguments for running the jar
		//What I might do is add an "arguments" text file that will be put in place of the ("java","-jar",...) stuff
		//with default memory options and it will enable people to use their own arguments
		
		
		server.redirectErrorStream(true);
		p = server.start();
		cmdSend = new PrintWriter(p.getOutputStream());
		
//		System.out.println(p.isAlive());
		
		OT = new OutputThread(p, "Output Thread");
		OT.start();
		
//		EOT = new ServerErrorStreams(p, "Error Output Thread");
//		EOT.start();
		
//		^^^^^^^^ Removed for Alpha 1.0
//		adedd back for, you know, it was a bit silly to remove
	}
	
	static void stopServer() throws IOException {
		
//		EOT.terminate();
		OT.terminate();
		
		if (p.isAlive()) {
			sendCommand("stop");
		}
		
		
	}
	
	static public void sendCommand(String command) {
		
		cmdSend.write(command);
		cmdSend.write("\n");
		cmdSend.flush();
		
	}
	
}
