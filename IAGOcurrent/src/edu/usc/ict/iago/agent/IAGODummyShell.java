package edu.usc.ict.iago.agent;

import java.util.LinkedList;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;
import edu.usc.ict.iago.utils.History;

public class IAGODummyShell extends GeneralVH 
{

	public IAGODummyShell(String username, GameSpec game, Session session) {
		super(username, game, session);
	}

	@Override
	public LinkedList<Event> getEventResponse(Event e) {
		LinkedList<Event> elist = new LinkedList<Event>();
		if (e.getOwner() != History.SYSTEM_ID && e.getType() != Event.EventClass.TIME) {
			elist.add(e);
		}
		return elist;
	}

	@Override
	public String getArtName() {
		return "Brad";
	}

	@Override
	public String agentDescription() {
		return "Your Partner";
	}

}
