package edu.usc.ict.iago.views;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


import com.google.gson.Gson;

import edu.usc.ict.iago.agent.IAGODummyShell;
//import edu.usc.ict.iago.agent.IAGOChenVH;
import edu.usc.ict.iago.utils.*;
import edu.usc.ict.iago.utils.Governor.SessionState;
import edu.usc.ict.iago.utils.GameBridgeUtils.NegotiationMode;

/**
 * This the GameBridge, the connector between the web hosting front end of IAGO and the back end.
 * You probably don't want to modify anything here.  Perhaps you should make your changes in config.txt?
 * Note that if you are not developing your own experiment (e.g., you're competing in ANAC), your config.txt
 * will NOT be saved.
 * @author Johnathan Mell
 *
 */
@ServerEndpoint(value = "/game/ws", configurator = GetHttpSessionConfigurator.class)
@WebServlet(name="IagoGame", loadOnStartup=1, description = "This servlet loads the Iago platform.", urlPatterns = { "/game" })
public class GameBridge extends HttpServlet  {

	private static final long serialVersionUID = 2669425387121437625L;
		
	private GameBridgeUtils u;
	private GameSpec p_gs; //utility for getting GS info.  Careful: only useful for getter methods, and only then AFTER construction.  Privileged version.
	private GameSpec u_gs; //utility for getting GS info.  Careful: only useful for getter methods, and only then AFTER construction.  Unprivileged version.
	private NegotiationRoom nRoom;
	private String vhQualifiedName0;
	private String vhQualifiedName1;
	private ArrayList<String> allGameSpecNames; 
	private int currentGame = 0;
	private GeneralVH storedVH0;
	private GeneralVH storedVH1;
	private String globalCondition;
	private HttpSession httpSession;
	private String globalMTurkID;		//this gets passed back into Qualtrics after the game ends if the user is coming from a Qualtrics study
	private boolean programmed;		//this lets us know whether or not the system is in programming mode
	private int mostPrefIndex; //user-inputted favorite item
	private int leastPrefIndex; //user-inputted favorite item
	private boolean useDynamicPreferences = false; //should we adjust the gameSpec
	private boolean turingMode;

	
	//private Logger LOGGER = LogManager.getLogger(GameBridge.class.getName());
	Logger logger = Logger.getLogger(GameBridge.class.getName());

	/**
	 * Constructor for the GameBridge.
	 * @throws Exception
	 */
	public GameBridge() throws Exception 
	{
		String email_username = "";
		String email_pass = "";
		String email_sender_name = "";
		String email_smtpAuth = "";
		String email_smtpHost = "";
		String email_smtpPort = "";
		String gameSpecMultiName = "";
		boolean dataMode_log = false;
		boolean dataMode_email = false;
		boolean dataMode_db = false;
		ServletUtils.DebugLevels debug = ServletUtils.DebugLevels.ERROR;
		//logger.log(Level.INFO, "I am a test");
		
		//LOGGER.warn("test i'm a test look at me test so test");
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null)
		{
			ServletUtils.log("Context class loader is null--resources may be improperly loaded", ServletUtils.DebugLevels.WARN);
			classLoader = ClassLoader.getSystemClassLoader();
			if (classLoader == null)
				ServletUtils.log("System class loader is null--cannot load properly.", ServletUtils.DebugLevels.ERROR);
		}
		InputStream input = classLoader.getResourceAsStream("config.txt");
		if(input == null)
			ServletUtils.log("Configuration file not found!", ServletUtils.DebugLevels.ERROR);
		Properties properties = new Properties();
		try {
			properties.load(input);

			vhQualifiedName0 = properties.getProperty("agent0");
			vhQualifiedName1 = properties.getProperty("agent1");
			gameSpecMultiName = properties.getProperty("gamespec");
			email_username = properties.getProperty("email_username");
			email_pass = properties.getProperty("email_pass");
			email_sender_name = properties.getProperty("email_sender_name");
			email_smtpAuth = properties.getProperty("email_smtpAuth");
			email_smtpHost = properties.getProperty("email_smtpHost");
			email_smtpPort = properties.getProperty("email_smtpPort");

			this.turingMode = properties.getProperty("turingMode").equals("enabled");
			dataMode_log = properties.getProperty("dataMode_log").equals("enabled");
			dataMode_email = properties.getProperty("dataMode_email").equals("enabled");
			dataMode_db = properties.getProperty("dataMode_db").equals("enabled");
			
			debug = ServletUtils.DebugLevels.valueOf(properties.getProperty("debugLevel"));
			
		} catch (IOException e) {
		}
		
