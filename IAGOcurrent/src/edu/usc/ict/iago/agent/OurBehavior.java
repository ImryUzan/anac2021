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
	private int[] lastPlayerOfferToAgent = null;
	private int[] lastPlayerOfferToPlayer = null;
	private int[] lastPlayerOfferFree = null;
	private int[] lastAgentOfferAgent = null;
	private int[] lastAgentOfferPlayer = null;
	private int rejectFlag = 0;
	private boolean TimingFlag = false;
	private int offer_num=0;
	private int acceptFlag = 0;
	private int reject_local_num = 0;
	private int rejectLevleCounter = 0;
	
	
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
		for(int issue = 0; issue < game.getNumberIssues(); issue++) {
			int arr[] = copyAllocate(allocated.getItem(issue));
			propose.setItem(issue, arr);
			}
		return propose;
	}
	
	@Override
	protected Offer getFinalOffer(History history)
	{
		this.reject_local_num = 0;
		Offer propose = new Offer(game.getNumberIssues());
		int totalFree = 0;
		do 
		{
			totalFree = 0;
			for(int issue = 0; issue < game.getNumberIssues(); issue++)
			{
				totalFree += allocated.getItem(issue)[1]; // adds up middle row of board, calculate unclaimed items
			}
			propose = equalOffer(history);
			if(propose!=null) {updateAllocated(propose);}
			
		} while(totalFree > 0); // Continue calling getNextOffer while there are still items left unclaimed
		this.lastOffer = propose;
		return propose;
	}
	
	//called last than a minute before the game ends
	public Offer equalOffer(History history) 
	{
		int saverPlayer[] = this.lastAgentOfferPlayer;
		int saverAgent[] = this.lastAgentOfferAgent;
		int[] free = this.getFreeProd();
		this.lastAgentOfferPlayer=new int[game.getNumberIssues()];
		this.lastAgentOfferAgent=new int[game.getNumberIssues()];
		int[] agent = new int[game.getNumberIssues()];
		int[] player = new int[game.getNumberIssues()];
		int leftToSplit = 0;
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, copyAllocate(allocated.getItem(issue)));
		for(int issue = 0; issue < game.getNumberIssues(); issue++) {
			if(free[issue]%2 == 0) {
				int number = free[issue]/2;
				int[] arr =copyAllocate(allocated.getItem(issue));
				arr[2]= arr[2]+number;
				arr[1] = 0;
				arr[0] = arr[0]+number;
				propose.setItem(issue, arr);
				agent[issue] = agent[issue]+number;
				player[issue] = player[issue]+number;
				free[issue]=0;
				
			} else {
				leftToSplit ++;
				int tmp = free[issue]-1;
				int numberMinusOne = tmp/2;
				int[] arr =copyAllocate(allocated.getItem(issue));
				arr[2]= arr[2]+numberMinusOne;
				arr[1] = 1;
				arr[0] = arr[0]+numberMinusOne;
				propose.setItem(issue, arr);
				agent[issue] = agent[issue]+numberMinusOne;
				player[issue] = player[issue]+numberMinusOne;
				free[issue]=1;
			}
		}
		int forAgent;
		int forPlayer;
		if (leftToSplit%2 !=0){
			leftToSplit--;
			forAgent =1+(leftToSplit/2);
			forPlayer =leftToSplit/2;	
		} else {
			if(leftToSplit != 0) {
			forAgent =leftToSplit/2;
			forPlayer =leftToSplit/2;
			} else {
				forAgent = 0;
				forPlayer = 0;
			}
		}
		int countAgent = 0;
		int countPlayer = 0;
		while(countAgent != forAgent) {
			int a = this.getMyBestNow(free);
			if(a==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
				
			}
			int[] arr = copyAllocate(propose.getItem(a));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(a, arr);
			agent[a]++;
			countAgent++;
			free[a]--;
			if(countPlayer != forPlayer) {
			int p = this.getPlayerBestNow(free);
			if(p ==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			int[] arr1 =copyAllocate(propose.getItem(p));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(p, arr1);
			player[p]++;
			countPlayer++;
			free[p]--;
			}
		}
		this.lastAgentOfferAgent = agent;
		this.lastAgentOfferPlayer = player;
		this.lastOffer = propose;
		this.offer_num++;
		return propose;
	}
	

	public void setLastPlayerOffer(Offer of) {
		this.lastPlayerOfferToPlayer=new int[game.getNumberIssues()];
		this.lastPlayerOfferToAgent=new int[game.getNumberIssues()];
		this.lastPlayerOfferFree=new int[game.getNumberIssues()];
		for(int i=0;i<game.getNumberIssues();i++) {
			this.lastPlayerOfferToAgent[i] = of.getItem(i)[0];
			this.lastPlayerOfferToPlayer[i] = of.getItem(i)[2];
			this.lastPlayerOfferFree[i] = of.getItem(i)[1];
		}
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
		this.reject_local_num = 0;
		int saverPlayer[] = this.lastAgentOfferPlayer;
		int saverAgent[] = this.lastAgentOfferAgent;
		this.lastAgentOfferPlayer=new int[game.getNumberIssues()];
		this.lastAgentOfferAgent=new int[game.getNumberIssues()];
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue, copyAllocate(allocated.getItem(issue)));
		
		// Array representing the middle of the board (undecided items)
		int[] free = new int[game.getNumberIssues()];
		
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
		{
			free[issue] = this.lastPlayerOfferFree[issue];
		}
		
		Offer tmp = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			tmp.setItem(issue,new int[] {this.lastPlayerOfferToAgent[issue], this.lastPlayerOfferFree[issue],this.lastPlayerOfferToPlayer[issue]});
		
		int agentProfit = utils.myActualOfferValue(tmp);	
		int playerProfit = utils.adversaryValue(tmp, utils.getMyOrdering());
		propose = tmp;
		if(agentProfit > playerProfit) {

			int a = this.getMyBestNow(free);
			if(a==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
				
			}
			
			int[] arr = copyAllocate(propose.getItem(a));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(a, arr);
			this.lastAgentOfferAgent[a]++;
			int p = this.getPlayerBestNow(free);
			if(p ==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
				
			}
			int[] arr1 =copyAllocate(propose.getItem(p));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(p, arr1);
			this.lastAgentOfferPlayer[p]++;
			this.lastOffer = propose;
			this.offer_num++;
			return propose;
	
		}else {
			//for(int i = 0; i < game.getNumberIssues(); i++)
			while(agentProfit <= playerProfit)
			{
				int i = this.getMyBestNow(free);
				if(i==-1) {
					break;
				}
				int[] arr =copyAllocate(propose.getItem(i));
				arr[0]++;
				arr[1]--;
				free[i]--;
				propose.setItem(i, arr);
				this.lastAgentOfferAgent[i]++;
				agentProfit = utils.myActualOfferValue(propose);
			
			}
			if(agentProfit >= playerProfit) {
				this.lastOffer = propose;
				this.offer_num++;
				return propose;
			}else {
				this.lastAgentOfferPlayer=new int[game.getNumberIssues()];
				this.lastAgentOfferAgent=new int[game.getNumberIssues()];
				for(int issue = 0; issue < game.getNumberIssues(); issue++)
					propose.setItem(issue,copyAllocate(allocated.getItem(issue)));
				boolean flag = false;
				free = this.getFreeProd();
				for(int i = 0; i < game.getNumberIssues(); i++)
				{
					if(free[i]>1) {
						flag= true;
						int[] arr =copyAllocate(allocated.getItem(i));
						arr[0]++;
						arr[2]++;
						arr[1] = arr[1]-2;
						free[i] = free[i]-2;
						propose.setItem(i, arr);
						this.lastAgentOfferAgent[i]++;
						this.lastAgentOfferPlayer[i]++;
					}
				}
				if(!flag) {
					this.lastAgentOfferPlayer= saverPlayer;
					this.lastAgentOfferAgent=saverAgent;
					this.lastOffer = propose;
					this.offer_num++;
					return null;
				}
				this.lastOffer = propose;
				this.offer_num++;
				return propose;
			}
		}
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
		int max = vhPref.size()+1;
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
		int max = 0;
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
		int max = 0;
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
		int max = playerPref.size()+1;
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
	
	private int[] copyAllocate(int[] allocatedArr) {
		
		int small[] = new int[allocatedArr.length];
		small[0]=allocatedArr[0];
		small[1]=allocatedArr[1];
		small[2]=allocatedArr[2];
		
		return small;
	}
	
	@Override
	protected Offer getTimingOffer(History history) {
		this.reject_local_num = 0;
		if(!TimingFlag) {
			TimingFlag = true;
			int[] free = this.getFreeProd();
			int index = this.getPlayerBestNow(free);
			int myIndex = this.getMyBestNow(free);
		
			Offer propose = new Offer(game.getNumberIssues());
			for(int issue = 0; issue < game.getNumberIssues(); issue++)
				propose.setItem(issue,copyAllocate(allocated.getItem(issue)));
		
			if(myIndex==-1 || index==-1) {
				return null;
			}
		
			if(index==myIndex && free[index]>1) {
				int[] arr =copyAllocate(allocated.getItem(index));
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
				int[] arr = copyAllocate(allocated.getItem(myIndex));
				arr[1] = arr[1]-1;
				arr[0]++;
				propose.setItem(index, arr);
				free[index]--;
				index = this.getPlayerBestNow(free);
				if(index==-1) {
					return null;
				}
				arr =copyAllocate(allocated.getItem(index));
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
		
			int[] arr = copyAllocate(allocated.getItem(myIndex));
			arr[1] = arr[1]-1;
			arr[0]++;
			propose.setItem(myIndex, arr);
			arr = copyAllocate(allocated.getItem(index));
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
		}else {
			TimingFlag = false;
			Offer propose = new Offer(game.getNumberIssues());
			propose = getRejectOfferFollowup(history);
			return propose;
		}
	
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
		this.reject_local_num = 0;
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
					int[ ] item = copyAllocate(of.getItem(i));
					of.setItem(i, new int[] {item[0]+1,item[1]-1,item[2]});
					this.lastAgentOfferAgent[i]++;
					free[i]--;
				}
			}
			for(int i=0;i<game.getNumberIssues();i++) {
				if (temp1[i]>0 && free[i]>0) {
					flagSamePlayer = true;
					int[ ] item = copyAllocate(of.getItem(i));
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
			int[ ] item = copyAllocate(of.getItem(i));
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
			int[ ] item =copyAllocate(of.getItem(i));
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
		if(myIndex==-1 || index == -1) {
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
			int arr3[] = copyAllocate(of.getItem(my));
			arr3[1]--;
			arr3[0]++;
			this.lastAgentOfferAgent[my]++;
			of.setItem(my,arr3);
			free[my]--;
			//give him a random prod

			free[index]=0;
			double chance =Math.random();
			int in = this.getPlayerBestNow(free);
			if(in==-1) {
				this.lastAgentOfferAgent = saverAgent;
				this.lastAgentOfferPlayer = saverPlayer;
				return null;
			}
			if(chance>0.8) {
				in = this.getRndomProd(free);
			}
			
			int arr2[] = copyAllocate(of.getItem(in));
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
			int arr3[] =copyAllocate(of.getItem(myIndex));
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
			
			
			int in = this.getPlayerBestNow(free);
			if(in==-1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			double chance =Math.random();
			if(chance>0.8) {
				in = this.getRndomProd(free);
			}
			
			
			
			int arr2[] = copyAllocate(of.getItem(in));
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
				int ar[]=copyAllocate(of.getItem(goodAgent));
				ar[0]++;
				ar[1]--;
				of.setItem(goodAgent, ar);
				int ar1[]=copyAllocate(of.getItem(goodPlayer));
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
		int free[] = this.getFreeProd();
		int agentFav = this.getMyBestNow(free);
		if(agentFav==-1) {return null;}
		
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue,copyAllocate(allocated.getItem(issue)));
		
		propose.setItem(agentFav, new int[] {1, allocated.getItem(agentFav)[1]-1, 0});
		
		
		int forPlayer = this.getMyWorstNow(free);
		if(forPlayer==-1) {return null;}
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
		return 0;
		//return Math.max(0, Math.min(game.getNumberIssues(), adverseEvents));//basic decaying will, starts with fair
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
	//int[] free = this.getFreeProd();
	int saverPlayer[] = this.lastAgentOfferPlayer;
	int saverAgent[] = this.lastAgentOfferAgent;
	this.lastAgentOfferPlayer=new int[game.getNumberIssues()];
	this.lastAgentOfferAgent=new int[game.getNumberIssues()];
	if(this.reject_local_num < 2) {
		this.reject_local_num++;
		Offer propose = new Offer(game.getNumberIssues());
		for(int issue = 0; issue < game.getNumberIssues(); issue++)
			propose.setItem(issue,copyAllocate(allocated.getItem(issue)));
		ArrayList<Integer> vhPref = utils.getMyOrdering();
		int agentVal = -1;
		int playerVal = -1;
		switch (rejectLevleCounter) {
		case 0: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 2 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 4 && this.lastOffer.getItem(issue)[1]>0) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				//random or move to the next case?
				this.rejectLevleCounter++;
				propose = getRejectOfferFollowup(history);
				return propose;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==1) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr =copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 =copyAllocate(propose.getItem(playerVal));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]++;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
			
		} case 1: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 1 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 3 && this.lastOffer.getItem(issue)[1]>0) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				//random or move to the next case?
				this.rejectLevleCounter++;
				propose = getRejectOfferFollowup(history);
				return propose;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==1) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr =copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 =copyAllocate(propose.getItem(playerVal));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]++;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
			
		} case 2: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 1 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 4 && this.lastOffer.getItem(issue)[1]>1) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				//random or move to the next case?
				this.rejectLevleCounter++;
				propose = getRejectOfferFollowup(history);
				return propose;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==2) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr = copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 = copyAllocate(propose.getItem(playerVal));
			arr1[2] = arr1[2]+2;
			arr1[1] = arr1[1]-2;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]+=2;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
	
		}case 3: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 1 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 2 && this.lastOffer.getItem(issue)[1]>0) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				//random or move to the next case?
				this.rejectLevleCounter++;
				propose = getRejectOfferFollowup(history);
				return propose;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==1) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr =copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 =copyAllocate(propose.getItem(playerVal));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]++;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
	
		} case 4: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 2 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 3 && this.lastOffer.getItem(issue)[1]>0) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				//random or move to the next case?
				this.rejectLevleCounter++;
				propose = getRejectOfferFollowup(history);
				return propose;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==1) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr =copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 =copyAllocate(propose.getItem(playerVal));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]++;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
	
		} case 5: {
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				if ( vhPref.get(issue) == 3 && this.lastOffer.getItem(issue)[1]>0) {
					agentVal = issue;
				}
				if (vhPref.get(issue) == 4 && this.lastOffer.getItem(issue)[1]>0) {
					playerVal = issue;
				}
			}
			if(agentVal == -1 || playerVal == -1) {
				this.rejectLevleCounter=0;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			if(playerVal == agentVal && this.lastOffer.getItem(agentVal)[1]==1) {
				this.rejectLevleCounter++;
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				return null;
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			int[] arr = copyAllocate(propose.getItem(agentVal));
			arr[0]++;
			arr[1] = arr[1]-1;
			propose.setItem(agentVal, arr);
			this.lastAgentOfferAgent[agentVal]++;
			int[] arr1 = copyAllocate(propose.getItem(playerVal));
			arr1[2]++;
			arr1[1] = arr1[1]-1;
			propose.setItem(playerVal, arr1);
			this.lastAgentOfferPlayer[playerVal]++;
			this.lastOffer = propose;
			this.offer_num++;
			this.rejectLevleCounter++;
			return propose;
		} case 6: {
			int[] free = new int[game.getNumberIssues()];
			//the case we always will go into
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				propose.setItem(issue,new int[] {this.lastOffer.getItem(issue)[0], this.lastOffer.getItem(issue)[1],this.lastOffer.getItem(issue)[2]});	
			}
			for(int issue = 0; issue < game.getNumberIssues(); issue++) {
				free[issue]= this.lastOffer.getItem(issue)[1];
			}
			int arr[] = new int[game.getNumberIssues()];
			int[] oldfree = this.getFreeProd();
			for(int i=0;i<oldfree.length;i++) {
				oldfree[i]+=saverAgent[i];
				oldfree[i]+=saverPlayer[i];
			}
			
			int myIndex = this.getMyBestNow(oldfree);
			int index = this.getPlayerBestNow(oldfree);
			boolean flagMyBest = false;
			if(myIndex==-1 || index == -1) {
				this.lastAgentOfferPlayer = saverPlayer;
				this.lastAgentOfferAgent = saverAgent;
				this.reject_local_num++;
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
					this.reject_local_num++;
					return null;
				}
				int arr3[] = copyAllocate(propose.getItem(my));
				arr3[1]--;
				arr3[0]++;
				this.lastAgentOfferAgent[my]++;
				propose.setItem(my,arr3);
				free[my]--;
				//give him a random prod

				free[index]=0;
				//double chance =Math.random();
				int in = this.getPlayerBestNow(free);
				if(in==-1) {
					this.lastAgentOfferAgent = saverAgent;
					this.lastAgentOfferPlayer = saverPlayer;
					this.reject_local_num++;
					return null;
				}
				int arr2[] =copyAllocate(propose.getItem(in));
				arr2[1]--;
				arr2[2]++;
				this.lastAgentOfferPlayer[in]++;
				
				propose.setItem(in,arr2);
				this.lastOffer = propose;
				this.offer_num++;
				this.reject_local_num++;
				return propose;
				
			}
			
			if(myIndex==this.getHisWorstNow(oldfree)) {
				myIndex = this.getMyBestNow(free);
				if(myIndex==-1) {
					this.lastAgentOfferAgent = saverAgent;
					this.lastAgentOfferPlayer = saverPlayer;
					this.reject_local_num++;
					return null;
				}
				int arr3[] = copyAllocate(propose.getItem(myIndex));
				arr3[1]--;
				arr3[0]++;
				propose.setItem(myIndex,arr3);
				free[myIndex]--;
				this.lastAgentOfferAgent[myIndex]++;
				if(free[myIndex]>0) {
					arr3[1]--;
					arr3[0]++;
					propose.setItem(myIndex,arr3);
					free[myIndex]--;
					this.lastAgentOfferAgent[myIndex]++;
				}
				
				//give him a random prod

		
				int in = this.getRndomProd(free);
				int arr2[] = copyAllocate(propose.getItem(in));
				arr2[1]--;
				arr2[2]++;
				this.lastAgentOfferPlayer[in]++;

				propose.setItem(in,arr2);
				this.lastOffer = propose;
				this.offer_num++;
				this.reject_local_num++;
				return propose;
			}
			
			int goodAgent = this.getMyBestNow(free);
			if(goodAgent>-1) {
				free[goodAgent]--;
				int goodPlayer= this.getPlayerBestNow(free);
				if(goodPlayer>-1) {
					int ar[]= copyAllocate(propose.getItem(goodAgent));
					ar[0]++;
					ar[1]--;
					propose.setItem(goodAgent, ar);
					int ar1[]= copyAllocate(propose.getItem(goodPlayer));
					ar1[2]++;
					ar1[1]--;
					propose.setItem(goodPlayer, ar1);
					this.lastOffer = propose;
					this.lastAgentOfferAgent[goodAgent]=1;
					this.lastAgentOfferPlayer[goodPlayer]=1;
					this.reject_local_num++;
					return propose;
				}
				
			}
			this.reject_local_num++;
			this.lastAgentOfferPlayer = saverPlayer;
			this.lastAgentOfferAgent = saverAgent;
			return null;
			
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + rejectLevleCounter);
		}
	} else {
		if(	this.reject_local_num > 3) {
			this.reject_local_num = 0;
			this.lastAgentOfferPlayer = saverPlayer;
			this.lastAgentOfferAgent = saverAgent;
			return null;
		}
		this.reject_local_num++;
		this.lastAgentOfferPlayer = saverPlayer;
		this.lastAgentOfferAgent = saverAgent;
		return null;
	}

	}
}
