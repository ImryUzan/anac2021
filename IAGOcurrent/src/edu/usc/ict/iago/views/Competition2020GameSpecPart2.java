package edu.usc.ict.iago.views;

import java.util.HashMap;
import edu.usc.ict.iago.utils.GameSpec;

class Competition2020GameSpecPart2 extends GameSpec{
	
	
	
	Competition2020GameSpecPart2(boolean privileged) 
	{

		//VH preferences
		
		HashMap<String, Integer> simpleVHPoints = new HashMap<String, Integer>();
		HashMap<String, Integer> simplePlayerPoints = new HashMap<String, Integer>();
		
		simpleVHPoints.put(getIssuePluralNames()[0], 1);
		simpleVHPoints.put(getIssuePluralNames()[1], 2);
		simpleVHPoints.put(getIssuePluralNames()[2], 3);
		simpleVHPoints.put(getIssuePluralNames()[3], 4);
		
		simplePlayerPoints.put(getIssuePluralNames()[0], 8);
		simplePlayerPoints.put(getIssuePluralNames()[1], 4);
		simplePlayerPoints.put(getIssuePluralNames()[2], 3);
		simplePlayerPoints.put(getIssuePluralNames()[3], 2);
		
		//Privileged calls -- cannot be called outside package.
		setSimpleOpponentPoints(simpleVHPoints);
		setSimpleUserPoints(simplePlayerPoints);	
		setUserBATNA(12);
		setOpponentBATNA(12);	
		if(privileged)
			enablePrivilege();
		
		
		
		setIndexMenu(this.buildMenu());
	}

	@Override
	public int getNumIssues() {
		return 4;
	}

	@Override
	public int[] getIssueQuants() {
		return new int[] {5,5,5,5};
	}

	@Override
	public String[] getIssuePluralNames() {
		return new String[] {"bars of iron", "bars of gold", "barrels of oil", "shipments of spices"};
	}

	@Override
	public String[] getIssueNames() {
		return new String[] {"bar of iron", "bar of gold", "barrel of oil", "shipment of spices"};
	}

	
	@Override
	public boolean isAdvancedPoints() {
		return false;
	}
	

	@Override
	public int getTotalTime() {
		return 420;
	}
	
	@Override
	public String getTargetEmail()
	{
		return "mell@ict.usc.edu";
	}
	

	@Override
	public String getEndgameMessage() {
		return "The game has ended!  Be prepared, a new game will start with the same opponent soon!";
	}
	
	@Override
	public String getNewgameMessage() {
		return "The items and your preferences have changed!  You need to negotiate a trade deal!  Click \"View Payoffs\" to view these again.  <strong>You now get a whopping 8 points for each bar of iron</strong>, " +
				"4 points for each bar of gold, 3 points for each barrel of oil, and only 2 points for each shipment of spices.  You have an alternative deal for 12 points!";
	}

	@Override
	public boolean showOpponentScoreOnEnd() {
		return false;
	}

	@Override
	public boolean showNegotiationTimer() {
		return true;
	}

	@Override
	public String getStudyName() {
		return "FavorPart2";
	}
	
	@Override
	public String getSurvey() {
		return "favorSurvey.jsp";
	}

}
