package pt.uminho.sysbio.biosynth.integration.etl;

import edu.uminho.biosynth.core.components.Reaction;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class DefaultReactionEtlLoad<R extends Reaction> implements EtlLoad<R> {

	private final ReactionDao<R> reactionDao;
	
	public DefaultReactionEtlLoad(ReactionDao<R> reactionDao) {
		this.reactionDao = reactionDao;
	}
	
	@Override
	public void etlLoad(R reaction) {
		this.reactionDao.saveReaction(reaction);
	}

}
