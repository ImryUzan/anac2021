package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.usc.ict.iago.agent.RepeatedFavorBehavior.LedgerBehavior;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;
import edu.usc.ict.iago.utils.ServletUtils;



public class RepeatedFavorMessage extends IAGOCoreMessage implements MessagePolicy {
	protected final String[] proposal = {"I think this deal is good for the both of us.", 
			"I think you'll find this offer to be satisfactory.", 
			"I think this arrangement is fair.", 
			"I think this deal will interest you.",
	"Please consider this deal?"};

	protected final String[] acceptResponse = {
			"Great!",
			"Wonderful!",
			"I'm glad we could come to an agreement!",
	"Sounds good!"};

	protected final String[] rejectResponse = {
			"Oh that's too bad.",
			"Ah well, perhaps another time.",
			"Ok, maybe something different next time.",
	"Alright."};

	protected final String[] vhReject = {
			"I'm sorry, but I don't think that's fair to me.",
			"Apologies, but that won't work for me.",
			"Perhaps we should spend more time finding a solution that's good for us both...",
	"I won't be able to accept that.  So sorry. :("};

	protected final String[] vhAccept = {
			"Your offer is good!",
			"That seems like a good deal.",
			"That will work for me.",
	"Yes. This deal will work."};

	protected final String[] vhWaiting = {
			"Hello? Are you still there?",
			"No rush, but are you going to send me an offer?",
			"Can I do anything to help us reach a deal that's good for us both?",
			"I'm sorry, but are you still there?",
	"Can I provide more information to help us reach consensus?",
	"Would you please make an offer?","We should try harder to find a deal that benefits us both.", null};
	
	private String[] vhIdleQuestions = {"Do you like something best?", "So could you tell me about your preferences?"};	//P++ will only use each of these messages once at the game's start before trying other messages

