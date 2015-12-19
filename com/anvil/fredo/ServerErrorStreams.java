package com.anvil.fredo;

//Deprecated

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;

public class ServerErrorStreams extends Thread implements Runnable {

	Thread t;
	String threadName;
	BufferedReader br;
	BufferedWriter bw;
	OutputInterpret oi;

	String errorOutput;
	
	boolean running;
	
	public ServerErrorStreams(Process p, String name) {
		
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		threadName = name;
	
	}
	
	public void run() {
		
		String output;
		
		while (running) {
			
			
			try {
				
				//This is probably not the best way to handle the errorstream but hey, it's going somewhere!
				errorOutput = br.readLine();
				//Main.dbOutput(errorOutput);
				
				
				//Main.dbOutput("EOT check");
				
				//Main.dbOutput(Boolean.toString(Server.p.isAlive()));
				if (!Server.p.isAlive()) {
					//Please don't continue using this! This is a very hashed out copy!!!
					Server.stopServer(false);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Something has gone terribly wrong! But I don't know what!");
			}

		}
			
		
		return;
		
	}
	
	void Output(String msg) {
		
		System.out.println("[ServerErrorStream]: " + msg);
	}
	
	public void start( ) {
		this.running = true;
		
		
		if (t==null) {
			t = new Thread (this, threadName);
			t.start();
		}
		
	}
	
	public void terminate() {
		this.running = false;
	}
	
}
