package edu.usc.ict.iago.agent;

import java.util.LinkedList;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.Event.EventClass;

public abstract class IAGOCoreVH extends GeneralVH
{
	private Offer lastOfferReceived;
	private Offer lastOfferSent;
	private Offer favorOffer; //the favor offer
	private boolean favorOfferIncoming = false; //marked when an offer is about to be sent that is a favor
	private IAGOCoreBehavior behavior;
	private IAGOCoreExpression expression;
	private IAGOCoreMessage messages;
	private AgentUtilsExtension utils;
	private boolean timeFlag = false;
	private boolean firstFlag = false;
	private int noResponse = 0;
	private boolean noResponseFlag = false;
	private boolean firstGame = true;
	private boolean disable = false;	//adding a disabler check to help with agent vs. P++ functionality (note this will only be added to corevh and not the ++ version)
	private int currentGameCount = 0;
	private Ledger myLedger = new Ledger();
	
	private class Ledger
	{
		int ledgerValue = 0; //favors are added to the final ledger iff the offer was accepted, otherwise discarded.  Positive means agent has conducted a favor.
		int verbalLedger = 0; //favors get added in here via verbal agreement.  Positive means agent has agreed to grant favors.
		int offerLedger = 0;  //favors are moved here and consumed from verbal when offer is made.  Positive means agent has made a favorable offer.
	}
	


	/**
	 * Constructor for most VHs used by IAGO.
	 * @param name name of the agent. NOTE: Please give your agent a unique name. Do not copy from the default names, such as "Pinocchio,"
	 * or use the name of one of the character models. An agent's name and getArtName do not need to match.
	 * @param game the GameSpec that the agent plays in first.
	 * @param session the web session that the agent will be active in.
	 * @param behavior Every core agent needs a behavior extending CoreBehavior.
	 * @param expression Every core agent needs an expression extending CoreExpression.
	 * @param messages Every core agent needs a message extending CoreMessage.
	 */
	public IAGOCoreVH(String name, GameSpec game, Session session, IAGOCoreBehavior behavior,
			IAGOCoreExpression expression, IAGOCoreMessage messages)
	{
		super(name, game, session);

		AgentUtilsExtension aue = new AgentUtilsExtension(this);
		aue.configureGame(game);
		this.utils = aue;
		this.expression = expression;
		this.messages = messages;
		this.behavior = behavior;
		aue.setAgentBelief(this.behavior.getAgentBelief());

		this.messages.setUtils(utils);
		this.behavior.setUtils(utils);
	}
	
	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent.  Can be negative.  Persists across games.
	 * @return the ledger
	 */
	protected int getLedger()
	{
		return myLedger.ledgerValue;
	}
	
	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent, including all pending values.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	protected int getTotalLedger()
	{
		return myLedger.ledgerValue + myLedger.offerLedger + myLedger.verbalLedger;
	}
	
