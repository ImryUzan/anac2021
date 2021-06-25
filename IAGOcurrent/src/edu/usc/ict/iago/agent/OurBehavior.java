package edu.usc.ict.iago.agent;

import java.util.ArrayList;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;

public class OurBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
		
	private AgentUtilsExtension utils;
	private GameSpec game;	
	private Offer allocated;
	private LedgerBehavior lb = LedgerBehavior.NONE;
	private int adverseEvents = 0;
	
	
	public enum LedgerBehavior
	{
		FAIR,
		LIMITED,
		BETRAYING,
		NONE;
	}
	
	public OurBehavior (LedgerBehavior lb)
	{
		super();
		this.lb = lb;
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
	}
	
	@Override
	protected void updateAllocated (Offer update)
	{
		allocated = update;
	}
	
	@Override
	protected void updateAdverseEvents (int change)
	{
		adverseEvents = Math.max(0, adverseEvents + change);
	}
	
	
	@Override
	protected Offer getAllocated ()
	{
		return allocated;
	}
	
	@Override
	protected Offer getConceded ()
	{
		return allocated;
	}
	
	@Override
	protected Offer getFinalOffer(History history)
	{
		Offer propose = new Offer(game.getNumberIssues());
		int totalFree = 0;
		do 
		{
			totalFree = 0;
			for(int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				totalFree += allocated.getItem(issue)[1]; // adds up middle row of board, calculate unclaimed items
			}
			propose = getNextOffer(history);
			updateAllocated(propose);
		} while(totalFree > 0); // Continue calling getNextOffer while there are still items left unclaimed
		return propose;
	}

	@Override
	public Offer getNextOffer(History history) 
	{	
			
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		int max = vhPref.get(0);
		int agentFav = 0;
		int min = vhPref.get(0);
		int forPlayer=0;
		for(int i  = 0; i < game.getNumberIssues(); i++) {
			if(vhPref.get(i) < max)
			{
				agentFav = i;
				max = vhPref.get(i);
			}
			if(vhPref.get(i) > min)
			{
				forPlayer = i;
				min = vhPref.get(i);
			}
		}
		
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		propose.setItem(agentFav, new int[] {allocated.getItem(agentFav)[0]+1, allocated.getItem(agentFav)[1]-1, allocated.getItem(agentFav)[2]});
		propose.setItem(forPlayer, new int[] {allocated.getItem(forPlayer)[0], allocated.getItem(forPlayer)[1]-1, allocated.getItem(forPlayer)[2]+1});
		
		
		return propose;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		//start from where we currently have accepted
//		Offer propose = new Offer(game.getNumberIssues());
//		for(int issue = 0; issue < game.getNumberIssues(); issue++)
//			propose.setItem(issue, allocated.getItem(issue));
//		
//		
//		// Assign ordering to the player based on perceived preferences. Ideally, they would be opposite the agent's (integrative)
//		ArrayList<Integer> playerPref = utils.getMinimaxOrdering(); 
//		ArrayList<Integer> vhPref = utils.getMyOrdering();
//		
//		// Array representing the middle of the board (undecided items)
//		int[] free = new int[game.getNumberIssues()];
//		
//		for(int issue = 0; issue < game.getNumberIssues(); issue++)
//		{
//			free[issue] = allocated.getItem(issue)[1];
//		}
//	
//		int userFave = -1;
//		int opponentFave = -1;
//		
//		// Find most valued issue for player and VH (of the issues that have undeclared items)
//		int max = game.getNumberIssues() + 1;
//		for(int i  = 0; i < game.getNumberIssues(); i++)
//			if(free[i] > 0 && playerPref.get(i) < max)
//			{
//				userFave = i;
//				max = playerPref.get(i);
//			}
//		max = game.getNumberIssues() + 1;
//		for(int i  = 0; i < game.getNumberIssues(); i++)
//			if(free[i] > 0 && vhPref.get(i) < max)
//			{
//				opponentFave = i;
//				max = vhPref.get(i);
//			}
//		
//		
//		//is there ledger to work with?
//		if(lb == LedgerBehavior.NONE) //this agent doesn't care
//		{
//			//nothing
//		}
//		else if (utils.getVerbalLedger() < 0) //we have favors to cash!
//		{
//			//we will naively cash them immediately regardless of game importance
//			//take entire category
//			utils.modifyOfferLedger(-1);
//			propose.setItem(opponentFave, new int[] {allocated.getItem(opponentFave)[0] + free[opponentFave], 0, allocated.getItem(opponentFave)[2]});
//			return propose;	
//		}
//		else if (utils.getVerbalLedger() > 0) //we have favors to return!
//		{
//			if (lb == LedgerBehavior.BETRAYING)//this agent doesn't care
//			{
//				//nothing, so continue
//			}
//			else if(lb == LedgerBehavior.FAIR)//this agent returns an entire column!
//			{
//				//return entire category
//				utils.modifyOfferLedger(1);
//				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], 0, allocated.getItem(userFave)[2] + free[userFave]});
//				return propose;
//			}
//			else //if (lb == LedgerBehavior.LIMITED)//this agent returns a single item.  woo hoo
//			{
//				//return single item
//				utils.modifyOfferLedger(1);
//				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
//				return propose;
//			}
//		}
//		else //we have nothing special
//		{
//			//nothing, so continue
//		}
//
//		
//
//		if (userFave == -1 && opponentFave == -1) // We already have a full offer (no undecided items), try something different
//		{
//			//just repeat and keep allocated
//		}			
//		else if(userFave == opponentFave)// Both agent and player want the same issue most
//		{
//			if(free[userFave] >= 2) // If there are more than two of that issue, propose an offer where the VH and player each get one more of that issue
//				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0] + 1, free[userFave] - 2, allocated.getItem(userFave)[2] + 1});
//			else // Otherwise just give the one item left to us, the agent
//			{
//				if (utils.adversaryRow == 0) {
//					propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
//				} else if (utils.adversaryRow == 2) {
//					propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0] + 1, free[userFave] - 1, allocated.getItem(userFave)[2]});
//				}
//			}
//		}
//		else // If the agent and player have different top picks
//		{
//			// Give both the VH and the player one more of the item they want most
//			propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
//			propose.setItem(opponentFave, new int[] {allocated.getItem(opponentFave)[0] + 1, free[opponentFave] - 1, allocated.getItem(opponentFave)[2]});
//		}
//		
//		return propose;
	}
	
	private int[] getFreeProd() {
		int[] free = new int[game.getNumberIssues()];
		
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = allocated.getItem(issue)[1];
		}
		return free;
	}
	
	private int getMyBestNow(int[] free) {
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		int max = vhPref.get(0);
		int agentFav = -1;
		
		
		for(int i  = 0; i < game.getNumberIssues(); i++) {
			if(vhPref.get(i) <= max)
			{
				if(free[i]==0) {
					continue;
				}
				agentFav = i;
				max = vhPref.get(i);
			}
		}
		
		return agentFav;
	}
	
	private int getPlayerBestNow(int[] free) {
		
		ArrayList<Integer> playerPref = utils.getMinimaxOrdering(); 
		int max = playerPref.get(0);
		int index = -1;
		for (int i=0;i<playerPref.size();i++) {
			if(playerPref.get(i) <= max)
			{
				if(free[i]==0) {
					continue;
				}
				index = i;
				max = playerPref.get(i);
			}
		}
		return index;
	}
	
	@Override
	protected Offer getTimingOffer(History history) {
		int[] free = this.getFreeProd();
		int index = this.getPlayerBestNow(free);
		int myIndex = this.getMyBestNow(free);
		
		Offer propose = new Offer(game.getNumberIssues());
		if(myIndex==-1) {
			return null;
		}
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		
		if(index==myIndex && free[index]>1) {
			int[] arr = allocated.getItem(index);
			arr[2]++;
			arr[1] = arr[1]-2;
			arr[0]++;
			propose.setItem(index, arr);
			
			
			return propose;
		}else if(index==myIndex) {
			int[] arr = allocated.getItem(index);
			arr[1] = arr[1]-1;
			arr[0]++;
			propose.setItem(index, arr);
			free[index]--;
			index = this.getPlayerBestNow(free);
			if(index==-1) {
				return null;
			}
			arr = allocated.getItem(index);
			arr[1] = arr[1]-1;
			arr[2]++;
			propose.setItem(index, arr);
			return propose;
			
		}
		
		int[] arr = allocated.getItem(myIndex);
		arr[1] = arr[1]-1;
		arr[0]++;
		propose.setItem(index, arr);
		arr = allocated.getItem(index);
		arr[1] = arr[1]-1;
		arr[2]++;
		propose.setItem(index, arr);
		return propose;
	
	}

	@Override
	protected Offer getAcceptOfferFollowup(History history) {
		return null;
	}
	
	@Override
	protected Offer getFirstOffer(History history) {
		
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		int max = vhPref.get(0);
		int agentFav = 0;
		int min = vhPref.get(0);
		int forPlayer=0;
		for(int i  = 0; i < game.getNumberIssues(); i++) {
			if(vhPref.get(i) < max)
			{
				agentFav = i;
				max = vhPref.get(i);
			}
			if(vhPref.get(i) > min)
			{
				forPlayer = i;
				min = vhPref.get(i);
			}
		}
		
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		propose.setItem(agentFav, new int[] {1, allocated.getItem(agentFav)[1]-1, 0});
		propose.setItem(forPlayer, new int[] {0, allocated.getItem(forPlayer)[1]-1, 1});
		
		
		return propose;
	}

	@Override
	protected int getAcceptMargin() {
		return Math.max(0, Math.min(game.getNumberIssues(), adverseEvents));//basic decaying will, starts with fair
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
		return null;
	}
	

}
