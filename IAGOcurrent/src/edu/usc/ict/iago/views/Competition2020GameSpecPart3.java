package edu.usc.ict.iago.views;

import java.util.HashMap;
import edu.usc.ict.iago.utils.GameSpec;

class Competition2020GameSpecPart3 extends GameSpec{
	
	
	
	Competition2020GameSpecPart3(boolean privileged) 
	{

		//VH preferences
		
		HashMap<String, Integer> simpleVHPoints = new HashMap<String, Integer>();
		HashMap<String, Integer> simplePlayerPoints = new HashMap<String, Integer>();
		
		simpleVHPoints.put(getIssuePluralNames()[0], 4);
		simpleVHPoints.put(getIssuePluralNames()[1], 2);
		simpleVHPoints.put(getIssuePluralNames()[2], 3);
		simpleVHPoints.put(getIssuePluralNames()[3], 1);
		
		simplePlayerPoints.put(getIssuePluralNames()[0], 1);
		simplePlayerPoints.put(getIssuePluralNames()[1], 3);
		simplePlayerPoints.put(getIssuePluralNames()[2], 2);
		simplePlayerPoints.put(getIssuePluralNames()[3], 4);
		
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
		return new String[] {"apples", "oranges", "pears", "bananas"};
	}

	@Override
	public String[] getIssueNames() {
		return new String[] {"apple", "orange", "pear", "banana"};
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
		return "The game has ended!  You'll be redirected to finish soon.";
	}
	

	@Override
	public String getNewgameMessage() {
		return "The items and your preferences have changed!  You need to negotiate at a market stall!  Click \"View Payoffs\" to view these again."
				+ "You now get 4 points for each banana, 3 points for each orange, 2 points for each pear, and <strong>only 1 point for each apple.</strong>  You have an alternative deal for 12 points!";
	}
	
	@Override
	public String getRedirectLink()
	{
		return "https://usc.qualtrics.com/jfe/form/SV_7QebxGIeZ5sZA7H";
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
		return "FavorPart3";
	}
	
	@Override
	public String getSurvey() {
		return "favorSurvey.jsp";
	}

}
