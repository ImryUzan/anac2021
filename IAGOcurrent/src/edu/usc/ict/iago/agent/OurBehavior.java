package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.Random;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.ServletUtils;

public class OurBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
		
	private AgentUtilsExtension utils;
	private GameSpec game;	
	private Offer allocated;
	private LedgerBehavior lb = LedgerBehavior.NONE;
	private int adverseEvents = 0;
	private Offer lastOffer = null;
	private int[] lastPlayerOffer = null;
	
	private int[] lastAgentOfferAgent = null;
	private int[] lastAgentOfferPlayer = null;
	private int reject_num = 0;
	private int offer_num=0;
	private int num_accept = 0;
	private int reject_local_num = 0;
	
	
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
	
	public Offer NewOffer() {
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		return propose;
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
		this.lastOffer = propose;
		return propose;
	}

	public void setLastPlayerOffer(Offer of) {
		int arr[] =new int[game.getNumberIssues()];
		Offer all = this.allocated;
		for(int i=0;i<game.getNumberIssues();i++) {
			arr[i]=of.getItem(i)[1]-all.getItem(i)[1];
		}
		this.lastPlayerOffer = arr;
	}
	
//	public void setLastAgentOffer() {
//		int arr[] =new int[game.getNumberIssues()];
//		Offer all = this.allocated;
//		for(int i=0;i<game.getNumberIssues();i++) {
//			arr[i]=all.getItem(i)[1]-this.lastOffer.getItem(i)[1];
//			
//		}
//		this.lastAgentOffer = arr;
//		
//	}
	
	@Override
	public Offer getNextOffer(History history) 
	{	
			
//		ArrayList<Integer> vhPref = utils.getMyOrdering();
//		int max = vhPref.get(0);
//		int agentFav = 0;
//		int min = vhPref.get(0);
//		int forPlayer=0;
//		for(int i  = 0; i < game.getNumberIssues(); i++) {
//			if(vhPref.get(i) < max)
//			{
//				agentFav = i;
//				max = vhPref.get(i);
//			}
//			if(vhPref.get(i) > min)
//			{
//				forPlayer = i;
//				min = vhPref.get(i);
//			}
//		}
//		
//		Offer propose = new Offer(game.getNumberIssues());
//		for(int issue = 0; issue < game.getNumberIssues(); issue++)
//			propose.setItem(issue, allocated.getItem(issue));
//		propose.setItem(agentFav, new int[] {allocated.getItem(agentFav)[0]+1, allocated.getItem(agentFav)[1]-1, allocated.getItem(agentFav)[2]});
//		propose.setItem(forPlayer, new int[] {allocated.getItem(forPlayer)[0], allocated.getItem(forPlayer)[1]-1, allocated.getItem(forPlayer)[2]+1});
//		
//		this.lastOffer = propose;
//		return propose;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		
		
		// Assign ordering to the player based on perceived preferences. Ideally, they would be opposite the agent's (integrative)
		ArrayList<Integer> playerPref = utils.getMinimaxOrdering(); 
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		
		// Array representing the middle of the board (undecided items)
		int[] free = new int[game.getNumberIssues()];
		
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = allocated.getItem(issue)[1];
		}
	
		int userFave = -1;
		int opponentFave = -1;
		
		// Find most valued issue for player and VH (of the issues that have undeclared items)
		int max = game.getNumberIssues() + 1;
		for(int i  = 0; i < game.getNumberIssues(); i++)
			if(free[i] > 0 && playerPref.get(i) < max)
			{
				userFave = i;
				max = playerPref.get(i);
			}
		max = game.getNumberIssues() + 1;
		for(int i  = 0; i < game.getNumberIssues(); i++)
			if(free[i] > 0 && vhPref.get(i) < max)
			{
				opponentFave = i;
				max = vhPref.get(i);
			}
		
		
		//is there ledger to work with?
		if(lb == LedgerBehavior.NONE) //this agent doesn't care
		{
			//nothing
		}
		else if (utils.getVerbalLedger() < 0) //we have favors to cash!
		{
			//we will naively cash them immediately regardless of game importance
			//take entire category
			utils.modifyOfferLedger(-1);
			propose.setItem(opponentFave, new int[] {allocated.getItem(opponentFave)[0] + free[opponentFave], 0, allocated.getItem(opponentFave)[2]});
			return propose;	
		}
		else if (utils.getVerbalLedger() > 0) //we have favors to return!
		{
			if (lb == LedgerBehavior.BETRAYING)//this agent doesn't care
			{
				//nothing, so continue
			}
			else if(lb == LedgerBehavior.FAIR)//this agent returns an entire column!
			{
				//return entire category
				utils.modifyOfferLedger(1);
				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], 0, allocated.getItem(userFave)[2] + free[userFave]});
				return propose;
			}
			else //if (lb == LedgerBehavior.LIMITED)//this agent returns a single item.  woo hoo
			{
				//return single item
				utils.modifyOfferLedger(1);
				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
				return propose;
			}
		}
		else //we have nothing special
		{
			//nothing, so continue
		}

		

		if (userFave == -1 && opponentFave == -1) // We already have a full offer (no undecided items), try something different
		{
			//just repeat and keep allocated
		}			
		else if(userFave == opponentFave)// Both agent and player want the same issue most
		{
			if(free[userFave] >= 2) // If there are more than two of that issue, propose an offer where the VH and player each get one more of that issue
				propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0] + 1, free[userFave] - 2, allocated.getItem(userFave)[2] + 1});
			else // Otherwise just give the one item left to us, the agent
			{
				if (utils.adversaryRow == 0) {
					propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
				} else if (utils.adversaryRow == 2) {
					propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0] + 1, free[userFave] - 1, allocated.getItem(userFave)[2]});
				}
			}
		}
		else // If the agent and player have different top picks
		{
			// Give both the VH and the player one more of the item they want most
			propose.setItem(userFave, new int[] {allocated.getItem(userFave)[0], free[userFave] - 1, allocated.getItem(userFave)[2] + 1});
			propose.setItem(opponentFave, new int[] {allocated.getItem(opponentFave)[0] + 1, free[opponentFave] - 1, allocated.getItem(opponentFave)[2]});
		}
		this.offer_num++;
		return propose;
	}
