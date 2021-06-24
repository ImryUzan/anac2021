package edu.usc.ict.iago.views;

import java.util.ArrayList;
import java.util.HashMap;
import edu.usc.ict.iago.utils.GameSpec;

class ViralGameSpec extends GameSpec{
	
	
	
	ViralGameSpec(boolean privileged) 
	{

		//VH preferences		
		HashMap<String, Integer> simpleVHPoints = new HashMap<String, Integer>();
		HashMap<String, Integer> simplePlayerPoints = new HashMap<String, Integer>();
		ArrayList<String> plurals = new ArrayList<String>();
		ArrayList<String> singulars = new ArrayList<String>();
		ArrayList<Integer> quants = new ArrayList<Integer>();
		
		plurals.add("lamps");
		plurals.add("paintings");
		plurals.add("boxes of records");
		plurals.add("cuckoo clocks");
		
		singulars.add("lamp");
		singulars.add("painting");
		singulars.add("box of records");
		singulars.add("cuckoo clock");
		
		quants.add(5);
		quants.add(5);
		quants.add(5);
		quants.add(5);
		
		//Privileged calls -- cannot be called outside package.
		setNumIssues(4);
		try {
			setIssuePluralText(plurals);
			setIssueSingularText(singulars);
			setQuantities(quants);
		} catch (Exception e) {
			e.printStackTrace();
		}

		simpleVHPoints.put(getIssuePluralText().get(0), 2);
		simpleVHPoints.put(getIssuePluralText().get(1), 1);
		simpleVHPoints.put(getIssuePluralText().get(2), 4);
		simpleVHPoints.put(getIssuePluralText().get(3), 3);
		
		simplePlayerPoints.put(getIssuePluralText().get(0), 2);
		simplePlayerPoints.put(getIssuePluralText().get(1), 3);
		simplePlayerPoints.put(getIssuePluralText().get(2), 4);
		simplePlayerPoints.put(getIssuePluralText().get(3), 1);
		
		setSimpleOpponentPoints(simpleVHPoints);
		setSimpleUserPoints(simplePlayerPoints);	
		setUserBATNA(12);
		setOpponentBATNA(12);	
		if(privileged)
			enablePrivilege();
		
		
		
		
		
		
		setIndexMenu(this.buildMenu());
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
		return "test";
	}
	

	@Override
	public String getEndgameMessage() {
		return "The game has ended!  You'll now be redirected to a final survey!";
	}
	
	@Override
	public String getNewgameMessage() {
		return "Time to negotiate for some antiques!  Click \"View Payoffs\" to view these again. You now get 4 points for each box of records, "
				+ " 3 points for each painting, 2 points for each lamp, and <strong>only 1 point for each cuckoo clock.</strong>  You will receive only 12 points if you fail to reach an agreement. Try to get more!";
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
	public String getRedirectLink()
	{
		return "https://jtmell.com";
	}

	@Override
	public String getStudyName() {
		return "ViralStudyPart2";
	}
	
	@Override
	public String getSurvey() {
		return "";
	}

}
