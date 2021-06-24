package edu.usc.ict.iago.views;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import edu.usc.ict.iago.utils.GetHttpSessionConfigurator;
import edu.usc.ict.iago.utils.WebSocketUtils;
import edu.usc.ict.iago.utils.Governor;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.ServletUtils.DebugLevels;

/**
 * Servlet implementation class Start
 */
@ServerEndpoint(value = "/ws", configurator = GetHttpSessionConfigurator.class)
@WebServlet(name="Start", loadOnStartup=1, description = "Starting servlet for opening screen.", urlPatterns = { "" })
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession httpSession;
	private String modeFlag;
	
	public Start() {
		//DatabaseUtils.connect();
	}
//	boolean special;
	boolean vh;

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		
		this.httpSession = (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
		modeFlag = httpSession.getAttribute("gameMode") == null ? "" : httpSession.getAttribute("gameMode").toString();
		if(modeFlag == null || !modeFlag.equals("HH"))
		{
			ServletUtils.log("gameMode was: " + modeFlag + ", not HH--proceeding in normal mode...", ServletUtils.DebugLevels.DEBUG);
			return;
		}
		if(Governor.register(this.httpSession, session) == false) {
			WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("REG", "You have already registered.")), session);
		};
		System.out.println(this.httpSession);
		//System.out.println("Registered");
		//WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("MATCH", "You will now be redirected.")), session);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason cr) {
		if(session.getId().equals("1000")) {
			System.out.println("Page has been redirected or closed! Logout the user!");
			//ServletUtils.log("User " + session + " has closed the page", DebugLevels.DEBUG);
		}
		Governor.exitInWaiting(this.httpSession, session);
		System.out.println("Exit in waiting");
		ServletUtils.log("Startpage closed " + session.getId() + "--" + cr.toString(), DebugLevels.DEBUG);
	}
	
	@OnError
	public void onError(Throwable error) {
		
	}
	
	@OnMessage
	public void onMessage(Session session, String msg) {
		System.out.println("Users send: " + msg);
		//WebSocketUtils.send("Message from server", session);
	}
	

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession(true).setAttribute("gameMode",request.getParameter("gameMode"));
		request.getRequestDispatcher("start.jsp").forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	}
	
}