//	public int scoreOffer(Offer offer)
//    {
//		int totalPoints = 0;
//		for (int index = 0; index < game.getNumberIssues(); index++)
//		{
//			game.getIssuePluralText();
//			String s = game.getIssuePluralText().get(index);
//			totalPoints += offer.getItem(index)[0] * game.getSimplePoints(utils.getID()).get(s);
//		}
//		return totalPoints;
//    }
//	
//	
//	public double gradeOffer(Offer offer)
//    {
//    	double score = this.scoreOffer(offer);
//    	if (score <= utils.myPresentedBATNA)
//    		return 0.;
//    	else if (score >= bestCaseOfferScore)
//    		return 1.;
//    	else
//    		return score / (double)bestCaseOfferScore;
//    }
	
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
	
	private int getMyWorstNow(int[] free) {
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		int max = vhPref.get(0);
		int agentFav = -1;
		
		
		for(int i  = 0; i < game.getNumberIssues(); i++) {
			if(vhPref.get(i) >= max)
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
	
	private int getHisWorstNow(int[] free) {
		ArrayList<Integer> vhPref = utils.getMinimaxOrdering();
		int max = vhPref.get(0);
		int agentFav = -1;
		
		
		for(int i  = 0; i < game.getNumberIssues(); i++) {
			if(vhPref.get(i) >= max)
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
		for(int i:playerPref) {
			ServletUtils.log("reut"+Integer.toString(i), ServletUtils.DebugLevels.DEBUG);
		}
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
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, allocated.getItem(issue));
		
		if(myIndex==-1 || index==-1) {
			return null;
		}
		
		if(index==myIndex && free[index]>1) {
			int[] arr = allocated.getItem(index);
			arr[2]++;
			arr[1] = arr[1]-2;
			arr[0]++;
			propose.setItem(index, arr);
			int[] agent = new int[game.getNumberIssues()];
			agent[index]++;
			this.lastAgentOfferAgent = agent;
			int[] player = new int[game.getNumberIssues()];
			player[index]++;
			this.lastAgentOfferPlayer = player;
			this.lastOffer = propose;
			this.offer_num++;
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
			this.lastOffer = propose;
			int[] agent = new int[game.getNumberIssues()];
			agent[myIndex]++;
			this.lastAgentOfferAgent = agent;
			int[] player = new int[game.getNumberIssues()];
			player[index]++;
			this.lastAgentOfferPlayer = player;
			this.offer_num++;
			return propose;
			
		}
		
		int[] arr = allocated.getItem(myIndex);
		arr[1] = arr[1]-1;
		arr[0]++;
		propose.setItem(myIndex, arr);
		arr = allocated.getItem(index);
		arr[1] = arr[1]-1;
		arr[2]++;
		propose.setItem(index, arr);
		this.lastOffer = propose;
		int[] agent = new int[game.getNumberIssues()];
		agent[myIndex]++;
		this.lastAgentOfferAgent = agent;
		int[] player = new int[game.getNumberIssues()];
		player[index]++;
		this.lastAgentOfferPlayer = player;
		this.offer_num++;
		return propose;
	
	}
	
	
	private int getRndomProd(int[] free) {
		
		int sum = 0;
		
		for(int i: free) {
			if(i!=0) {
				sum++;
			}
		}
		int arr[]=new int[sum];
		int j=0;
		for(int i=0;i<free.length;i++) {
			if(free[i]>0) {
				arr[j]=i;
				j++;
			}
			
		}
		int var = (int)(Math.random() *sum);

		if (var>=sum) {
			return sum-1;
		}
		return arr[var];

	}
	
	
	
	@Override
	protected Offer getAcceptOfferFollowup(History history) {
		int saverPlayer[] = this.lastAgentOfferPlayer;
		int saverAgent[] = this.lastAgentOfferAgent;
		Offer of = this.NewOffer();
		int[] free = this.getFreeProd();
		int[] oldfree = this.getFreeProd();
		int arr[] = new int[game.getNumberIssues()];
		int arr1[]=new int[game.getNumberIssues()];
		
		
		
		int temp[]=this.lastAgentOfferAgent;
		int temp1[]=this.lastAgentOfferPlayer;
		this.lastAgentOfferPlayer=new int[game.getNumberIssues()];
		this.lastAgentOfferAgent=new int[game.getNumberIssues()];
		boolean flagSameAgent = false;
		boolean flagSamePlayer = false;
		if(this.offer_num<7) {
			
			for(int i=0;i<game.getNumberIssues();i++) {
				if (temp[i]>0 && free[i]>0) {
					flagSameAgent = true;
					int[ ] item = of.getItem(i);
					of.setItem(i, new int[] {item[0]+1,item[1]-1,item[2]});
					this.lastAgentOfferAgent[i]++;
					free[i]--;
				}
			}
			for(int i=0;i<game.getNumberIssues();i++) {
				if (temp1[i]>0 && free[i]>0) {
					flagSamePlayer = true;
					int[ ] item = of.getItem(i);
					of.setItem(i, new int[] {item[0],item[1]-1,item[2]+1});
					this.lastAgentOfferPlayer[i]++;
					free[i]--;
				}
			}
		}
		if(flagSameAgent&&flagSamePlayer) {
			this.lastOffer = of;
			this.offer_num++;
			return of;
		}else if((!flagSameAgent)&&(flagSamePlayer)) {
			int i = this.getMyBestNow(free);
			if(i==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
				
			}
			int[ ] item = of.getItem(i);
			of.setItem(i, new int[] {item[0]+1,item[1]-1,item[2]});
			this.lastAgentOfferAgent[i]++;
			this.lastOffer = of;
			this.offer_num++;
			return of;
			
		}else if((flagSameAgent)&&(!flagSamePlayer)) {
			int i = this.getPlayerBestNow(free);
			if(i==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;}
			int[ ] item = of.getItem(i);
			of.setItem(i, new int[] {item[0],item[1]-1,item[2]+1});
			this.lastAgentOfferPlayer[i]++;
			this.lastOffer = of;
			this.offer_num++;
			return of;
		}
		
		
		
		
		

		for(int i=0;i<oldfree.length;i++) {
			oldfree[i]+=saverAgent[i];
			oldfree[i]+=saverPlayer[i];
		}
		
		int myIndex = this.getMyBestNow(oldfree);
		int index = this.getPlayerBestNow(oldfree);
		boolean flagMyBest = false;
		if(myIndex==-1) {
			this.lastAgentOfferPlayer = saverPlayer;
			this.lastAgentOfferAgent = saverAgent;
			return null;
		}
		if(arr[myIndex]>0) {
			flagMyBest = true;
		}
		if(index==myIndex && flagMyBest) {
			//take me my second best
			free[index]=0;
			int my = this.getMyBestNow(free);
			if(my==-1) {
				this.lastAgentOfferAgent = saverAgent;
				this.lastAgentOfferPlayer = saverPlayer;
				return null;
			}
			int arr3[] = of.getItem(my);
			arr3[1]--;
			arr3[0]++;
			this.lastAgentOfferAgent[my]++;
			of.setItem(my,arr3);
			free[my]--;
			//give him a random prod

			free[index]=0;
			double chance =Math.random();
			int in = this.getPlayerBestNow(free);
			if(myIndex==-1) {
				this.lastAgentOfferAgent = saverAgent;
				this.lastAgentOfferPlayer = saverPlayer;
				return null;
			}
			if(chance>0.8) {
				in = this.getRndomProd(free);
			}
			
			int arr2[] = of.getItem(in);
			arr2[1]--;
			arr2[2]++;
			this.lastAgentOfferPlayer[in]++;
			
			of.setItem(in,arr2);
			this.lastOffer = of;
			this.offer_num++;
			return of;
			
		}
		
		if(myIndex==this.getHisWorstNow(oldfree)) {
			myIndex = this.getMyBestNow(free);
			if(myIndex==-1) {
				this.lastAgentOfferAgent = saverAgent;
				this.lastAgentOfferPlayer = saverPlayer;
				return null;
			}
			int arr3[] = of.getItem(myIndex);
			arr3[1]--;
			arr3[0]++;
			of.setItem(myIndex,arr3);
			free[myIndex]--;
			this.lastAgentOfferAgent[myIndex]++;
			if(free[myIndex]>0) {
				arr3[1]--;
				arr3[0]++;
				of.setItem(myIndex,arr3);
				free[myIndex]--;
				this.lastAgentOfferAgent[myIndex]++;
			}
			
			//give him a random prod

			
			int in = this.getPlayerBestNow(free);
			if(myIndex==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			double chance =Math.random();
			if(chance>0.8) {
				in = this.getRndomProd(free);
			}
			
			
			
			int arr2[] = of.getItem(in);
			arr2[1]--;
			arr2[2]++;
			this.lastAgentOfferPlayer[in]++;

			of.setItem(in,arr2);
			this.lastOffer = of;
			this.offer_num++;
			return of;
		}
		
		int goodAgent = this.getMyBestNow(free);
		if(goodAgent>-1) {
			free[goodAgent]--;
			int goodPlayer= this.getPlayerBestNow(free);
			if(goodPlayer>-1) {
				int ar[]=of.getItem(goodAgent);
				ar[0]++;
				ar[1]--;
				of.setItem(goodAgent, ar);
				int ar1[]=of.getItem(goodPlayer);
				ar1[2]++;
				ar1[1]--;
				of.setItem(goodPlayer, ar1);
				this.lastOffer = of;
				this.lastAgentOfferAgent[goodAgent]=1;
				this.lastAgentOfferPlayer[goodPlayer]=1;
				return of;
			}
			
		}
		
		this.lastAgentOfferPlayer = saverPlayer;
		this.lastAgentOfferAgent = saverAgent;
		return null;
		
	}
	
	
	
	
	@Override
	protected Offer getFirstOffer(History history) {
		int agentprod[] = new int[game.getNumberIssues()];
		int playerprod[] = new int[game.getNumberIssues()];
		
		
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
		
		this.lastOffer = propose;
		agentprod[agentFav]++;
		playerprod[forPlayer]++;
		this.lastAgentOfferAgent = agentprod;
		this.lastAgentOfferPlayer = playerprod;
		this.offer_num++;
		return propose;
	}

	@Override
	protected int getAcceptMargin() {
		return Math.max(0, Math.min(game.getNumberIssues(), adverseEvents));//basic decaying will, starts with fair
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
//write a func depend on the time.		
		
//		double chance = Math.random();
//		if(chance > 0.7) {
//			switch(this.reject) {
//			case 0:{
//				
//				
//			}case 1:{
//				
//			}case 2:{
//				
//			}
//			
//			}
//		}
		return null;
	}
	

}
