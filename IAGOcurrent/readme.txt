How to Install IAGO for Game_Edition only development

Prereqs:
a) Install Java 11 from their website (Java 12 will probably work, but is not officially supported).
b) Install Tomcat 8.5 from their website
c) Install Eclipse 2021-03 (EE Edition!) or newer from their website
d) Optional: install notepad++, and cygwin 

Main install:

1) Unzip the IAGO_Game_Edition zip into a new folder, called myIago (or whatever you'd like)
2) Open eclipse.  Immediately switch workspace to myIago
3) Server tab, add server, Apache --> Tomcat 8.5
4) Browse to C:/Program Files/Apache Software Foundation/Tomcat 8.5 --> Finish
5) Right click, New Project --> Web --> Dynamic Web Project --> Next --> uncheck default location --> browse to myIago/IAGO_Game_Edition
6) Project name = IAGO_Game_Edition. Target Runtime Tomcat 8.5. Finish
7) (If you forget to set the Target Runtime in 6), then: Right-click project --> Configure Build Path --> Libraries tab --> Add Library --> Server Runtime --> Tomcat 8.5
8) Make sure the file IAGO_Core.jar is in IAGO_Game_Edition/WEB-INF/lib 
9) Back to servers.  Tomcat v8.5 server--> right click --> Start
10) Double click Tomcat v8.5 server --> Deploy path = webapps, Server location = "Use Tomcat Installation"  Save
11a) If no error, continue to 12
11b) If error, make sure permissions for the Apache Software Foundation (or at least Tomcat 8.5) are set to all for all users.  Make a change (e.g., retype the deploy path again), save, restart server.
12) Go to modules tab on the server.  Add Web Module --> select IGE, set deploy path to "IAGO" Ok
13) Table should now read: Path: /IAGO_Game_Edition | Document Base: IAGO_Game_Edition | Module: IAGO_Game_Edition | Auto Reload: Enabled. 
14) Edit.  Change path to /IAGO because Eclipse sometimes has a bug here.
15) Save.  Start server.
16) You should now be able to access IAGO in a web browser at localhost:8080/IAGO.

How to use the config file:

If you want to simulate two agents, use two agents.  If you want the standard human vs. agent, only include one agent name (it must be agent1, not agent0).
If you want multiple negotiations, include multiple comma-separated GameSpec classes.  Otherwise, include only one.
If you want your agent to be able to say things that are NOT in the GameSpec, you must disable Turing Mode.


