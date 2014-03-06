package edu.uminho.biosynth.core.data.io.dao.bigg;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class CsvBiggMetaboliteDaoImpl implements IMetaboliteDao<BiggMetaboliteEntity>{

	private File biggMetaboliteTsv;
	
	public File getBiggMetaboliteTsv() {
		return biggMetaboliteTsv;
	}

	public void setBiggMetaboliteTsv(File biggMetaboliteTsv) {
		this.biggMetaboliteTsv = biggMetaboliteTsv;
	}

	@Override
	public BiggMetaboliteEntity find(Serializable id) {
		for (BiggMetaboliteEntity c : this.findAll()) {
			if (c.getEntry().equals(id)) return c;
		}
		return null;
	}

	@Override
	public List<BiggMetaboliteEntity> findAll() {
		List<BiggMetaboliteEntity> cpdList = null;
		try {
			cpdList = DefaultBiggMetaboliteParser.parseMetabolites(biggMetaboliteTsv);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpdList;
	}

	@Override
	public Serializable save(BiggMetaboliteEntity entity) {
		throw new RuntimeException("Not Supported Operation");
	}
}
