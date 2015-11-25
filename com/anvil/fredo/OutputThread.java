//MSPLite
package com.anvil.fredo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class OutputThread extends Thread implements Runnable {

	Thread t;
	String threadName;
	BufferedReader br;
	BufferedWriter bw;
	OutputInterpret oi;
	
	
	boolean running;
	
	public OutputThread(Process p, String name) {
		
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		threadName = name;
	
		
		
	}
	
	public void run() {
		
		String output;
		
		while (running) {
			
			
			//((output = br.readLine()) != null); what we formally used in a while loop, 
			
			try {
				//So it should be noted that this works not quite as expected. I expected it to jump to the catch case 
				//if no output was present but what I think is happening is br.readLine() returns null, which doesn't trigger
				//an exception. Will test.
				//That is exactly what was happening. I've got it working how I want it now.
				output = br.readLine();
				try {
					oi.Interpret(output);
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				//OutputInterpret here!
				
//				Main.dbOutput("OT check");
//				Main.dbOutput(Boolean.toString(Server.p.isAlive()));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Something has gone terribly wrong! But I don't know what!");
			}

		}
		
		
		return;
		
	}
	
	
	
	
	public void start( ) {
		this.running = true;
		
		
		oi = new OutputInterpret();
		
		if (t==null) {
			t = new Thread (this, threadName);
			t.start();
		}
		
	}
	
	public void terminate() {
		this.running = false;
	}
	
	/*public void start() {
		
		if (t == null) {
			
			t = new Thread(this);
			t.start();
			
		}
		
	}*/
	
}
