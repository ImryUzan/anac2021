package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.Preference.Relation;

class AgentUtilsExtension 
{
	private IAGOCoreVH agent;
	private GameSpec game;
	private ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
	private int[][] permutations;
	private LinkedList<Preference> preferences = new LinkedList<Preference>();
	public int adversaryBATNA = -1;
	public int myPresentedBATNA = -1;
	public final double LIE_THRESHOLD = 0.6; 	
	protected ArrayList<Boolean>  previouslyOffered = new ArrayList<Boolean>();
	public boolean competitive = false;
	public int myRow;
	public int adversaryRow;
	private boolean isFixedPie = false;

	/**
	 * Constructor for the AUE.
	 * @param core The VH associated with this instance of AUE.
	 */
	AgentUtilsExtension(IAGOCoreVH core){
		this.agent = core;
		
		if (this.agent.getID() == History.USER_ID) 
		{
			myRow = 2;
			adversaryRow = 0;
		} else if (this.agent.getID() == History.OPPONENT_ID) 
		{
			myRow = 0;
			adversaryRow = 2;
		}
	}
	
	/**
	 * Configures initial parameters for the given game.
	 * @param game the game being played.
	 */
	protected void configureGame(GameSpec game)
	{
		this.game = game;
		permutations = MathUtils.getPermutations(game.getNumberIssues(), 1);//offset by 1, so we will be 1-indexed
		for (int i = 0; i < game.getNumberIssues(); i++)
			previouslyOffered.add(false);
	}
	
	/**
	 * Sets the agent belief for when multiple opponent orderings are equally likely.  When true, it assumes it has the same preferences.  When false, it does not.
	 * @param fixedpie true if fixed pie belief is in effect, false otherwise (if method is not called, defaults to false)
	 */
	protected void setAgentBelief(boolean fixedpie)
	{
		isFixedPie = fixedpie;
	}
	
	/**
	 * returns the GameSpec being used in the current game.
	 * @return the GameSpec currently being played.
	 */
	protected GameSpec getSpec()
	{
		return game;
	}
	
	/**
	 * Adds the given preference to the list of preferences.
	 * @param p the preference to add
	 */
	protected void addPref (Preference p)
	{
		preferences.add(p);
	}
	
	/**
	 * Removes the 0th element in the preferences queue.
	 * @return the preference removed, or throws IndexOutOfBoundException 
	 */
	protected Preference dequeuePref()
	{
		return preferences.remove(0);
	}
	
	/**
	 * Returns the value of an offer with respect to the caller. 
	 * @param o the offer
	 * @return the total value (how many points the agent will get)
	 */
	protected int myActualOfferValue(Offer o) 
	{
		int ans = 0;
		for (int num = 0; num < game.getNumberIssues(); num++)
			ans += o.getItem(num)[myRow] * game.getSimplePoints(agent.getID()).get(game.getIssuePluralText().get(num));
		return ans;
	}
	
	/**
	 * Returns the VH value of an ordering (of preferences?).
	 * @param o the ordering
	 * @return the total value
	 */
	protected int myActualOrderValue(ArrayList<Integer> o) 
	{
		int ans = 0;
		for (int num = 0; num < game.getNumberIssues(); num++)
			ans += o.get(num) * game.getSimplePoints(agent.getID()).get(game.getIssuePluralText().get(num));
		return ans;
	}
	
	/**
	 * Check to see if the offer is a full offer.
	 * @param o the offer
	 * @return is full offer
	 */
	protected boolean isFullOffer(Offer o)
	{
		boolean ans = true;
		for (int num = 0; num < game.getNumberIssues(); num++)
			if(o.getItem(num)[1] > 0)
				ans = false;
		return ans;
	}

	/**
	 * Returns the normalized ordering of VH preferences (e.g., a point value of {3, 7, 2} would return {2, 1, 3}), with 1 being the highest
	 * @return an ArrayList of preferences
	 */
	protected ArrayList<Integer> getMyOrdering() 
	{
		int rating = 1;
		ArrayList<Integer> ans = new ArrayList<Integer>(game.getNumberIssues());
		ArrayList<Integer> sortedIndices = new ArrayList<Integer>(game.getNumberIssues());
		for(int init = 0; init < game.getNumberIssues(); init++)
			ans.add(0);
		
		for (int i = 0; i < game.getNumberIssues(); i++)
		{
			int max = 0;
			int value = 0;
			int index = 0;
			for (int j = 0; j < game.getNumberIssues(); j++)
			{
				value = game.getSimplePoints(agent.getID()).get(game.getIssuePluralText().get(j));
				if (value > max && !sortedIndices.contains(j))
				{
					max = value;
					index = j;
				}
			}
			sortedIndices.add(index);
			ans.set(index, rating);
			rating++;
		}
		return ans;
	}
	
