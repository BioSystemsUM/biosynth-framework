package edu.uminho.biosynth.chemanalysis.openbabel;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.util.BioSynthUtilsIO;

public class TestOpenBabelWrapper {

	@BeforeClass
	public static void s() {
		OpenBabelWrapper.initializeLibrary();
	}
	
	@Test
	public void testConvertSmilesToCannonicalSmiles() {
		assertEquals("ClC(=O)CCC(=O)Cl", OpenBabelWrapper.convertSmilesToCannonicalSmiles("C(Cl)(=O)CCC(=O)Cl"));
	}
	
	@Test
	public void testAssertSmilesToCannonicalSmile() throws IOException {
		String smiles1 = "[OH2]";
		String smiles2 = "[H]O[H]";
		assertEquals(true, OpenBabelWrapper.convert(smiles1, "smi", "can").equals(OpenBabelWrapper.convert(smiles2, "smi", "can")));
	}

	@Test
	public void testConvertInchiToCannonicalSmiles() throws IOException {
		String molFile = BioSynthUtilsIO.readFromFile("D:/home/data/kegg/dr/D10511.mol");
		assertEquals("CCCC[C@@H](C(=O)N[C@H](C(=O)N[C@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)N[C@H](C(=O)NCC(=O)N[C@H](C(=O)N1CCC[C@H]1C(=O)N[C@H](C(=O)N)C(C)C)CCCCN)Cc1c[nH]c2c1cccc2)CCCNC(=N)N)Cc1ccccc1)Cc1[nH]cnc1)CCC(=O)O)NC(=O)[C@@H](NC(=O)[C@@H](NC(=O)[C@@H](NC(=O)C)CO)Cc1ccc(cc1)O)CO", 
				OpenBabelWrapper.convertMol2dToSmiles(molFile));
	}
	
	@Test
	public void testConvertInchiToCannonicalSmilesEmptyFile() throws IOException {
		String molFile = BioSynthUtilsIO.readFromFile("D:/home/data/kegg/gl/G00100.mol");
		assertEquals("", 
				OpenBabelWrapper.convertMol2dToSmiles(molFile));
	}
	
	@Test(expected=NullPointerException.class)
	public void testConvertInchiToCannonicalSmilesNullString() throws IOException {
		OpenBabelWrapper.convertMol2dToSmiles(null);
	}

	@Test
	public void testConvertSmilesToInchi() {
		assertEquals("InChI=1S/C4H4Cl2O2/c5-3(7)1-2-4(6)8/h1-2H2", OpenBabelWrapper.convertSmilesToInchi("C(Cl)(=O)CCC(=O)Cl"));
	}
	
	@Test
	public void testConvertMol2dToInchi() throws IOException {
		String molFile = BioSynthUtilsIO.readFromFile("D:/home/data/kegg/cpd/C00001.mol");
		assertEquals("InChI=1S/H2O/h1H2", OpenBabelWrapper.convert(molFile, "mol", "inchi"));
	}
	
	@Test
	public void testConvertMol2dToCannonicalSmiles() throws IOException {
		String molFile = BioSynthUtilsIO.readFromFile("D:/home/data/kegg/cpd/C00001.mol");
		assertEquals("O", OpenBabelWrapper.convert(molFile, "mol", "can"));
	}	
	
}
