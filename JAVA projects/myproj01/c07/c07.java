package c07;

import java.util.*;

class Note {
	private int i;
	private Note(int val) { i= val;};
	public static final Note
	middleC = new Note(1),
	Csharp = new Note(2);
	}

class Instrument {
	public void play( Note n ) {
		System.out.println("Instrument play");
	}
}

class Wind extends Instrument {
	public void play( Note n ){
		System.out.println("Wind play");
	}
}

class Music {
	public static void tune( Instrument Inst ){
		Inst.play(Note.middleC);
	}
	public static void main(String[] arg){
		Wind flute = new Wind();
		tune(flute);
	}
}