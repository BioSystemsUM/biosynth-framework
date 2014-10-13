package pt.uminho.sysbio.data.test.rule;

import org.hibernate.SessionFactory;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public abstract class MetaboliteFactoryRule<M extends GenericMetabolite> implements MethodRule{

	private MetaboliteDao<M> metaboliteDao;
	
	public MetaboliteDao<M> getMetaboliteDao() {
		return metaboliteDao;
	}
	
	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				
				
				try {
					base.evaluate();
				} finally {
					
				}
			}
		};
	}
	
	public abstract MetaboliteDao<M> buildMetaboliteDao(SessionFactory sessionFactory);
}
