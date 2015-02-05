package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum ReactionRelationshipType implements RelationshipType {
	has_name, 
	has_crossreference_to, 
	instance_of, 
	has_ec_number, stoichiometry, left_component, right_component, in_pathway,
	has_orthology, InEnzymaticReaction,
//	Left, Right,
}
