package com.anvil.fredo;

import java.io.IOException;

public class BlockFirst extends Block {

	public BlockFirst(String cbCommand, int type, boolean conditional)
			throws IOException {
		super(cbCommand, type, conditional);
		// TODO Auto-generated constructor stub
	}

/*
	//(TODO) Hey guess what, we need to have a function in makeredstone that sets the pattern in Block, 
	//because we have to replace that function in the legacy code (the old Block main constructor, that one set the PAT)
	
	//implicit constructor
	public BlockFirst(String cbCommand, boolean called) {
		super(cbCommand, called);
	}
	
	//explicit constructor
	public BlockFirst(int[] startCoords, int[] pattern, String cbCommand, boolean called) {
		super(startCoords, pattern, cbCommand, called);
	}
	
	//Hey you, store the location of each repeating block please, we need it later to start/stop the system (TODO)
*/
}