	/**
	 * Returns a simple int representing the potential "ledger" of favors verbally agreed to.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	protected int getVerbalLedger()
	{
		return myLedger.verbalLedger;
	}
	
	/**
	 * Allows you to modify the agent's internal "ledger" of verbal favors done for it.  
	 * @param increment value (negative ok)
	 */
	protected void modifyVerbalLedger(int increment)
	{
		ServletUtils.log("Verbal Event! Previous Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);		
		
		myLedger.verbalLedger += increment;
		
		ServletUtils.log("Verbal Event! Current Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);	
	}
	
	/**
	 * Allows you to modify the agent's internal "ledger" of offer favors proposed.  
	 * @param increment value (negative ok)
	 */
	protected void modifyOfferLedger(int increment)
	{
		ServletUtils.log("Offer Event! Previous Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);		
		
		myLedger.verbalLedger -= increment;
		myLedger.offerLedger += increment;
		
		favorOfferIncoming = true;
		
		ServletUtils.log("Offer Event! Current Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);	
	}
	
	/**
	 * Allows you to modify the agent's internal "ledger" of offer favors actually executed.  
	 * Automatically takes the execution from the offerLedger.
	 */
	private void modifyLedger()
	{
		ServletUtils.log("Finalization Event! Previous Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);		
		
		myLedger.ledgerValue += myLedger.offerLedger;
		myLedger.offerLedger = 0;
		
		ServletUtils.log("Finalization Event! Current Ledger: v:" + myLedger.verbalLedger + ", o:" + myLedger.offerLedger + ", main:" + myLedger.ledgerValue, ServletUtils.DebugLevels.DEBUG);
	}
	
	/**
	 * Returns a simple int representing the current game count. 
	 * @return the game number (starting with 1)
	 */
	protected int getGameCount()
	{
		return currentGameCount;
	}
	

	/**
	 * Agents work by responding to various events. This method describes how core agents go about selecting their responses.
	 * @param e the Event that the agent will respond to.
	 * @return a list of Events in response to the initial Event.
	 */
	@Override
	public LinkedList<Event> getEventResponse(Event e)
	{
		LinkedList<Event> resp = new LinkedList<Event>();
		/**what to do when the game has changed -- this is only necessary because our AUE needs to be updated.
			Game, the current GameSpec from our superclass has been automatically changed!
			IMPORTANT: between GAME_END and GAME_START, the gameSpec stored in the superclass is undefined.
			Furthermore, attempting to access data that is decipherable with a previous gameSpec could lead to exceptions!
			For example, attempting to decipher an offer from Game 1 while in Game 2 could be a problem (what if Game 1 had 4 issues, but Game 2 only has 3?)
			You should always treat the current GameSpec as true (following a GAME_START) and store any useful metadata about past games yourself.
		 **/

		if(e.getType().equals(Event.EventClass.GAME_START)) 
		{		
			currentGameCount++;
			ServletUtils.log("Game number is now " + currentGameCount + "... reconfiguring!", ServletUtils.DebugLevels.DEBUG);
			AgentUtilsExtension aue = new AgentUtilsExtension(this);
			aue.configureGame(game);
			this.utils = aue;
			this.messages.setUtils(utils);
			this.behavior.setUtils(utils);	
			this.disable = false;

			//if we wanted, we could change our Policies between games... but not easily.  Probably don't do that.  Just don't.

			//we should also reset some things
			timeFlag = false;
			firstFlag = false;
			noResponse = 0;
			noResponseFlag = false;
			myLedger.offerLedger = 0;
			myLedger.verbalLedger = 0;
			
			boolean liar = messages.getLying(game);
			utils.myPresentedBATNA = utils.getLyingBATNA(game, utils.LIE_THRESHOLD, liar);

			if(!firstGame)
			{
				String newGameMessage = "It's good to see you again!  Let's get ready to negotiate again.";
				Event e0 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.NONE, newGameMessage, (int) (100*game.getMultiplier()));
				resp.add(e0);
				return resp;
			}
			firstGame = false;
		}
		
		
		
		 if(e.getType().equals(Event.EventClass.OFFER_IN_PROGRESS)) 
		{	
			disable = true;
			ServletUtils.log("Agent is currently being restrained", ServletUtils.DebugLevels.DEBUG);
			return resp;
		}
		 

		//should we lead with an offer?
		if(!firstFlag && !this.disable)
		{
			ServletUtils.log("First offer being made.", ServletUtils.DebugLevels.DEBUG);
			firstFlag = true;
			Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getFirstOffer(getHistory()), 0); 
			if(e2.getOffer() != null)
			{
				ServletUtils.log("First offer isn't null.", ServletUtils.DebugLevels.DEBUG);
				Event e3 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
				resp.add(e3);
				Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.NONE, messages.getProposalLangFirst(),  (int) (1000*game.getMultiplier()));
				resp.add(e4);
				lastOfferSent = e2.getOffer();
				if(favorOfferIncoming)
				{
					favorOffer = lastOfferSent;
					favorOfferIncoming = false;
				}
				resp.add(e2);
			}

			String revealBATNA = "Just so you know, I already have an offer for " + utils.myPresentedBATNA + " points, so I won't accept anything less.   What about you?";
			Event e5 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.BATNA_INFO, utils.myPresentedBATNA, revealBATNA,  (int) (1000*game.getMultiplier()));
			resp.add(e5);
			disable = false;
			
			Event e6 = messages.getFavorBehavior(getHistory(), game, e);
			
			if (e6 != null && (e6.getType() == EventClass.OFFER_IN_PROGRESS || e6.getSubClass() == Event.SubClass.FAVOR_ACCEPT)) 
			{
				Event e7 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (700*game.getMultiplier())); 
				if (e7.getOffer() != null)
				{
					String s1 = messages.getProposalLang(getHistory(), game);
					Event e8 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, s1, (int) (2000*game.getMultiplier()));
					resp.add(e6);
					resp.add(e7);
					resp.add(e8);
					this.lastOfferSent = e7.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
				} 
			}
			else 
			{
				resp.add(e6);
			}

		}

