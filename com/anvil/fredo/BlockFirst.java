package com.anvil.fredo;

public class BlockFirst extends Block {


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
	

}
