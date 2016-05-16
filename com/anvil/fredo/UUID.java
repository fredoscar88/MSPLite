package com.anvil.fredo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * USING FOLLOWING JSON LIBRARY
 * http://www.java2s.com/Code/JarDownload/java/java-json.jar.zip
 * 
 */

public class UUID {

	String UUID;
	String playerName;
	JSONObject jso;
	
	URL url;
	URLConnection urlCon;
	BufferedReader br;
	
	
	public UUID(String input) throws JSONException, IOException {
		
		//Input is a username
		if (input.length() <= 16) {
			this.playerName = input;
			this.UUID = findByName(input);
			Main.dbOutput("You've reached USERNAME");
		}
		//Input is a UUID
		else {
			Main.dbOutput("You've reached UUID");
			this.UUID = input;
			this.playerName = findByUUID(input);
		}
		
		jso = new JSONObject();
		jso.put("name", this.playerName);
		jso.put("id", this.UUID);
		
	}
	
	private String findByName(String name) throws IOException, JSONException {
		
		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			urlCon = url.openConnection();
			
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			
			jso = new JSONObject(br.readLine());
			
		}
		catch (Exception e) {
			if (e instanceof NullPointerException) {
				
				return "There is no user with this username";
				
			}
			else {
				return "Unknown error occurred";
			}
		}
		return jso.getString("id");
		
	}
	
	private String findByUUID(String UUID) throws JSONException, IOException {
		
		try {
			url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID);
			urlCon = url.openConnection();
			
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			jso = new JSONObject(br.readLine());
			
		}
		catch (Exception e) {
			if (e instanceof NullPointerException) {
				
				return "User not found";
				
			}
			else {
				return "Unknown error occurred";
			}
		}
		
		
		return jso.getString("name");
		
	}
	
	public JSONObject getData() {
		
		return this.jso;
		
	}
}
