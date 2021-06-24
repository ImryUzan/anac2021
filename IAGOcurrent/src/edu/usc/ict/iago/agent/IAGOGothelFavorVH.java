package edu.usc.ict.iago.agent;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.GameSpec;


/**
 * @author mell
 * 
 */
public class IAGOGothelFavorVH extends IAGOCoreVH {

	/**
	 * @author mell
	 * Instantiates a new vh
	 *
	 * @param name: agent's name
	 * @param game: gamespec value
	 * @param session: the session
	 */
	public IAGOGothelFavorVH(String name, GameSpec game, Session session)
	{
		super("GothelFavor", game, session, new RepeatedFavorBehavior(RepeatedFavorBehavior.LedgerBehavior.BETRAYING), new RepeatedFavorExpression(), 
				new RepeatedFavorMessage(false, false, RepeatedFavorBehavior.LedgerBehavior.BETRAYING, game));	
		
		super.safeForMultiAgent = false;
	}

	@Override
	public String getArtName() {
		return "Rens";
	}

	@Override
	public String agentDescription() {
			return "<h1>Opponent</h1><p>They are excited to begin negotiating!</p>";
	}
}