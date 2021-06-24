package edu.usc.ict.iago.agent;

import javax.websocket.Session;

import edu.usc.ict.iago.agent.RepeatedFavorBehavior.LedgerBehavior;
import edu.usc.ict.iago.utils.GameSpec;

public class IAGOTemplateVH extends IAGOCoreVH {
	
	
	public IAGOTemplateVH(String name, GameSpec game, Session session)
	{
		super("Template", game, session, new IAGOConcedingBehavior(), new RepeatedFavorExpression(), 
				new RepeatedFavorMessage(false, false, LedgerBehavior.FAIR, game));	
		
	}

	@Override
	public String getArtName() {
		return "Brad";
	}
	
	@Override
	public String agentDescription() {
		return "<h1>Template class</h1>";
	}
	

}