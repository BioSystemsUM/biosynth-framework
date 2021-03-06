package pt.uminho.sysbio.biosynthframework.chemanalysis;

import net.sf.jniinchi.INCHI_KEY;

import org.openbabel.OBConversion;
import org.openbabel.OBMol;

import pt.uminho.sysbio.biosynthframework.chemanalysis.domain.ChemicalStructureCore;
import pt.uminho.sysbio.biosynthframework.chemanalysis.inchi.JniInchi;
import pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel.OpenBabelWrapper;

public class ChemicalStructureCoreBuilder {
	
	public static ChemicalStructureCore build(String data, String type) {
		ChemicalStructureCore core = new ChemicalStructureCore();
		OBConversion conv = new OBConversion();
		OBMol mol = new OBMol();
		conv.SetInFormat(type);
		conv.ReadString(mol, data);
		core.setFormula(mol.GetFormula());
		String can = OpenBabelWrapper.convert(data, type, "can");
		String inchi = OpenBabelWrapper.convert(data, type, "inchi");
		String inchikey = JniInchi.getInchiKeyFromInchi(inchi);
		core.setCan(can);
		core.setInchi(inchi);
		core.setInchiKey(inchikey);
		if (!JniInchi.LAST_CALL_STATUS.equals(INCHI_KEY.OK)) {
			core.setInchi(JniInchi.LAST_CALL_STATUS.toString());
			core.setInchiKey(JniInchi.LAST_CALL_STATUS.toString());
		}
		return core;
	}
}
