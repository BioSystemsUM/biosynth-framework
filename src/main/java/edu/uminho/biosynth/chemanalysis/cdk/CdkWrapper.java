package edu.uminho.biosynth.chemanalysis.cdk;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * 
 * @author Filipe
 *
 */
public class CdkWrapper {

	/**
	 * Wraps the CDK MolecularFormulaManipulator getString method. Uses the 
	 * {@link org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator#getMajorIsotopeMolecularFormula(String, IChemObjectBuilder) 
	 * getMajorIsotopeMolecularFormula} to build an molecular formula data structure. 
	 * Returns the string representation of the molecule formula. Based on Hill System.
	 * 
	 * @param formula the molecular formula as string
	 * @param setOne True, when must be set the value 1 for elements with one atom
	 * @return Returns the string representation of the molecule formula. Based on Hill System.
	 * @see {@link org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator#getString(IMolecularFormula, boolean)}
	 */
	public static String convertToIsotopeMolecularFormula(String formula, boolean setOne) {
		IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
		IMolecularFormula molecularFormula = MolecularFormulaManipulator
				.getMajorIsotopeMolecularFormula(formula, builder);

		String ret = MolecularFormulaManipulator.getString(molecularFormula, setOne);

		return ret;
	}
}