		ServletUtils.setDebug(debug);
		ServletUtils.setDataModeDb(dataMode_db);
		ServletUtils.setDataModeEmail(dataMode_email);
		ServletUtils.setDataModeLog(dataMode_log);
		ServletUtils.setCredentials(email_username, email_pass, email_sender_name, email_smtpAuth, email_smtpHost, email_smtpPort);
		
		if (vhQualifiedName0 == null || vhQualifiedName0.equals(""))
			vhQualifiedName0 =  null;
		if (vhQualifiedName1 == null || vhQualifiedName1.equals(""))
			vhQualifiedName1 =  null;
		
		if (gameSpecMultiName == null || gameSpecMultiName.equals(""))
			gameSpecMultiName =  null;
		
		//account for multiple games
		allGameSpecNames = new ArrayList<String> (Arrays.asList(gameSpecMultiName.split("\\s*,\\s*")));
		ServletUtils.log("We found the following GameSpecs: " + allGameSpecNames.toString(), ServletUtils.DebugLevels.DEBUG);

		u = new GameBridgeUtils();
	
	}
	
	@OnOpen
	public void onOpen(javax.websocket.Session session, EndpointConfig config) 
	{	
		ServletUtils.log("ws open", ServletUtils.DebugLevels.DEBUG);

		//The following loads the appropriate agents based on the received input:
		try {
			
			GeneralVH vh0 = null;	
			
			this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());	//access the HTTP session to get the data needed to construct the agent (this needs to go above the control statements below because gameChoice determines path)
			
			String mostPrefIndexTemp = httpSession.getAttribute("mostPrefIndex") == null ? null : (String)httpSession.getAttribute("mostPrefIndex");
			String leastPrefIndexTemp = httpSession.getAttribute("leastPrefIndex") == null ? null : (String)httpSession.getAttribute("leastPrefIndex");
			if (mostPrefIndexTemp != null && leastPrefIndexTemp != null && !mostPrefIndexTemp.equals("") && 
					!leastPrefIndexTemp.equals("") && !mostPrefIndexTemp.equalsIgnoreCase("null") && !leastPrefIndexTemp.equalsIgnoreCase("null"))
			{
				useDynamicPreferences = true;
				this.mostPrefIndex= Integer.parseInt(mostPrefIndexTemp);
				this.leastPrefIndex= Integer.parseInt(leastPrefIndexTemp);
			}
			
			this.buildPrivilegedGameSpec();
			this.buildUnprivilegedGameSpec();
			
			u.setGameSpec(this.p_gs);
			
			String gameChoice = (String)httpSession.getAttribute("gameChoice");				//user's game choice (same as above line - must be considered first to determine control flow)
			String qFlag = (String)httpSession.getAttribute("qualtricsFlag");				//determines if user is coming from Qualtrics survey or not
			if (qFlag == null)
				qFlag = "OFF";																//makes it easier to do GET requests
			globalMTurkID= (String)httpSession.getAttribute("MTurkID");
			globalCondition = (String)httpSession.getAttribute("condition");
			if (globalMTurkID == null || globalMTurkID.equals("") || globalMTurkID.equals("false"))
				globalMTurkID = "MISSING";
			if (globalCondition == null || globalCondition.equals("") || globalCondition.equals("false"))
				globalCondition = "MISSING";
			String q1 = (String)httpSession.getAttribute("qualtricsQ1");
			String exp = "";
			if(q1 != null)
			{
				if(q1.equals("Positive")) {
					exp = "Happy";
				}
				else if(q1.equals("Negative")) {
					exp = "Angry";
				}
				else {
					exp = q1;
				}
			}
			String q2 = (String)httpSession.getAttribute("qualtricsQ2");
			String q3 = (String)httpSession.getAttribute("qualtricsQ3");
			String q4 = (String)httpSession.getAttribute("qualtricsQ4");
			if(q1 == null || q2 == null || q3 == null || q4 == null) {
				gameChoice = "player";
			}
			programmed = qFlag.equals("ON") ? true : false;
			
			ServletUtils.log("Game Choice: " + gameChoice, ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Qualtrics Flag: " + qFlag, ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("MTurkID: " + globalMTurkID, ServletUtils.DebugLevels.DEBUG);
			
			if (vhQualifiedName0 == null && gameChoice.equals("agent") && programmed) //agent is being programed via q1-q4
			{
				String concatUserChoice = "edu.usc.ict.iago.agent.IAGO" + q2 +exp +q1 + q3 +q4 + "VH"; 
				ServletUtils.log("Agent choice: " + concatUserChoice, ServletUtils.DebugLevels.DEBUG);

				//Displaying data in logs to make sure everything works correctly:
				ServletUtils.log("Hopefully working real agent choice: " + concatUserChoice, ServletUtils.DebugLevels.DEBUG);
				
				@SuppressWarnings("unchecked")
				Class<? extends GeneralVH> vhclass = (Class<? extends GeneralVH>) Class.forName(concatUserChoice);	//agent construction happens here and is built with above concatenated string
				
				Constructor<? extends GeneralVH> ctor0 = vhclass.getConstructor(String.class, GameSpec.class, javax.websocket.Session.class); 
				vh0 = ctor0.newInstance(new Object [] {"defaultAgent", this.u_gs, session});
				storedVH0 = vh0;
				ServletUtils.log("We found the following user agent: " + storedVH0.toString(), ServletUtils.DebugLevels.DEBUG);
				//If vh0 isn't safe for MA games, do below block:
				if (!vh0.safeForMultiAgent()) {
					ServletUtils.log("One of your chosen agents is not capable of multi-agent. Reverted to single player.", ServletUtils.DebugLevels.DEBUG);
					vh0 = null;
					storedVH0 = null;
				}
				p_gs.setMultiAgent(vh0.safeForMultiAgent());
			} 
			else if(vhQualifiedName0 != null) //a standard, non-Qualtrics agent has been registered via config.txt
			{
				@SuppressWarnings("unchecked")
				
				Class<? extends GeneralVH> vhclass = (Class<? extends GeneralVH>) Class.forName(vhQualifiedName0);
				Constructor<? extends GeneralVH> ctor0 = vhclass.getConstructor(String.class, GameSpec.class, javax.websocket.Session.class); 
				vh0 = ctor0.newInstance(new Object [] {"defaultAgent", this.u_gs, session});
				storedVH0 = vh0;
				ServletUtils.log("We found the following user agent: " + storedVH0.toString(), ServletUtils.DebugLevels.DEBUG);
				//If vh0 isn't safe for MA games, do below block:
				if (!vh0.safeForMultiAgent()) {
					ServletUtils.log("One of your chosen agents is not capable of multi-agent. Reverted to single player.", ServletUtils.DebugLevels.DEBUG);
					vh0 = null;
					storedVH0 = null;
				}
				p_gs.setMultiAgent(vh0.safeForMultiAgent());
			}
			else//no agent should play this seat (normal mode)
			{
				ServletUtils.log("We found NO user agents." , ServletUtils.DebugLevels.DEBUG);
				storedVH0 = vh0;
			}
			
			GeneralVH vh1 = null;
			if (vhQualifiedName1 != null) {
				@SuppressWarnings("unchecked")
				Class<? extends GeneralVH> vhClass1 = (Class<? extends GeneralVH>) Class.forName(vhQualifiedName1);
				
				//Class<? extends GeneralVH> vhClass1 = (Class<? extends GeneralVH>) Class.forName(agentChoice);
				Constructor<? extends GeneralVH> ctor1 = vhClass1.getConstructor(String.class, GameSpec.class, javax.websocket.Session.class); 
				vh1 = ctor1.newInstance(new Object [] {"defaultAgent", this.u_gs, session});
			} 
			else
			{ //human-human case
				ServletUtils.log("Computer agent is null, should be human-human case", ServletUtils.DebugLevels.DEBUG);
				
				SessionState state = Governor.getSessionState(httpSession);
				if(state == SessionState.NEGOTIATING) {
					WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("ERR", "You have an unclosed negotiation already.")), session);
					return;
				}
				nRoom = Governor.findUserByHttpSession(httpSession);
				if(nRoom == null) {
					ServletUtils.log("Error, no previous connection", ServletUtils.DebugLevels.DEBUG);
				} else { //update new websocket
					WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("MSG", "Test")), session);	
					nRoom.setGBU(httpSession, this.u);
					nRoom.updateSocket(httpSession, session);
					Governor.startNegotiating(httpSession);
				}
				
				//UserSession user = nRoom.getAdversary(httpSession); //opponent's session info
				//u.setSessionOther(user.getHttpSession(), user.getWsSession());
				
				u.setNegotiationMode(NegotiationMode.HUMAN_HUMAN);
				u.setNRoom(nRoom);
				vh1 = new IAGODummyShell("human", this.u_gs, session); //TODO Will be overhauled in IAGO 3.0 as part of the human-human features.
			}
			
			u.setMultiplayer(gameChoice.equals("agent") ? true : false);
			//vh1.setAgentVsAgent(gameChoice);
			//PERSONAL NOTE: onOpenHelper method activates here, linking to GBUtils class:
			u.onOpenHelper(session, config, vh0, vh1); 
			
			storedVH1 = vh1;
			ServletUtils.log("We found the following computer agent: " + storedVH1.toString(), ServletUtils.DebugLevels.DEBUG);
			if (vh0 != null && !vh1.safeForMultiAgent()) {
				ServletUtils.log("One of your chosen agents is not capable of multi-agent. Reverted to single player.", ServletUtils.DebugLevels.DEBUG);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("We were unable to load the primary VH file from the class name provided in the configuration file.");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println("You have not provided a constructor that meets the requirements!");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			System.err.println("Your VH did not cast properly.  Did it extend GeneralVH?");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (storedVH0 != null) 
		{
			storedVH0.setGameSpec(this.u_gs);
			this.p_gs.setMultiAgent(true);
		}
		storedVH1.setGameSpec(u_gs);

	}
	
	@OnClose
	public void onClose(javax.websocket.Session session, javax.websocket.CloseReason cr) {
		u.onCloseHelper(session, cr);
	}
	
	@OnMessage
	public void onMessage(javax.websocket.Session session, String msg, boolean last) 
	{
		WebSocketUtils.JsonObject joIn = (WebSocketUtils.JsonObject)AccessController.doPrivileged(new PrivilegedAction<Object>()
		{
			public Object run() {
				return new Gson().fromJson(msg, WebSocketUtils.JsonObject.class);
			}
			
		});
		if(joIn.tag.equals("ngPing"))
		{
			currentGame++;
			ServletUtils.log("Ping received for new game", ServletUtils.DebugLevels.DEBUG);	
			if(currentGame > allGameSpecNames.size() - 1)
			{
				if(globalMTurkID.equals("MISSING")) 
				{
					ServletUtils.log("GlobalID was not found!  This data may end up as trash! Did you include ?id=<arg> as a GET parameter in your study link?", ServletUtils.DebugLevels.ERROR);
				}
				WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("trueEnd", 
						this.u_gs.getRedirectLink() + "?id=" + globalMTurkID + "&condition=" + globalCondition)), session);
				//WebSocketUtils.close(session);
			}
			else
			{
				try {
					
					buildPrivilegedGameSpec();
					u.setGameSpec(this.p_gs);
					buildUnprivilegedGameSpec();	
					if (storedVH0 != null) {
						storedVH0.setGameSpec(this.u_gs);
						this.p_gs.setMultiAgent(true);
					}
					storedVH1.setGameSpec(u_gs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		else
			u.onMessageHelper(session, msg, last);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{		
		ServletUtils.log("Post request", ServletUtils.DebugLevels.DEBUG);
		u.doPostHelper(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{		
		u.doPostHelper(request, response);//TODO doGetHelper will be fully deprecated soon
	}
	
	/**
	 * Creates a privileged GameSpec, for IAGO itself to see information like points.
	 * @throws Exception
	 */
	private void buildPrivilegedGameSpec() throws Exception
	{
		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {true});
			if(useDynamicPreferences)
				gs.adjustDynamicPreferences(this.mostPrefIndex, this.leastPrefIndex);
			gs.setTuringMode(this.turingMode);
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			throw new Exception("Unknown problem creating GameSpec class from configuration.");
		
		this.p_gs = gs;
	}
	
	/**
	 * Creates an unprivileged GameSpec, for agents who are not supposed to see key information like points.
	 * @throws Exception
	 */
	private void buildUnprivilegedGameSpec() throws Exception
	{
		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {false});
			Field prv = gs.getClass().getSuperclass().getDeclaredField("isPrivileged");
			prv.setAccessible(true);
			prv.setBoolean(gs, true);
			if(useDynamicPreferences)
				gs.adjustDynamicPreferences(this.mostPrefIndex, this.leastPrefIndex);
			gs.setTuringMode(this.turingMode);
			prv.setBoolean(gs, false);
			prv.setAccessible(false);
		
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			try {
				throw new Exception("Unknown problem creating GameSpec class from configuration.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		this.u_gs = gs;
		
		
	}
}