	/**
	 * Returns the expected value for this agent's adversary on an offer for a given ordering of preferences.
	 * @param o the offer
	 * @param ordering the ordering
	 * @return the total value
	 */
	protected int adversaryValue(Offer o, ArrayList<Integer> ordering) 
	{
		int ans = 0;
		for (int num = 0; num < game.getNumberIssues(); num++)
			ans += o.getItem(num)[adversaryRow] * (game.getNumberIssues() - ordering.get(num) + 1);
		return ans;
	}
	
	/**
	 * Returns the maximum possible value for an adversary on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	protected int adversaryValueMax(Offer o)
	{
		int max = 0;
		for (ArrayList<Integer> order : orderings)
			max = Math.max(max,  adversaryValue(o, order));
		return max;
	}
	
	/**
	 * Returns the minimum possible value for an adversary on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	protected int adversaryValueMin(Offer o)
	{
		int min = 0;
		for (ArrayList<Integer> order : orderings)
			min = Math.min(min,  adversaryValue(o, order));
		return min;
	}
	
	/***
	 * Finds the adversary's highest ranked item in the most ideal ordering
	 * @return index of the best item (for opposing agent)
	 */
	protected int findAdversaryIdealBest()
	{
		ArrayList<Integer> order = getMinimaxOrdering();
		for (int i = 0; i < order.size(); i++)
		{
			if(order.get(i) == 1)
				return i;
		}
		return -1;
	}

	/***
	 * Finds the adversary's second highest ranked item in the most ideal ordering
	 * @return index of the second best item (for opposing agent)
	 */
	protected int findAdversaryIdealSecondBest()
	{
		ArrayList<Integer> order = getMinimaxOrdering();
		for (int i = 0; i < order.size(); i++)
		{
			if(order.get(i) == 2)
				return i;
		}
		return -1;
	}

	/***
	 * Finds the adversary's lowest ranked item in the most ideal ordering
	 * @return index of the worst item (for opposing agent)
	 */
	protected int findAdversaryIdealWorst(GameSpec game)
	{
		ArrayList<Integer> order = getMinimaxOrdering();
		for (int i = 0; i < order.size(); i++)
		{
			if(order.get(i) == game.getNumberIssues())
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns the last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event lastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		for (int i = history.size() - 1; i > 0; i--)
		{
			if(history.get(i).getType() == type)
				return history.get(i);
		}
		return null;
	}
	
	/**
	 * Returns the last event of type type that was received, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event lastEventReceived(LinkedList<Event> history, Event.EventClass type)
	{
		for (int i = history.size() - 1; i > 0; i--)
		{
			if(history.get(i).getType() == type && history.get(i).getOwner() != agent.getID()) 
			{
				return history.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns the second to last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event secondLastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		boolean foundFirst = false;
		for (int i = history.size() - 1; i > 0; i--)
		{
			if(history.get(i).getType() == type && !foundFirst)
			{
				foundFirst = true;
				continue;
			}
			else if (history.get(i).getType() == type && foundFirst)
				return history.get(i);
		}
		return null;
	}
	
	/**
	 * Eliminates invalid orderings by looking at preferences, the oldest ones first.
	 * @return true if there are no valid orderings, false otherwise
	 */
	protected boolean reconcileContradictions()
	{
		orderings = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < permutations.length; i++)
		{
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < permutations[i].length; j++)
			{
				a.add(permutations[i][j]);
			}
			orderings.add(a); 
		}
		
