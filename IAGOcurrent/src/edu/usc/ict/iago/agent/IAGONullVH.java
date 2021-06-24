package edu.usc.ict.iago.agent;

import java.util.LinkedList;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;

/**
 * The point of this rather uninspired class is to grant the user a "sandbox".  In it, the user can play around with IAGO's buttons,
 * offer tables, and other functionality without the bother of having a reponsive partner.  It is used in tutorials.
 * @author jmell
 *
 */
public class IAGONullVH extends GeneralVH {

	public IAGONullVH(String name, GameSpec game, Session session)
	{
		super("NullAgent", game, session);	
		super.safeForMultiAgent = true;
	}

	@Override
	public String getArtName() {
		return "Missing";
	}

	@Override
	public String agentDescription() {
		return null;
	}

	@Override
	public LinkedList<Event> getEventResponse(Event e) {
		return null;
	}


}