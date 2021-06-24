package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.List;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.ServletUtils;

public class IAGOConcedingBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
		
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
	protected Offer getFinalOffer(History history)
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
				propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
			}
		}
		return propose;
	}

	@Override
	public Offer getNextOffer(History history) 
	{
		return getTimingOffer(history);
	}
	
	@Override
	protected int getAcceptMargin() {
		return (int)(-1.5 * game.getNumberIssues());
	}

	@Override
	protected Offer getTimingOffer(History history) {
		
		//50% chance of doing nothing
		double chance = Math.random();
		ServletUtils.log("Agent rolling dice: " + ((double)((int)(chance*100)))/100, ServletUtils.DebugLevels.DEBUG);
		if (chance > 0.50)
			return null;
		
		ArrayList<Integer> ordering = utils.getMyOrdering();
		
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
		
		//find bot deals
		int botVH = game.getNumberIssues();
		int min = -1;
		for(int i  = 0; i < game.getNumberIssues(); i++)
		{
			if(concession.getItem(i)[0] > 0 && ordering.get(i) > min)
			{
				botVH = i;
				min = ordering.get(i);
			}
		}
		
		if(totalFree > 0)  //concede free items
		{
			for (int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
			}
		}
		else //concede my worst
		{
			int totalIssues = 0;
			for (int i = 0; i < game.getNumberIssues(); i++)
				totalIssues += game.getIssueQuantities().get(i);
			int fairSplit = ((game.getNumberIssues() + 1) * totalIssues / 4);//approximation based on distributive case
			
			propose.setItem(botVH, new int[] {concession.getItem(botVH)[0] - 1, 0, concession.getItem(botVH)[2] + 1});
			
			ServletUtils.log("Actual Value: " + utils.myActualOfferValue(propose) + " vs. Split: " + (fairSplit - getAcceptMargin()), ServletUtils.DebugLevels.DEBUG);
			if(!(utils.myActualOfferValue(propose) > fairSplit - getAcceptMargin()))//don't go below what we'd formally accept
				return null;				
		}
		
		concession = propose;	
		
		return propose;
	}

	@Override
	protected Offer getAcceptOfferFollowup(History history) {
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
		int topVH = -1;
		int max = game.getNumberIssues() + 1;
		max = game.getNumberIssues() + 1;
		for(int i  = 0; i < game.getNumberIssues(); i++)
			if(free[i] > 0 && ordering.get(i) < max)
			{
				topVH = i;
				max = ordering.get(i);
			}
		
		if (topVH == -1) //we're already at a full offer, but need to try something different
		{
			//just repeat and keep allocated
		}		
		else
			propose.setItem(topVH, new int[] {concession.getItem(topVH)[0] + free[topVH], 0, concession.getItem(topVH)[2]}); //take it all
		concession = propose;	
		
		return propose;
	}

	@Override
	protected Offer getFirstOffer(History history) {
		Offer propose = new Offer(game.getNumberIssues());
		ArrayList<Integer> ordering = utils.getMyOrdering();
		List<Integer> quants = game.getIssueQuantities();
		for (int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			if (ordering.get(issue) == 1)
				propose.setItem(issue, new int[] {quants.get(issue), 0, 0});//claim all of our best issue
			else
				propose.setItem(issue, new int[] {(int)((quants.get(issue)/2.0) + .5), quants.get(issue) - (int)((quants.get(issue)/2.0) + .5), 0});//claim half of all remaining issues
		}
		concession = propose;
		return propose;
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
		
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
				propose.setItem(issue, new int[] {concession.getItem(issue)[0], 0, concession.getItem(issue)[2] + free[issue]});
			}
		}
		else
			return null;
		
		concession = propose;	
		
		return propose;
	}

	@Override
	protected void updateAdverseEvents(int change) {
		return;
		
	}

}
