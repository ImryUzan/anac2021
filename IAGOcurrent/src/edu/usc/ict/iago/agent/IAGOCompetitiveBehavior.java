package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.List;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.ServletUtils.DebugLevels;

public class IAGOCompetitiveBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
		
	private AgentUtilsExtension utils;
	private GameSpec game;	
	private Offer allocated;
	private Offer concession;
	
	@Override
	protected void updateAllocated (Offer update)
	{
		allocated = update;
	}

	@Override
	protected Offer getAllocated ()
	{
		return allocated;
	}

	@Override
	protected Offer getConceded ()
	{
		return concession;
	}

	@Override
	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;
		utils.competitive = true;
		
		this.game = this.utils.getSpec();
		allocated = new Offer(game.getNumberIssues());
		for(int i = 0; i < game.getNumberIssues(); i++)
		{
			int[] init = {0, game.getIssueQuantities().get(i), 0};
			allocated.setItem(i, init);
		}
		concession = new Offer(game.getNumberIssues());
		for(int i = 0; i < game.getNumberIssues(); i++)
		{
			int[] init = {game.getIssueQuantities().get(i), 0, 0};
			concession.setItem(i, init);
		}
	}
	
	@Override
	protected Offer getFirstOffer(History history)
	{
		return getFirstOffer();
	}
	
	private Offer getFirstOffer()
	{
		Offer propose = new Offer(game.getNumberIssues());
		ArrayList<Integer> ordering = utils.getMyOrdering();
		List<Integer> quants = game.getIssueQuantities();
		for (int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			if (ordering.get(issue) == 1) // If the issue is my most valued issue
			{
				// Claim all of our favorite issue
				if (utils.myRow == 0)
				{
					propose.setItem(issue, new int[] {quants.get(issue), 0, 0});
				} 
				else if (utils.myRow == 2) 
				{
					propose.setItem(issue, new int[] {0, 0, quants.get(issue)});
				}
			} 
			else
			{
				// Take only half, still don't give adversary any issues
				if (utils.myRow == 0) 
				{
					propose.setItem(issue, new int[] {(int)((quants.get(issue)/2.0) + .5), quants.get(issue) - (int)((quants.get(issue)/2.0) + .5), 0});
				} 
				else if (utils.myRow == 2) 
				{
					propose.setItem(issue, new int[] {0, quants.get(issue) - (int)((quants.get(issue)/2.0) + .5), (int)((quants.get(issue)/2.0) + .5)});
				}
			}
		}
		concession = propose;
		return propose;
	}
	
	@Override
	public Offer getNextOffer(History history) 
	{
		return this.getNextOffer();
	}
	
	private Offer getNextOffer() 
	{	
		int myBATNA = game.getBATNA(utils.getID());
		
		// If the BATNAs conflict, we can't make an offer anyway
		if (utils.conflictBATNA(myBATNA, utils.adversaryBATNA)) 
		{
			return null; 
		}
		
		int opponentValue = 0;
		ArrayList<Integer> ordering = utils.getMyOrdering();
		
		// Start from where we currently have conceded
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue,  concession.getItem(issue));
			
		// Find all free items
		int[] free = new int[game.getNumberIssues()];
		int totalFree = 0;
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = concession.getItem(issue)[1];
			totalFree += concession.getItem(issue)[1];
		}
		
		//find bot deals
		int bottomIndex = game.getNumberIssues(); // Index of least valued issue for agent
		int min = -1;
		
		// Find issue such that agent has at least 1 item and it is our least valued issue
		for(int i  = 0; i < game.getNumberIssues(); i++)
		{
			if(concession.getItem(i)[utils.myRow] > 0 && ordering.get(i) > min)
			{
				bottomIndex = i;
				min = ordering.get(i);
			}
		}
		
		if(totalFree > 0)  //concede free items
		{
			for (int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				if (utils.myRow == 0) {
					propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
				} 
				else if (utils.myRow == 2)
				{
					propose.setItem(issue, new int[] {concession.getItem(issue)[0] + free[issue], 0, concession.getItem(issue)[2]});
				}
				
			}
			concession.setOffer(propose);
		}
		else if(bottomIndex < game.getNumberIssues()) //concede my worst, don't run this block if VH doesn't have any more items to concede
		{
			
			if (utils.myRow == 0)
			{
				propose.setItem(bottomIndex, new int[] {concession.getItem(bottomIndex)[0] - 1, 0, concession.getItem(bottomIndex)[2] + 1});
			} 
			else if (utils.myRow == 2) 
			{
				propose.setItem(bottomIndex, new int[] {concession.getItem(bottomIndex)[0] + 1, 0, concession.getItem(bottomIndex)[2] - 1});
			}
				
//			ServletUtils.log("Actual Value: " + utils.myActualOfferValue(propose) + " vs. Split: " + (fairSplit - getAcceptMargin()), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Considering new offer to propose...", ServletUtils.DebugLevels.DEBUG);
			if(!acceptOffer(propose)) // Don't go below what we'd formally accept
			{
				ServletUtils.log("New Offer rejected. Returning to most recent offer.", ServletUtils.DebugLevels.DEBUG);
				propose.setOffer(concession);	 //we don't want to return null, because that will result in a buggy "why don't you send an offer" message.
			}
			else 
			{
				ServletUtils.log("New offer accepted.", ServletUtils.DebugLevels.DEBUG);
				concession.setOffer(propose);
			}
		}
		
		opponentValue = utils.getAdversaryValue(propose);
		ServletUtils.log("Ensuring new offer is fair to opponent...", ServletUtils.DebugLevels.DEBUG);
		while (opponentValue < utils.adversaryBATNA)
		{	//concede to opponent's BATNA
			
			bottomIndex = game.getNumberIssues();
			min = -1;
			
			for(int i  = 0; i < game.getNumberIssues(); i++)
			{
				if(concession.getItem(i)[utils.myRow] > 0 && ordering.get(i) > min)
				{
					bottomIndex = i;
					min = ordering.get(i);
				}
			}
			
			if (utils.myRow == 0)
			{
				propose.setItem(bottomIndex, new int[] {concession.getItem(bottomIndex)[0] - 1, 0, concession.getItem(bottomIndex)[2] + 1});
			}
			else if (utils.myRow == 2)
			{
				propose.setItem(bottomIndex, new int[] {concession.getItem(bottomIndex)[0] + 1, 0, concession.getItem(bottomIndex)[2] - 1});
			}
			
			if (utils.myActualOfferValue(propose) < game.getBATNA(utils.getID())) {
				ServletUtils.log("Opponent Value: " + utils.getAdversaryValue(propose), DebugLevels.DEBUG);
				ServletUtils.log("VH Value: " + utils.myActualOfferValue(propose), DebugLevels.DEBUG);
				ServletUtils.log("Offer: " + propose, DebugLevels.DEBUG);
				return null;	//should only return null if the BATNAs are in conflict.
			}
			opponentValue = utils.getAdversaryValue(propose);
			concession.setOffer(propose);
		}
		
		return propose;
	}
	
	@Override
	protected Offer getTimingOffer(History history) 
	{
		return this.getTimingOffer();
	}
	
	private Offer getTimingOffer()
	{
		//50% chance of doing nothing
		double chance = Math.random();
		ServletUtils.log("Agent rolling dice: " + ((double)((int)(chance*100)))/100, ServletUtils.DebugLevels.DEBUG);
		if (chance > 0.50)
			return null;
		else
			return getNextOffer();
	}

	@Override
	protected Offer getAcceptOfferFollowup(History history) 
	{
		return getAcceptOfferFollowup();
	}
	
	private Offer getAcceptOfferFollowup()
	{
		ArrayList<Integer> ordering = utils.getMyOrdering();
		
		//start from where we currently have conceded
		Offer propose = new Offer(game.getNumberIssues());
			for(int issue = 0; issue < game.getNumberIssues(); issue++)
				propose.setItem(issue,  concession.getItem(issue));
		
		int[] free = new int[game.getNumberIssues()];
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = concession.getItem(issue)[1];
		}
		
		//find top deals
		int opponentFave = -1;
		int max = game.getNumberIssues() + 1;

		// Go through the issues, find ones that have items unclaimed, identify which of those issues is most valued by
		// the opponent.
		for(int i  = 0; i < game.getNumberIssues(); i++)
			if(free[i] > 0 && ordering.get(i) < max) 
			{
				opponentFave = i;
				max = ordering.get(i);
			}
		
		if (opponentFave == -1) //we're already at a full offer, but need to try something different
		{
			//just repeat and keep allocated
		}		
		else 
		{
			// Agent takes all of the items left of its favorite
			if (utils.myRow == 0) 
			{
				propose.setItem(opponentFave, new int[] {concession.getItem(opponentFave)[0] + free[opponentFave], 0, concession.getItem(opponentFave)[2]}); 
			} 
			else if (utils.myRow == 2) 
			{
				propose.setItem(opponentFave, new int[] {concession.getItem(opponentFave)[0], 0, concession.getItem(opponentFave)[2] + free[opponentFave]});
			}
		}
		concession = propose;	
		return propose;
	}

	@Override
	protected Offer getRejectOfferFollowup(History history)
	{
		return getRejectOfferFollowup();
	}
	
	private Offer getRejectOfferFollowup()
	{
		//start from where we currently have conceded
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue,  concession.getItem(issue));
			
		int[] free = new int[game.getNumberIssues()];
		int totalFree = 0;
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = concession.getItem(issue)[1];
			totalFree += concession.getItem(issue)[1];
		}
		
		if(totalFree > 0)  // Concede all free items
		{
			for (int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				if (utils.myRow == 0)
				{
					propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
				} 
				else if (utils.myRow == 2)
				{
					propose.setItem(issue, new int[] {concession.getItem(issue)[0] + free[issue], 0, concession.getItem(issue)[2]});
				}
			}
		}
		else
			return null;
		
		concession = propose;	
		return propose;
	}
	
	@Override
	protected Offer getFinalOffer(History history)
	{
		return this.getFinalOffer();
	}
	
	private Offer getFinalOffer()
	{		
		//start from where we currently have conceded
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue,  concession.getItem(issue));

		int[] free = new int[game.getNumberIssues()];
		int totalFree = 0;
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = concession.getItem(issue)[1];
			totalFree += concession.getItem(issue)[1];
		}
				
		if(totalFree > 0)  //concede free items
		{
			for (int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				if (utils.myRow == 0) 
				{
					propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
				} 
				else if (utils.myRow == 2) 
				{
					propose.setItem(issue, new int[] {concession.getItem(issue)[0] + free[issue], 0, concession.getItem(issue)[2]});
				}
			}
		}
		return propose;
	}

	@Override
	protected int getAcceptMargin() {
		return (int)(-1.5 * game.getNumberIssues());
	}
	
	/**
	 * Determines whether the agent should accept an offer based on the joint value using perceived user preferences
	 * @param o an offer to evaluate
	 * @return a boolean, true if the offer provides the agent with at least a given percentage of the overall joint value 
	 */
	protected boolean acceptOffer(Offer o)
	{
		double myValue, opponentValue, jointValue;
		myValue = utils.myActualOfferValue(o);
		opponentValue = utils.getAdversaryValue(o);
		ServletUtils.log("acceptOffer method - Agent Value: " + myValue + ", Perceived Opponent Value: " + opponentValue + ", Opponent BATNA: " + utils.adversaryBATNA, ServletUtils.DebugLevels.DEBUG);
		jointValue = myValue + opponentValue;
		return (myValue/jointValue > .6 & myValue >= utils.myPresentedBATNA) || 
			   (!utils.conflictBATNA(utils.myPresentedBATNA, utils.adversaryBATNA) && myValue >= utils.myPresentedBATNA);	//The threshold of .6 is semi-arbitrary
	}
	
	/**
	 * When the opponent sends a new BATNA value, the VH starts a new concession curve from the "top" of the graph.
	 */
	protected void resetConcessionCurve()
	{
		concession = new Offer(game.getNumberIssues());
		for(int i = 0; i < game.getNumberIssues(); i++)
		{
			int[] init = {0,0,0};
			
			if (utils.myRow == 0) 
			{
				init = new int[] {game.getIssueQuantities().get(i), 0, 0};
			} else if (utils.myRow == 2) 
			{
				init = new int[] {0, 0, game.getIssueQuantities().get(i)};
			}
			
			concession.setItem(i, init);
		}
		this.getFirstOffer();
	}

	@Override
	protected void updateAdverseEvents(int change) {
		return;
		
	}

}