		//what to do when player sends an expression -- react to it with text and our own expression
		if(e.getType().equals(Event.EventClass.SEND_EXPRESSION))
		{
			String expr = expression.getExpression(getHistory());
			if (expr != null)
			{
				Event e1 = new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expr, 2000, (int) (700*game.getMultiplier()));
				resp.add(e1);
			}
			Event e0 = messages.getVerboseMessageResponse(getHistory(), game, e);
			if (e0 != null && (e0.getType() == EventClass.OFFER_IN_PROGRESS || e0.getSubClass() == Event.SubClass.FAVOR_ACCEPT))
			{
				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (700*game.getMultiplier())); 
				if (e2.getOffer() != null) 
				{
					String s1 = messages.getProposalLang(getHistory(), game);
					Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, s1, (int) (2000*game.getMultiplier()));
					resp.add(e0);
					resp.add(e1);
					resp.add(e2);
					this.lastOfferSent = e2.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
				}
			} 
			else if (e0 != null) 
			{ 
				resp.add(e0);
			}
			disable = false;
			return resp;
		}

		// When to formally accept when player sends an incoming formal acceptance
		if(e.getType().equals(Event.EventClass.FORMAL_ACCEPT))
		{
			Event lastOffer = utils.lastEvent(getHistory().getHistory(), Event.EventClass.SEND_OFFER);
			Event lastTime = utils.lastEvent(getHistory().getHistory(), Event.EventClass.TIME);
			
			disable = false;
			
			int totalItems = 0;
			for (int i = 0; i < game.getNumberIssues(); i++)
				totalItems += game.getIssueQuantities().get(i);
			if(lastOffer != null && lastTime != null)
			{
				//approximation based on distributive case
				int fairSplit = ((game.getNumberIssues() + 1) * totalItems / 4);
				//down to the wire, accept anything better than BATNA (less than 30 seconds from finishing time)
				if(utils.myActualOfferValue(lastOffer.getOffer()) > game.getBATNA(getID()) && Integer.parseInt(lastTime.getMessage()) + 30 > game.getTotalTime()) 
				{
					Event e0 = new Event(this.getID(), Event.EventClass.FORMAL_ACCEPT, 0);
					resp.add(e0);
					return resp;
				}
				//accept anything better than fair minus margin

				if (behavior instanceof IAGOCompetitiveBehavior)
				{
					if(((IAGOCompetitiveBehavior) behavior).acceptOffer(lastOffer.getOffer())){
						Event e0 = new Event(this.getID(), Event.EventClass.FORMAL_ACCEPT, 0);
						resp.add(e0);
						return resp;
					}
				}
				else if(utils.myActualOfferValue(lastOffer.getOffer()) > fairSplit - behavior.getAcceptMargin()) //accept anything better than fair minus margin
				{
					Event e0 = new Event(this.getID(), Event.EventClass.FORMAL_ACCEPT, 0);
					resp.add(e0);
					return resp;
				}
				else
				{
					Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_REJECT, messages.getRejectLang(getHistory(), game), (int) (700*game.getMultiplier()));
					resp.add(e1);
					behavior.updateAdverseEvents(1);
					return resp;					
				}
			}
		}

		//what to do with delays on the part of the other player
		if(e.getType().equals(Event.EventClass.TIME))
		{
			noResponse += 1;
			for(int i = getHistory().getHistory().size() - 1 ; i > 0 && i > getHistory().getHistory().size() - 6; i--)//if something from anyone for four time intervals
			{
				Event e1 = getHistory().getHistory().get(i);
				if(e1.getType() != Event.EventClass.TIME) 
					noResponse = 0;
			}

			if(noResponse >= 2)
			{
				Event e0 = messages.getVerboseMessageResponse(getHistory(), game, e);
				// Theoretically, this block isn't necessary. Event e0 should just be a message returned by getWaitingLang
				if (e0 != null && (e0.getType() == EventClass.OFFER_IN_PROGRESS || e0.getSubClass() == Event.SubClass.FAVOR_ACCEPT)) 
				{
					Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (700*game.getMultiplier())); 
					if (e2.getOffer() != null)
					{
						String s1 = messages.getProposalLang(getHistory(), game);
						Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, s1, (int) (2000*game.getMultiplier()));
						resp.add(e0);
						resp.add(e1);
						resp.add(e2);
						this.lastOfferSent = e2.getOffer();
						if(favorOfferIncoming)
						{
							favorOffer = lastOfferSent;
							favorOfferIncoming = false;
						}
					} 
				}
				else 
				{
					resp.add(e0);
				}

				noResponseFlag = true;
			}
			else if(noResponse >= 1 && noResponseFlag)
			{
				noResponseFlag = false;
				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getTimingOffer(getHistory()), 0); 
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game),  (int) (1000*game.getMultiplier()));
					resp.add(e4);
					resp.add(e2);
				}
			}

			// Times up
			if(!timeFlag && game.getTotalTime() - Integer.parseInt(e.getMessage()) < 30)
			{
				timeFlag = true;
				Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.TIMING, messages.getEndOfTimeResponse(), (int) (700*game.getMultiplier()));
				resp.add(e1);
				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getFinalOffer(getHistory()), 0); 
				if(e2.getOffer() != null)
				{
					resp.add(e2);
					lastOfferSent = e2.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
				}
			}

			// At 90 second, computer agent will send prompt for user to talk about preferences
			if (e.getMessage().equals("90") && this.getID() == History.OPPONENT_ID) 
			{
				String str = "By the way, will you tell me a little about your preferences?";
				Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.PREF_REQUEST, str, (int) (1000*game.getMultiplier()));
				e1.setFlushable(false);
				resp.add(e1);
			}

			return resp;
		}

		//what to do when the player sends an offer
		if(e.getType().equals(Event.EventClass.SEND_OFFER))
		{
			ServletUtils.log("Agent Normalized ordering: " + utils.getMyOrdering(), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Optimal ordering: " + utils.getMinimaxOrdering(), ServletUtils.DebugLevels.DEBUG);

			boolean firstOffer = false;
			if (this.lastOfferReceived == null)
				firstOffer = true;
				
			Offer o = e.getOffer();//incoming offer
			this.lastOfferReceived = o; 

			boolean localFair = false;
			boolean totalFair = false;
			boolean localEqual = false;

			Offer allocated = behavior.getAllocated();//what we've already agreed on
			Offer conceded = behavior.getConceded();//what the agent has agreed on internally
			ServletUtils.log("Allocated Agent Value: " + utils.myActualOfferValue(allocated), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Conceded Agent Value: " + utils.myActualOfferValue(conceded), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Offered Agent Value: " + utils.myActualOfferValue(o), ServletUtils.DebugLevels.DEBUG);
			int playerDiff = (utils.adversaryValue(o, utils.getMinimaxOrdering()) - utils.adversaryValue(allocated, utils.getMinimaxOrdering()));
			ServletUtils.log("Player Difference: " + playerDiff, ServletUtils.DebugLevels.DEBUG);

			if(utils.myActualOfferValue(o) >= utils.myActualOfferValue(allocated))
			{//net positive (o is a better offer than allocated)
				int myValue = utils.myActualOfferValue(o) - utils.myActualOfferValue(allocated) + behavior.getAcceptMargin();
				ServletUtils.log("My target: " + myValue, ServletUtils.DebugLevels.DEBUG);
				int opponentValue = utils.adversaryValue(o, utils.getMinimaxOrdering()) - utils.adversaryValue(allocated, utils.getMinimaxOrdering());
				if(myValue > opponentValue)
					localFair = true;//offer improvement is within one max value item of the same for me and my opponent
				else if (myValue == opponentValue && !firstOffer)
					localEqual = true;
			}

			if (behavior instanceof IAGOCompetitiveBehavior) 
			{
				totalFair = ((IAGOCompetitiveBehavior) behavior).acceptOffer(o);
			}
			else if(utils.myActualOfferValue(o) + behavior.getAcceptMargin() > utils.adversaryValue(o, utils.getMinimaxOrdering()))
				totalFair = true;//total offer still fair
			
			//totalFair too hard, so always set to true here
			totalFair = true;

			if (localFair && !totalFair)
			{
				String expr = expression.getSemiFairEmotion();
				if (expr != null)
				{
					Event eExpr = new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expr, 2000, (int) (700*game.getMultiplier()));
					resp.add(eExpr);
				}
				Event e0 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_REJECT, messages.getSemiFairResponse(), (int) (700*game.getMultiplier()));
				resp.add(e0);
				behavior.updateAdverseEvents(1);
				Event e3 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()),  (int) (700*game.getMultiplier()));
				if(e3.getOffer() != null)
				{
					Event e1 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e1);
					Event e2 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game),  (int) (3000*game.getMultiplier()));
					resp.add(e2);
					this.lastOfferSent = e3.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
					resp.add(e3);
				}
			}
			else if(localFair && totalFair)
			{
				String expr = expression.getFairEmotion();
				if (expr != null) 
				{
					Event eExpr = new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expr, 2000, (int) (700*game.getMultiplier()));
					resp.add(eExpr);
				}
				Event e0 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_ACCEPT, messages.getVHAcceptLang(getHistory(), game), (int) (700*game.getMultiplier()));

				resp.add(e0);
				ServletUtils.log("ACCEPTED OFFER!", ServletUtils.DebugLevels.DEBUG);
				behavior.updateAllocated(this.lastOfferReceived);

				Event eFinalize = new Event(this.getID(), Event.EventClass.FORMAL_ACCEPT, 0);
				if(utils.isFullOffer(o))
					resp.add(eFinalize);
			}
			else
			{
				String expr = expression.getUnfairEmotion();
				if (expr != null) 
				{
					Event eExpr = new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expr, 2000, (int) (700*game.getMultiplier()));
					resp.add(eExpr);
				}
				Event e0 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_REJECT, messages.getVHRejectLang(getHistory(), game), (int) (700*game.getMultiplier()));
				//slightly alter the language
				if (localEqual)
					e0 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_REJECT, messages.getVHEqualLang(), (int) (700*game.getMultiplier()));
				resp.add(e0);	
				behavior.updateAdverseEvents(1);
				Event e3 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (700*game.getMultiplier()));
				if(e3.getOffer() != null)
				{
					Event e1 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e1);
					Event e2 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game),  (int) (3000*game.getMultiplier()));
					resp.add(e2);
					this.lastOfferSent = e3.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
					resp.add(e3);
				}
			}
			
			
			return resp;
		}

		//what to do when the player sends a message (including offer acceptances and rejections)
		if(e.getType().equals(Event.EventClass.SEND_MESSAGE))
		{
			Preference p;
			if (e.getPreference() == null) 
			{
				p = null;
			} else {
				p = new Preference(e.getPreference().getIssue1(), e.getPreference().getIssue2(), e.getPreference().getRelation(), e.getPreference().isQuery());
			}
			
			if (p != null && !p.isQuery()) //a preference was expressed
			{
				utils.addPref(p);
				if(utils.reconcileContradictions())
				{
					//we simply drop the oldest expressed preference until we are reconciled.  This is not the best method, as it may not be the the most efficient route.
					LinkedList<String> dropped = new LinkedList<String>();
					dropped.add(IAGOCoreMessage.prefToEnglish(utils.dequeuePref(), game));
					int overflowCount = 0;
					while(utils.reconcileContradictions() && overflowCount < 5)
					{
						dropped.add(IAGOCoreMessage.prefToEnglish(utils.dequeuePref(), game));
						overflowCount++;
					}
					String drop = "";
					for (String s: dropped)
						drop += "\"" + s + "\", and ";

					drop = drop.substring(0, drop.length() - 6);//remove last 'and'

					Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.CONFUSION,
							messages.getContradictionResponse(drop), (int) (2000*game.getMultiplier()));
					e1.setFlushable(false);
					resp.add(e1);
				}
			}

			String expr = expression.getExpression(getHistory());
			if (expr != null) 
			{
				Event e0 = new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expr, 2000, (int) (700*game.getMultiplier()));
				resp.add(e0);
			}

		
			Event e0 = messages.getVerboseMessageResponse(getHistory(), game, e);
			if (e0 != null && (e0.getType() == EventClass.OFFER_IN_PROGRESS || e0.getSubClass() == Event.SubClass.FAVOR_ACCEPT)) 
			{
				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (700*game.getMultiplier()));
				if (e2.getOffer() != null) 
				{
					String s1 = messages.getProposalLang(getHistory(), game);
					Event e1 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, s1, (int) (2000*game.getMultiplier()));
					resp.add(e0);
					resp.add(e1);
					resp.add(e2);
					this.lastOfferSent = e2.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
				} 
			} else {
				resp.add(e0);
			}


			if(behavior instanceof IAGOCompetitiveBehavior && e.getSubClass() == Event.SubClass.BATNA_INFO)
			{
				((IAGOCompetitiveBehavior)behavior).resetConcessionCurve();
			}

			boolean offerRequested = (e.getSubClass() == Event.SubClass.OFFER_REQUEST_NEG || e.getSubClass() == Event.SubClass.OFFER_REQUEST_POS);
						
			if(offerRequested)
			{	
				if(utils.adversaryBATNA + utils.myPresentedBATNA > utils.getMaxPossiblePoints()) 
				{
					String walkAway = "Actually, I won't be able to offer you anything that gives you " + utils.adversaryBATNA + " points. I think I'm going to have to walk "
							+ "away, unless you were lying.";
					Event e2 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.THREAT_NEG, utils.adversaryBATNA, walkAway, (int) (2000*game.getMultiplier()));
					resp.add(e2);
				}
				else 
				{
					Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), (int) (3000*game.getMultiplier()));
					if (e2.getOffer() == null) 
					{
						String response = "Actually, I'm having trouble finding an offer that works for both of us.";
						Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.THREAT_POS, response, (int) (2000*game.getMultiplier()));
						if (utils.adversaryBATNA != -1) 
						{
							response += " Are you sure you can't accept anything less than " + utils.adversaryBATNA + " points?";
							e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.BATNA_REQUEST, utils.adversaryBATNA, response, (int) (2000*game.getMultiplier()));
						}					
						resp.add(e4);
						ServletUtils.log("Null Offer", ServletUtils.DebugLevels.DEBUG);
					}
					else 
					{
						Event e3 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
						resp.add(e3);

						Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game), (int) (1000*game.getMultiplier()));
						resp.add(e4);
						this.lastOfferSent = e2.getOffer();
						if(favorOfferIncoming)
						{
							favorOffer = lastOfferSent;
							favorOfferIncoming = false;
						}
						resp.add(e2);

					}
				}
			}

			if(e.getSubClass() == Event.SubClass.OFFER_ACCEPT)//offer accepted
			{
				if(this.lastOfferSent != null)
				{
					behavior.updateAllocated(this.lastOfferSent);
					if(lastOfferSent.equals(favorOffer))
						modifyLedger();
					else
						myLedger.offerLedger = 0;
				}

				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getAcceptOfferFollowup(getHistory()), (int) (3000*game.getMultiplier()));
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game), (int) (1000*game.getMultiplier()));
					resp.add(e4);
					this.lastOfferSent = e2.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}
					resp.add(e2);		
				}
			}

			if(e.getSubClass() == Event.SubClass.OFFER_REJECT)//offer rejected
			{	
				behavior.updateAdverseEvents(1);
				myLedger.offerLedger = 0;

				Event e2 = new Event(this.getID(), Event.EventClass.SEND_OFFER, behavior.getRejectOfferFollowup(getHistory()), (int) (3000*game.getMultiplier()));
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(this.getID(), Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.OFFER_PROPOSE, messages.getProposalLang(getHistory(), game), (int) (1000*game.getMultiplier()));
					resp.add(e4);
					this.lastOfferSent = e2.getOffer();
					if(favorOfferIncoming)
					{
						favorOffer = lastOfferSent;
						favorOfferIncoming = false;
					}

					resp.add(e2);	
				}
			}
			return resp;
		}
		return resp;
	}

	/**
	 * Every agent needs a name to select the art that will be used. Currently only 4 names are supported: Brad, Ellie, Rens, and Laura.
	 * Note: This is NOT the name that users will see when they negotiate with the agent.
	 * @return the name of the character model to be used. If this does not match "Brad", "Ellie", "Rens", or "Laura", one of those names
	 * will be selected as a default. Please use one of those names.
	 */
	@Override
	public abstract String getArtName();

	/**
	 * This is the method that dictates what users read when they negotiate with your agent. An agent developer can implement
	 * this method to say anything they want.
	 * @return The message that users see when negotiating with the agent.
	 */
}