	private boolean isWithholding;
	private boolean lying;
	private RepeatedFavorBehavior.LedgerBehavior lb;
	private AgentUtilsExtension utils;
	private int opponentBATNA = -1;
	private int agentID;
	private GameSpec gs;

	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;
		opponentBATNA = -1;
		agentID = utils.getID();
	}
	
	/***
	 * Constructor for a positive message. The resulting agent can be either withholding or open, lying or honest.
	 * @param isWithholding a boolean representing whether an agent is withholding (true if yes, false if no)
	 * @param lying a boolean representing whether an agent will tell a BATNA lie
	 * @param lb an enum representing how the agent talks about favors
	 * @param gs the gameSpec
	 */
	public RepeatedFavorMessage(boolean isWithholding, boolean lying, RepeatedFavorBehavior.LedgerBehavior lb, GameSpec gs) 
	{
		this.isWithholding = isWithholding;
		this.lying = lying;
		this.lb = lb;
		this.gs = gs;
	}

	@Deprecated
	public void updateOrderings (ArrayList<ArrayList<Integer>> orderings)
	{
		//		this.orderings = orderings;
		return;
	}

	public String getProposalLang(History history, GameSpec game){
		return proposal[(int)(Math.random()*proposal.length)];
	}

	public String getAcceptLang(History history, GameSpec game){
		return acceptResponse[(int)(Math.random()*acceptResponse.length)];
	}

	public String getRejectLang(History history, GameSpec game){
		return rejectResponse[(int)(Math.random()*rejectResponse.length)];
	}

	public String getVHAcceptLang(History history, GameSpec game){
		return vhAccept[(int)(Math.random()*vhAccept.length)];
	}

	public String getVHRejectLang(History history, GameSpec game){
		return vhReject[(int)(Math.random()*vhReject.length)];
	}

	public String getWaitingLang(History history, GameSpec game){
		if(vhIdleQuestions.length != 0) {									//if the questions in this array haven't all been removed, pick another one to use
			int rand = (int)(Math.random()*vhIdleQuestions.length);
			String randQ = vhIdleQuestions[rand];
			ArrayList<String> temp = new ArrayList<String>((List<String>) Arrays.asList(vhIdleQuestions));
			temp.remove(rand);
			Object[] temp2 = temp.toArray();
			vhIdleQuestions = Arrays.copyOf(temp2, temp2.length, String[].class);		//remove questions from this array once used (will eventually be empty at which point P++ randomly picks from vhWaiting below)
			return randQ;
		}
		return vhWaiting[(int)(Math.random()*vhWaiting.length)];
	}

	public boolean getLying(GameSpec game) {
		return lying;
	}

	private String getEmotionResponse(History history, GameSpec game, Event e) {

		if (e.getType() != Event.EventClass.SEND_EXPRESSION)
			throw new UnsupportedOperationException("The last event wasn't an expresion--this method is inappropriate.");
		if(e.getMessage().equals("sad") || e.getMessage().equals("angry"))
			return "What's wrong?";
		else if(e.getMessage().equals("happy"))
			return "Well, at least you're happy!";
		else if(e.getMessage().equals("surprised"))
			return "What, did I surprise you?";
		else if(e.getMessage().equals("neutral"))
			return null;
		return "I don't know what face you just made!";
	}

	protected String getEndOfTimeResponse() {
		return "We're almost out of time!  Accept this quickly!";
	}

	protected String getSemiFairResponse() {
		return "Unfortunately, I cannot accept.  But that's getting close to being fair.";
	}

	protected String getContradictionResponse(String drop) {
		return "I'm sorry.  I must be misunderstanding.  Earlier, you said: " + drop + " Was that not correct?";
	}

	@Override
	public String getMessageResponse(History history, GameSpec game) {
		return null;
	}
	
	@Override
	protected Event getFavorBehavior(History history, GameSpec game, Event e)
	{
		if (lb != LedgerBehavior.NONE && utils.isImportantGame())
			return new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.FAVOR_REQUEST, 
					"Excuse me, but there is a super-valuable item here for me!  If you accept my favor request, I'll promise to pay you back in a future round!",
					(int) (1000*game.getMultiplier()));
		else if (lb != LedgerBehavior.NONE && utils.getLedger() > 0)
			return new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.FAVOR_REQUEST, 
					"Excuse me, but you still owe me a favor.  Accept my favor request, so you can pay me back!",
					(int) (1000*game.getMultiplier()));
		else if (lb != LedgerBehavior.NONE && lb != LedgerBehavior.BETRAYING && utils.getLedger() < 0)
		{
			utils.modifyVerbalLedger(1);
			return new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.FAVOR_ACCEPT, 
					"I think I still owe you a favor!  Let me just pay that back for you.",
					(int) (1000*game.getMultiplier()));
		}
		return null;
	}

	public Event getVerboseMessageResponse(History history, GameSpec game, Event ePrime) {
	
		int randomDelay = new Random().nextInt(2000) + 3000;			//causes message delays to vary in a range between 3-5 seconds to appear more human-like
		int delay = (int) (randomDelay*game.getMultiplier()); 
		int value = -1;
		int issue1 = -1;
		int issue2 = -1;
		Relation relation = null;
		boolean isQuery = false;

		if (ePrime.getType() == Event.EventClass.SEND_EXPRESSION && !game.isMultiAgent())
		{
			String str = getEmotionResponse(history, game, ePrime);
			Event resp = new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.GENERIC_POS, str, delay); 
			return resp;
		} 
		else if (ePrime.getType() == Event.EventClass.SEND_EXPRESSION) 
		{
			return null; // Disables responding to emotions
		}

		if (ePrime.getType() == Event.EventClass.TIME) 
		{
			String str = getWaitingLang(history, game);
			Event resp = new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.TIMING, str, delay);
			return resp; 
		}
		
		//make sure we have a message
		if (ePrime.getType() != Event.EventClass.SEND_MESSAGE)
			return null;

		Preference p = ePrime.getPreference();
		if (p != null) //a preference was expressed
		{
			String str = "";
			if(p.isQuery() && this.isWithholding) 
			{ //asked about preferences

				str = "I don't think it best to reveal my intentions yet. Maybe if you did first...";
				Event resp = new Event(agentID, Event.EventClass.SEND_MESSAGE, Event.SubClass.PREF_WITHHOLD, str, delay);
				return resp;
			} 
		} 
		else 
		{
			ServletUtils.log("No preference detected in user message.", ServletUtils.DebugLevels.DEBUG);
		}

		//details for each response
		Event.SubClass sc = Event.SubClass.NONE;

		int best = utils.findAdversaryItemIndex(game, 1);
		int worst = utils.findAdversaryItemIndex(game, game.getNumberIssues());
		String str = "";



		int offerCount = 0;
		for(Event e: history.getHistory())
			if(e.getType() == Event.EventClass.SEND_OFFER)
				offerCount++;

		boolean isFull = true;
		Event lastOffer = null;
		if (offerCount > 0)
		{
			int index = history.getHistory().size() - 1;
			lastOffer = history.getHistory().get(index);
			while (lastOffer.getType() != Event.EventClass.SEND_OFFER)
			{
				index--;
				lastOffer = history.getHistory().get(index);
			}
			Offer o = lastOffer.getOffer();
			for (int i = 0; i < o.getIssueCount(); i++)
			{
				if(o.getItem(i)[1] != 0)//some undecided items
					isFull = false;
			}
		}
		
		//MAIN RESPONSE
		switch(ePrime.getSubClass())
		{
		case GENERIC_POS: 
			sc = Event.SubClass.PREF_INFO;
			if(best < 0) 
			{ // We do not have any guesses as to their favorite
				str = "I agree!  What is your favorite item?";
				isQuery = true;
				relation = Relation.BEST;
			}
			else 
			{
				str = "I agree!  Why don't we make sure you get your favorite item, and I get mine?  Yours is " + game.getIssuePluralText().get(best) + ", right?";
				issue1 = best;
				relation = Relation.BEST;
				isQuery = true;
			}
			break;
	
		case GENERIC_NEG:
			str = "I'm sorry, have I done something wrong?  I'm just trying to make sure we both get the things that make us the most happy.";
			sc = Event.SubClass.GENERIC_NEG;

			if(!isFull)
				str += "  Besides, what about the rest of the undecided items?";

			break;
		case OFFER_REJECT:
			sc = Event.SubClass.GENERIC_POS;
			if (offerCount > 0)
			{
				int avgPlayerValue = (Math.abs(utils.adversaryValueMax(lastOffer.getOffer()) - utils.adversaryValueMin(lastOffer.getOffer())))/2;
				if (Math.abs(utils.myActualOfferValue(lastOffer.getOffer()) - avgPlayerValue) > game.getNumberIssues() * 2)
				{
					str =  "Ok, I understand.  I do wish we could come up with something that is a more even split though.";
					if (best >= 0 && worst >= 0) 
					{
						str += "  Isn't it true that you like " + game.getIssuePluralText().get(best) + " best and " + game.getIssuePluralText().get(worst) + " least?";
						sc = Event.SubClass.PREF_REQUEST;
						issue1 = best;
						relation = Relation.BEST;
						isQuery = true;
						
					}
				}
				else
					str = "Ok, I understand.  This seems like a fairly even split.";
			}
			else
				str = "But... there haven't even been any offers!";

			if(!isFull)
				str += "  Also, what about the rest of the undecided items?";
			
			break;	
		case TIMING: //note: agent responds to this, but this event no longer is a user available action
			sc = Event.SubClass.GENERIC_POS;
			int time = 0;
			int index = history.getHistory().size() - 1;
			index = index < 1 ? 1 : index;
			if (history.getHistory().size() > 1)
			{
				Event lastTime = history.getHistory().get(index);
				while (lastTime.getType() != Event.EventClass.TIME && index > 0)
				{
					index--;
					lastTime = history.getHistory().get(index);
				}

				if(lastTime == null || lastTime.getMessage() == null || lastTime.getMessage().equals(""))
					break;
				time = (int)Double.parseDouble(lastTime.getMessage());
				time = game.getTotalTime() - time;

				int min = time / 60;
				int sec = time % 60;

				str = "There is currently " + min + " minute" + (min == 1 ? "" : "s") + " and " + sec + " seconds remaining.";

				if (min > 0)
				{
					str += "  Don't worry.  We've still got a bit more time to negotiate.";
				}
				else
				{
					int secondBest = utils.findAdversaryItemIndex(game, 2);
					int suggest2 = best >= 0 ? best : (int)(Math.random() * game.getNumberIssues());
					int suggest3 = secondBest >= 0 ? secondBest : (int)(Math.random() * game.getNumberIssues());
					if (suggest3 == suggest2)
						suggest3  = (suggest3 + 1) % game.getNumberIssues();
					str += "  AHH!  You're right!  Let's just split it like this: you get all the " + game.getIssuePluralText().get(suggest2) + " and the " 
							+ game.getIssuePluralText().get(suggest3) + " and I get the remainder.";
					sc = Event.SubClass.OFFER_PROPOSE;
				}
			}
			break;
		case OFFER_REQUEST_POS:
		case OFFER_REQUEST_NEG:
			str = "Alright, what do you think of this?";
			sc = Event.SubClass.OFFER_PROPOSE;
			break;
		case THREAT_POS: // "I'm sorry but I think I'm going to have to walk away.");
		case THREAT_NEG: // "This is a warning, I'm about to walk away."
			int loweredBATNA = utils.lowerBATNA(utils.myPresentedBATNA);
			if(loweredBATNA != utils.myPresentedBATNA) 
			{
				utils.myPresentedBATNA = utils.lowerBATNA(utils.myPresentedBATNA);
				str = "Well, maybe I can accept a little less. Would you be able to give me at least " + utils.myPresentedBATNA + " points?";
				value = utils.myPresentedBATNA;
				sc = Event.SubClass.BATNA_INFO;
			}
			else if (!utils.conflictBATNA(utils.myPresentedBATNA, opponentBATNA))
			{
				str = "Please don't go yet! Maybe we can still make a deal. Would you mind reminding me what you would like?";
				sc = Event.SubClass.BATNA_REQUEST;
			}
			else {
				str = "Oh well, I guess we really should walk away. Are you sure you can't accept anything less than " + utils.adversaryBATNA + " points?";
				sc = Event.SubClass.BATNA_REQUEST;
			}
			break;
		case PREF_INFO:
		case PREF_REQUEST:
		case PREF_SPECIFIC_REQUEST:	
		case PREF_WITHHOLD:
			sc = Event.SubClass.PREF_INFO;
			if (p == null && !isWithholding)
			{
				p = utils.randomPref();
				issue1 = p.getIssue1();
				issue2 = p.getIssue2();
				relation = p.getRelation();
				isQuery = false;

			} 
			else if (p == null && isWithholding) {
				str = "I don't think it best to reveal my intentions yet. Maybe if you did first...";
				sc = Event.SubClass.PREF_WITHHOLD;
				break;
			}
			if (p.getRelation() == Relation.BEST)
			{
				issue1 = utils.findMyItemIndex(game, 1);
				issue2 = -1;
				relation = Relation.BEST;
				isQuery = false;
				str = "I like " + utils.getSpec().getIssuePluralText().get(issue1) + " the best.";

			}
			else if (p.getRelation() == Relation.WORST)
			{
				issue1 = utils.findMyItemIndex(game, game.getNumberIssues());
				issue2 = -1;
				relation = Relation.WORST;
				isQuery = false;
				str = "I like " + utils.getSpec().getIssuePluralText().get(issue1) + " the least.";
			}
			else
			{
				if(p.getIssue1() == -1 || p.getIssue2() == -1) {
					str = "Can you be a little more specific? Saying \"something\" is confusing.";
					sc = Event.SubClass.PREF_SPECIFIC_REQUEST;
				} else {
					int value1 = game.getSimplePoints(agentID).get(game.getIssuePluralText().get(p.getIssue1()));
					int value2 = game.getSimplePoints(agentID).get(game.getIssuePluralText().get(p.getIssue2()));
					issue1 = p.getIssue1();
					issue2 = p.getIssue2();
					if(value1 > value2)
						relation = Relation.GREATER_THAN;
					else if (value2 > value1)
						relation = Relation.LESS_THAN;
					else
						relation = Relation.EQUAL;
					str = prefToEnglish(new Preference(p.getIssue1(), p.getIssue2(), relation, false), game);
					isQuery = false;
				}
			}
			break;
		//case OFFER_REJECT: 
		//	sc = Event.SubClass.GENERIC_NEG;
		//	str = this.getRejectLang(history, game);
		//	break;
		case OFFER_ACCEPT:
			sc = Event.SubClass.GENERIC_POS;
			str = this.getAcceptLang(history, game);
			break;
		case BATNA_INFO:
			if (ePrime.getValue() != -1)
			{
				utils.adversaryBATNA = ePrime.getValue();
				if (!utils.conflictBATNA(utils.myPresentedBATNA, utils.adversaryBATNA))
				{	
					if(opponentBATNA != utils.adversaryBATNA && opponentBATNA != -1) 
						str = "Oh it is? I thought you needed more than " + opponentBATNA + " points. ";


					opponentBATNA =  utils.adversaryBATNA;
					//TODO here is a good opportunity to improve the system so that it doesn't repeat this same info too often!
					
					//str += "In case you forgot, I already have an offer for " + utils.myPresentedBATNA + " points, so anything that gets me more than " 
					//		+ utils.myPresentedBATNA + " points will do.";
					str += "Thank you for the information.  That's helpful.";
					
					value = utils.myPresentedBATNA;
					sc = Event.SubClass.GENERIC_POS;
				} 
				else
				{
					opponentBATNA =  utils.adversaryBATNA;
					str = "Well, since you can't accept anything less than " + utils.adversaryBATNA + " points and I can't accept anything that gets me less than " 
							+ utils.myPresentedBATNA + " points, I don't think we'll be able to make a deal. Maybe we should just walk away.";
					value = utils.myPresentedBATNA; 
					sc = Event.SubClass.THREAT_POS;
				}
			}
			else {
				ServletUtils.log("No BATNA value found", ServletUtils.DebugLevels.DEBUG);
				str = "I don't know what message you just sent!";
			}

			break;
		case BATNA_REQUEST:
			str += "I already have an offer for " + utils.myPresentedBATNA + " points, so anything that gets me more than " 
					+ utils.myPresentedBATNA + " points will do.";
			value = utils.myPresentedBATNA;
			sc = Event.SubClass.BATNA_INFO;
			break;
		case CONFUSION:
			str += "I'm sorry, have I said something confusing? I didn't mean to.";
			sc = Event.SubClass.GENERIC_POS;
			break;
		case FAVOR_ACCEPT:
			str += "Oh wonderful!  I will make sure to pay you back in the next game!";
			utils.modifyVerbalLedger(-1);
			sc = Event.SubClass.FAVOR_ACCEPT;
			break;
		case FAVOR_REJECT:
			str += "Oh blast!  And this was so important to me this round too...";
			sc = Event.SubClass.GENERIC_NEG;
			break;
		case FAVOR_REQUEST:
			boolean paysLedger = (lb == LedgerBehavior.FAIR || lb == LedgerBehavior.LIMITED);
			if(lb == LedgerBehavior.NONE)
			{
				str += "I don't really do favors.";
				sc = Event.SubClass.FAVOR_REJECT;
			}
			else if(utils.isImportantGame())
			{
				str += "Oh I'm sorry, but items this game are worth so much to me...";
				sc = Event.SubClass.FAVOR_REJECT;			
			}
			else if (paysLedger && utils.getLedger() < 0)
			{
				str += "Sure, since you did me that favor before, I'm happy to help this round.";
				utils.modifyVerbalLedger(1);
				sc = Event.SubClass.FAVOR_ACCEPT;				
			}
			else if (!paysLedger && utils.getLedger() < 0)
			{
				str += "Hmm.  I don't really feel like it.";
				sc = Event.SubClass.FAVOR_REJECT;	
			}
			else if (utils.getLedger() == 0)
			{
				str += "Sure, but you'll owe me one, ok?";
				utils.modifyVerbalLedger(1);
				sc = Event.SubClass.FAVOR_ACCEPT;	
			}
			else //(utils.getTotalLedger()> 0)
			{
				str += "No way!  You still owe me from before...";
				sc = Event.SubClass.FAVOR_REJECT;	
			}			
			break;
		case FAVOR_RETURN:
			if (utils.getLedger() > 0)//note: agent has no way of knowing if you're being honest
			{
				str += "Thanks for returning the favor from before!";
				utils.modifyVerbalLedger(-1);
				sc = Event.SubClass.FAVOR_ACCEPT;
			}
			else
			{
				str += "Well, you're welcome I guess... but I don't think you really owed me!";
				sc = Event.SubClass.GENERIC_POS;
			}
			break;
		case NONE:
			ServletUtils.log("Agent didn't have a subclass to respond to...", ServletUtils.DebugLevels.DEBUG);
			return null;
		case OFFER_PROPOSE: //impossible to accomplish, human can't do this
			return null;
		default:
			return null;
		}

		Event resp;
		if (value != -1) {
			resp = new Event(agentID, Event.EventClass.SEND_MESSAGE, sc, value, str, delay);				
		} else {
			resp = new Event(agentID, Event.EventClass.SEND_MESSAGE, sc, str, delay);	
		}

		if (relation != null) {
			resp.encodePreferenceData(new Preference(issue1, issue2, relation, isQuery), gs);
		}
		return resp; 

	}


}