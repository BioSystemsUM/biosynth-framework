package edu.uminho.biosynth.core.data.io.dao.seed;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;

import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class SeedMetaboliteDaoImpl implements IGenericDao{

	@Override
	public <T> T find(Class<T> type, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] find(Class<T> type, Serializable... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getReference(Class<T> type, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] getReferences(Class<T> type, Serializable... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable[] save(Object... entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(Object... entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeById(Class<?> type, Serializable id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeByIds(Class<?> type, Serializable... ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> List<T> findAll(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttached(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh(Object... entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> List<T> criteria(Class<T> type, Criterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> query(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdate(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> List<T> projection(Class<T> type, Projection projection) {
		// TODO Auto-generated method stub
		return null;
	}

}
