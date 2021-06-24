package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Preference;

public abstract class IAGOCoreMessage implements MessagePolicy
{
	public HashMap<String, Integer> codeMap = new HashMap<String, Integer>();	

	public abstract void updateOrderings (ArrayList<ArrayList<Integer>> orderings);

	protected abstract String getEndOfTimeResponse();

	protected abstract String getSemiFairResponse();
	
	protected String getVHEqualLang()
	{
		return "That deal seems about the same as what we had before...";
	}

	protected abstract String getContradictionResponse(String drop);

	protected abstract void setUtils(AgentUtilsExtension utils);

	protected boolean getLying(GameSpec game)
	{
		return false;
	}

	protected String getProposalLangChange()
	{
		return "Is this better for you?";
	}

	protected String getProposalLangFirst()
	{
		return "How's this to start?";
	}

	protected String getProposalLangRej()
	{
		return "That offer didn't work. How about this one?";
	}

	protected String getProposalLangOneOffer()
	{
		return "Let's try to come to an agreement on a partial offer.";
	}

	protected String getProposalLangRepeat()
	{
		return "I proposed this before but this offer is really good!";
	}

	/**
	 * Provides an English phrase that accurately reflects the preference specified.
	 * @param preference
	 * @param game
	 * @return an english preference phrase
	 * TODO will be replaced in IAGO 3.0 with the language overhaul
	 */
	protected static String prefToEnglish(Preference preference, GameSpec game)
	{
		String ans = "";
		if (preference.isQuery())
			ans += "Do you like ";
		else
			ans += "I like ";

		if (preference.getIssue1() >= 0)
			ans += game.getIssuePluralText().get(preference.getIssue1()) + " ";
		else
			ans += "something ";
		switch (preference.getRelation())
		{
		case GREATER_THAN:
			ans += "more than ";
			if (preference.getIssue2() >= 0)
				ans += game.getIssuePluralText().get(preference.getIssue2());
			else
				ans += "something else.";
			break;
		case LESS_THAN:
			ans += "less than ";
			if (preference.getIssue2() >= 0)
				ans += game.getIssuePluralText().get(preference.getIssue2());
			else
				ans += "something else.";
			break;
		case BEST:
			ans += "the best";
			break;
		case WORST:
			ans += "the least";
			break;
		case EQUAL:
			ans += "the same as ";
			ans += game.getIssuePluralText().get(preference.getIssue2());
			break;
		}
		ans += preference.isQuery() ? "?" : ".";

		return ans;
	}

	protected Event getFavorBehavior(History history, GameSpec game, Event e)
	{
		return null;
	}
	
	protected String getWaitingLang(History history, GameSpec game)
	{
		return "...";
	}

}