		for (Preference pref: preferences)
		{
			//ServletUtils.log(pref.toString(), ServletUtils.DebugLevels.DEBUG);
			Relation r = pref.getRelation();
			ArrayList<ArrayList<Integer>> toRemove = new ArrayList<ArrayList<Integer>>();
			if(r == Relation.BEST)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) != 1)//if the ordering does not have the item as number 1 
						toRemove.add(o);
				}
			}
			else if(r == Relation.WORST)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) != game.getNumberIssues())//if the ordering does not have the item as the last place 
						toRemove.add(o);
				}
			}
			else if(r == Relation.GREATER_THAN)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) > o.get(pref.getIssue2()))//if the ordering does not have the item greater
						toRemove.add(o);
				}
			}
			else if(r == Relation.LESS_THAN)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) < o.get(pref.getIssue2()))//if the ordering does not have the item lesser 
						toRemove.add(o);
				}
			}
			else if(r == Relation.EQUAL)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(Math.abs(o.get(pref.getIssue1()) - o.get(pref.getIssue2())) == 1)//if the ordering does not have the items adjacent 
						toRemove.add(o);
				}
			}
			
			for(ArrayList<Integer> al : toRemove)
				orderings.remove(al);
			
			//ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		}
		//ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		
		if(orderings.size() == 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Finds the BATNA of an agent, takes into consideration whether it's lying or not
	 * @param game
	 * @param lieThreshold the gauge of how much an agent exaggerates its BATNA
	 * @param liar whether or not the agent actually wants to lie 
	 * @return the agent's BATNA, either true or a lie.
	 */
	public int getLyingBATNA(GameSpec game, double lieThreshold, boolean liar)
	{	
		if (liar)
		{
			int totalPoints = getMaxPossiblePoints();
			return (int) (lieThreshold*totalPoints); 
		}
		return game.getBATNA(agent.getID());
	}
		
	/**
	 * Finds the ordering among possible orderings that is most/least different than the VH's ordering, based on the value of isFixedPie (set by separate method).
	 * @return the chosen ordering.
	 */
	protected ArrayList<Integer> getMinimaxOrdering()
	{
		boolean reversed = isFixedPie;
		int valueHeuristic = 0;
		int minimax = reversed ? Integer.MAX_VALUE : 0;
		ArrayList<Integer> ans = null;
		// Just in case this hasn't been run yet
		reconcileContradictions();
		for(ArrayList<Integer> order: orderings) // Goes through each order in orderings
		{
			valueHeuristic = 0;
			for (int i = 0; i < order.size(); i++) 
			{
				valueHeuristic += Math.abs(order.get(i) - getMyOrdering().get(i)); // Sum up the difference between agent ranking and player ranking of each issue 
			}
			if(reversed)
			{
				if (valueHeuristic < minimax) // The largest sum means the bigger difference between the orderings and thus the most integrative scenario
				{
					minimax = valueHeuristic;
					ans = order;
				}
			}
			else
			{
				if(valueHeuristic > minimax)
				{
					minimax = valueHeuristic;
					ans = order;
				}
			}
		}
		return ans;
	}
	
	/***
	 * Finds the maximum amount of total points to be had in a certain game (i.e., agent's score if they received every item).
	 * @return max possible points
	 */
	public int getMaxPossiblePoints() 
	{
		List<Integer> quantities = game.getIssueQuantities();
		int totalPoints = 0;
		for (int index = 0; index < game.getNumberIssues(); index++)
		{
			String s = game.getIssuePluralText().get(index);
			totalPoints += quantities.get(index)*game.getSimplePoints(agent.getID()).get(s);
		}
		return totalPoints;
	}
	
	/**
	 * determines if a pair of given BATNA's are in conflict, i.e. an offer doesn't exist to satisfy both BATNAs.
	 * @param agentBATNA		can be either the true BATNA, or the presented BATNA, depending on the circumstances
	 * @param adversaryBATNA	this should always be the stored value of the opponent's BATNA.
	 * @return true if there is a conflict, false otherwise.
	 */
	public boolean conflictBATNA(int agentBATNA, int adversaryBATNA) 
	{
		return agentBATNA + adversaryBATNA > getMaxPossiblePoints();
	}
	
	/**
	 * determines if a pair of given BATNA's are in conflict, i.e. an offer doesn't exist to satisfy both BATNAs.
	 * If the BATNAs conflict, it returns a decreased value for the passed in BATNA.
	 * @param vhPresentedBATNA			can be either the true BATNA, or the presented BATNA, depending on the circumstances
	 * @return an updated BATNA for the VH, possibly lower, if the opponent and VH BATNAs conflict. Always at least the actual VH BATNA.
	 */
	public int lowerBATNA(int vhPresentedBATNA) 
	{
		if (conflictBATNA(vhPresentedBATNA, adversaryBATNA))
		{
			// If agent is competitive but they are not getting more than 60% of the joint value
			if (vhPresentedBATNA / (vhPresentedBATNA + adversaryBATNA) < 0.6 && competitive)
			{
				int curBATNA = vhPresentedBATNA;
				double newBATNA =  Math.max(game.getBATNA(agent.getID()), curBATNA - game.getBATNA(agent.getID())); 
				// Return the max of their actual BATNA and their current BATNA decremented by their actual BATNA 
				return (int) newBATNA;
			}
			// Otherwise, return the max of the agent's actual BATNA and the max number of points they could possibly get if
			// they awarded the user their BATNA.
			return Math.max(getMaxPossiblePoints() - adversaryBATNA, game.getBATNA(agent.getID()));
		}
		else
			return vhPresentedBATNA;
	}

	/***
	 * Helper to easily return expected/predicted value of an offer for an agent's adversary
	 * @param o an offer to look at
	 * @return the predicted value of the offer to the adversary.
	 */
	protected int getAdversaryValue(Offer o) 
	{
		ServletUtils.log("Minimax ordering: " + getMinimaxOrdering() , ServletUtils.DebugLevels.DEBUG);
		return adversaryValue(o, getMinimaxOrdering());
	}

	/***
	 * Getter for the agent's ID (0 for user, 1 for computer agent)
	 * @return current agent's ID 
	 */
	public int getID() 
	{
		return agent.getID();
	}
	
	/***
	 * Finds the position on the board (1 through num issues) of an item with a particular ranking for current agent
	 * @param game
	 * @param num the ranking of the item within an ordering (i.e., 1 means best, 2 means second best, etc.)
	 * @return the index of the item
	 */
	int findMyItemIndex(GameSpec game, int num) 
	{
		ArrayList<Integer> order = getMyOrdering();
		for (int i = 0; i < order.size(); i++) 
		{
			if (order.get(i) == num) 
			{
				return i;
			}
		}
		return -1;
	}
	
	/***
	 * Finds the position on the board (1 through num issues) of an item with a particular ranking for the agent's adversary.
	 * @param game
	 * @param num the ranking of the item within an ordering (i.e., 1 means best, 2 means second best, etc.)
	 * @return the index of the item
	 */
	int findAdversaryItemIndex(GameSpec game, int num) 
	{
		for (ArrayList<Integer> order: orderings)
		{
			for (int i = 0; i < order.size(); i++)
			{
				if(order.get(i) == num)
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * Finds the name of an item that's ranked a certain way by the agent.
	 * @param order The rank of the item.
	 * @param game The GameSpec being used.
	 * @return the name of the item the agent ranks according to the given order.
	 */
	String findMyItem(int order, GameSpec game)
	{
		if(order <= 0 || order > game.getNumberIssues())
			throw new IndexOutOfBoundsException("Index out bounds on VH Preference!");
		Map<String, Integer> pref =  MathUtils.sortByValue(game.getSimplePoints(this.getID()));
		int count = 0;
		for (Map.Entry<String, Integer> s: pref.entrySet())
		{
			count++;
			if(count == order)
				return s.getKey();
		}
		return null;
	}
	
	/***
	 * Finds a random, accurate preference
	 * @return random preference
	 */
	Preference randomPref() 
	{
		int issue1 = (int)( Math.random()* (getSpec().getNumberIssues()));
		int issue2 = (int)( Math.random()* (getSpec().getNumberIssues()));

		if (issue1 == issue2)
			issue2 = (issue2 + 1) % getSpec().getNumberIssues();
		ArrayList<Integer> ordering = getMyOrdering();
		Relation r = null;
		if (ordering.get(issue1) > ordering.get(issue2))
			r = Relation.GREATER_THAN;
		else if (ordering.get(issue1) < ordering.get(issue2))
			r = Relation.LESS_THAN;
		else
			r = Relation.EQUAL;
		Preference p = new Preference(issue1, issue2, r, false);
		return p;
	}
	
	/**
	 * Determines if there is a "particularly" valuable item this time around.
	 * @return a yes or no answer to that question
	 */
	protected boolean isImportantGame()
	{
		
		double RATIO = 1.5;
		double calcRatio = ((double)game.getSimpleOpponentPoints().get(this.findMyItem(1, game)))/((double)game.getSimpleOpponentPoints().get(this.findMyItem(2, game)));
		ServletUtils.log("Calculated value ratio was: " + calcRatio, ServletUtils.DebugLevels.DEBUG);
		
		return calcRatio > RATIO;
	}
	
	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent.  Can be negative.  Persists across games.
	 * @return the ledger
	 */
	protected int getLedger()
	{
		return this.agent.getLedger();
	}
	
	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent, including all pending values.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	protected int getTotalLedger()
	{
		return this.agent.getTotalLedger();
	}
	
	/**
	 * Returns a simple int representing the potential "ledger" of favors verbally agreed to.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	protected int getVerbalLedger()
	{
		return this.agent.getVerbalLedger();
	}
	
	/**
	 * Allows you to modify the agent's internal "ledger" of favors done for it.  
	 * @param increment value (negative ok)
	 */
	protected void modifyVerbalLedger(int increment)
	{
		this.agent.modifyVerbalLedger(increment);
	}
	
	/**
	 * Allows you to modify the agent's internal "ledger" of favors done for it.  
	 * @param increment value (negative ok)
	 */
	protected void modifyOfferLedger(int increment)
	{
		this.agent.modifyOfferLedger(increment);
	}



}



