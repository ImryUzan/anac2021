package edu.usc.ict.iago.agent;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.ExpressionPolicy;
import edu.usc.ict.iago.utils.History;

public class RepeatedFavorExpression extends IAGOCoreExpression implements ExpressionPolicy{
	
	protected String getSemiFairEmotion()
	{
		return "happy";
	}
	
	protected String getFairEmotion()
	{
		return "happy";
	}
	
	protected String getUnfairEmotion()
	{
		return "sad";
	}

	@Override
	public String getExpression(History history) 
	{
		Event last = history.getUserHistory().getLast();
		if(last.getType().equals(Event.EventClass.SEND_EXPRESSION)){
			if(last.getMessage().equals("sad") || last.getMessage().equals("neutral"))
				return "sad";
			else if(last.getMessage().equals("happy"))
				return "happy";
			else if(last.getMessage().equals("surprised"))
				return "neutral";
			else if(last.getMessage().equals("angry"))
				return "sad";
			else
				return null;
		} 
		else if (last.getType().equals(Event.EventClass.SEND_MESSAGE))
		{
			Event.SubClass type = last.getSubClass();
			switch(type)
			{
				case BATNA_INFO:
					return "happy";
				case BATNA_REQUEST:
					return null;
				case CONFUSION:
					return "sad";
				case FAVOR_ACCEPT:
					return "happy";
				case FAVOR_REJECT:
					return "sad";
				case FAVOR_REQUEST:
					return null;
				case FAVOR_RETURN:
					return "happy";
				case GENERIC_NEG:
					return "sad";
				case GENERIC_POS:
					return "happy";
				case NONE:
					return null;
				case OFFER_ACCEPT:
					return "happy";
				case OFFER_REJECT:
					return "sad";
				case OFFER_REQUEST_NEG:
					return "sad";
				case OFFER_REQUEST_POS:
					return null;
				case PREF_INFO:
					return "happy";
				case PREF_REQUEST:
					return null;
				case PREF_SPECIFIC_REQUEST:
					return null;
				case PREF_WITHHOLD:
					return "sad";
				case THREAT_NEG:
					return "sad";
				case THREAT_POS:
					return "sad";
				case OFFER_PROPOSE:
					return null;
				case TIMING:
					return null;
				default:
					return null;			
			}
		}
		else
			return null;
		
	}
}
