package edu.uminho.biosynth.program;

import java.io.IOException;

import edu.uminho.biosynth.chemanalysis.openbabel.OpenBabelWrapper;

public class InchiToCan {
	public static void main(String args[]) throws IOException {
		if (args.length < 1) {
			System.out.println("Missing args");
			return;
		}
		
		OpenBabelWrapper.initializeLibrary();
		System.out.println(OpenBabelWrapper.convert((args[0]), "inchi", "can"));
	}
}
