//MSPLite
package com.anvil.fredo;

import java.io.File;
import java.io.IOException;

public class Script {
	
	private FileUpdater SUpdater;
	private File scriptFile;
	static private File scriptStorage;
	
	public Script(String fileName) throws IOException {
		fileName = fileName.endsWith(".txt") ? fileName : fileName.concat(".txt");	//If fileName ends in.txt, remain the same, else concatenate .txt
		SUpdater = new FileUpdater();
		scriptFile = new File(scriptStorage.getName() + File.separator + fileName);
		Main.dbOutput(scriptFile.getAbsolutePath());
	}
	
	public void run() throws IOException {
		try {
			Server.sendCommand(SUpdater.read(scriptFile));
		}
		catch (Exception e) {
			Main.dbOutput("Most likely the script file that access was attempted is empty.");
		}
	}
	
	static public void setScriptStorage(String path) {
		scriptStorage = new File(path);
		scriptStorage.mkdirs();
	}
	
	public File getFile() {
		return scriptFile;
	}
}
