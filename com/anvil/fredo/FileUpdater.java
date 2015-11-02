package com.anvil.fredo;

//WE NEED:
/*
 * -A method to add settings to the file (like in player rank)
 */

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.RandomAccessFile;

public class FileUpdater {

	//Each thread gets its own FileUpdater to avoid A) creating a file updater for every file and
	//B) so we can go quasi-static I guess
	//File file;
	BufferedWriter writer;
	BufferedReader reader;
	
	String line;
	StringBuilder sb;
	String fileReadout;
	String tempFile;
	String tempSetting;
	
	//kek we don't even need the IOException
	public FileUpdater() /*throws IOException */{
		
		//reader = new BufferedReader(new FileReader(file));
		//Given the nature of reading files it makes sense to have it be reset each time I want to read the
		//file
	}
	
	public void clearFile(File file) throws IOException {

		writer = new BufferedWriter(new FileWriter(file));
		writer.write("say Inserting redstone!\n");
		writer.flush();
		writer.close();
		
	}
	
	public void write(File file, String toWrite) throws IOException {
		
		
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(toWrite);
		writer.write("\n");
		writer.flush();
	}
	public void writeNoLineBreak(File file, String toWrite) throws IOException {
		
		
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(toWrite);
		writer.flush();
	}
	public void writeNewLineBreak(File file) throws IOException {
		
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.write("\n");
		writer.flush();
	}
	
	public String read(File file) throws IOException {
		//Returns entire file
		reader = new BufferedReader(new FileReader(file));
		
		try {
			sb = new StringBuilder();
			line = reader.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = reader.readLine();	//Eventually this will throw an exception, leading us to the finally
			}
			fileReadout = sb.toString();
			
		} finally {
			reader.close();
		}
		
		return fileReadout;
		
	}
	
	public String getSetting(File file, String setting) throws IOException {
		//Returns the given setting.
		/*
		 * ex. if in a file we have line "PotatoesFarmed=164"
		 * we can call getSetting with the parameters (theFile, "PotatoesFarmed")
		 * and it will return 164 (whatever is after the = sign)
		 */
		
		reader = new BufferedReader(new FileReader(file));
		
		try {
			
			while (true) {
				line = reader.readLine();
				
				if (line.startsWith(setting)) {
					fileReadout = line.substring(setting.length()+1);	//Plus 1 because each setting has an = sign after it that needs to be disregarded
					//(TODO) ^While that above will work, we should replace it with .lastIndexOf("=") as that is probably a smarter way of doing it
					
					if (fileReadout.equals("")) return null;
					
					return fileReadout;
				}
			}
			
			
		} catch (Exception e) {
			return null;	//Only returned if this is used incorrectly.

		} 
		
	}
	
	public void changeSetting(File file, String setting, String changeTo) throws IOException {
		//Changes a setting in a file
		/*
		 * ex. if in a file we have line "PotatoesFarmed=164"
		 * we can call changeSetting with the parameters (theFile, "PotatoesFarmed", "165")
		 * and it will rewrite the file, changing the setting to "PotatoesFarmed=165"
		 */
		
		tempSetting = getSetting(file, setting);	//Gets the setting that we are replacing
		if (tempSetting == null) tempSetting = "";	//Sets to an empty string, because if the setting returns null that's because it didn't find anything, represented by an empty string.
		
		tempFile = read(file);						//Returns the file's content in string form
		
		tempFile = tempFile.replace(setting + "=" + tempSetting, setting + "=" + changeTo);
		//Replaces the setting with what we want to change it to
		
		//I actually have a method in this file to do this but I guess Im reserving it for outsider usage?
		writer = new BufferedWriter(new FileWriter(file, false));	//False because we are not adding onto the file
		writer.write(tempFile);		//Writes the new file
		writer.flush();
		//writer.close();
		//NOTE TO SELF IT MAY BE THAT WHEN WE WRITE FROM THE BEGINNING OF THE FILE WE MAY NOT REMOVE ALL REMNAMTS OF THE OLD SETTING! (TODO)
	}
	
}
