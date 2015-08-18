package pt.uminho.sysbio.biosynthframework.core.data.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;

@Deprecated
public abstract class AbstractMetaboliteService<T extends GenericMetabolite> 
	implements IMetaboliteService<T> {
	
	protected IGenericDao dao;
	protected Class<T> metaboliteClass;
	protected String serviceId;
	
	public AbstractMetaboliteService(IGenericDao dao, Class<T> metaboliteClass) {
		this.dao = dao;
		this.metaboliteClass = metaboliteClass;
	}
	
	public Class<T> getMetaboliteClass() { return metaboliteClass; }

	public String getServiceId() { return serviceId;}
	public void setServiceId(String serviceId) { this.serviceId = serviceId;}

	@Override
	public T getMetaboliteById(int id) { 
		return this.dao.find(metaboliteClass, id);
	};
	
	@Override
	public T getMetaboliteByEntry(String entry) {
//		System.out.println(metaboliteClass);
		List<T> result = this.dao.criteria(metaboliteClass, Restrictions.eq("entry", entry));
		if (result == null || result.size() < 1) return null;
		if (result.size() > 1) throw new RuntimeException("Unique field error, duplicate entry " + entry);
		return result.iterator().next();
	};
	
	@Override
	public List<T> getAllMetabolites() {
		return dao.findAll(metaboliteClass);
	}

	@Override
	public int countNumberOfMetabolites() {
		throw new RuntimeException("Not yet implemented !");
	}
	
	@Override
	public List<String> getAllMetabolitesEntries() {
		List<String> res = new ArrayList<> ();
		
		for (T cpd : dao.findAll(metaboliteClass)) {
			res.add(cpd.getEntry());
		}
//				dao.projection(String.class, Projections.property("entry"));
//		for (String r : res) {
//			System.out.println(r + " + " + r.getClass().toString());
//		}
//		
//		dao.cre
//		dao.criteria(metaboliteClass, Projections.property("entry"));
		return res;
	}
}
