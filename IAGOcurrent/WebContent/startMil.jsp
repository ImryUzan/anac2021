<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link href="css/iago.css" rel="stylesheet">

<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css" />

<script src="js/start.js" type="text/javascript"></script>

<title>ICT Experiment - Start</title>
</head>
<!-- NOTE: Much of the body text on these page appears to be repeated to account for both game conditions.  When users come from Qualtrics the correct text is loaded depending on their condition. -->
<body>
	<div id="mainpage">
	<div class="welcome instructions">
		
	</div>
	<div class="reminder instructions">
		<p/>
		Please read the instructions below. <strong>Pay special attention to the bold sections!</strong>
	</div>
	
	<hr>
	<div class="reminder instructions">
		<div id="standardText">
		
			<h1>Overview:</h1>
			<br>
			<div id="ava1">
			Your virtual agent is about to engage in a series of negotiation games with a partner.  The objective is to decide how to divide a set of items.<br>
			If your virtual agent can agree with your partner, you'll receive the points allocated on your side when you agreed.<br>
			Keep in mind that your agent be playing <strong>3 games</strong>, so watch out for opportunities to do well in the long-run, such as by <strong>exchanging favors!</strong><br>
			</div>
			<div id="pva1">
			You are about to engage in a series of negotiation games with a partner. The objective is to decide how to divide a set of items.<br>
			If you can agree with your partner, you'll receive the points allocated on your side when you agreed.<br>
			</div>
			<br>
			The negotiation consists of 4 items: equipment sets, MREs, ammo boxes, and fuel crates. <br>
			<br>
			<div id="ava2">
			In the first game, your agent gets <strong>4 points for each box of records, 3 points for each of the paintings, 2 points for each of the lamps, and only 1 point for each cuckoo clock</strong>.  <br>
			This means that the records are worth the most to it!  Your opponent may want the same items, <strong>or they may not</strong>.<br>
			</div>
			<div id="pva2">
			You get <strong>6 points for each equipment set, 4 points for each fuel can, 2 points for each ammunition box, and only 1 point for each MRE</strong>.<br>
			This means that the equipment sets are worth the most to you!  Your opponent may want the same items you do, <strong>or they may not</strong>.  Talking to your partner can help reveal what items they may want.<br>
			</div>
			<br>
			<hr>
			<h1>About the Game Board:</h1>
			<br>
			Below is a picture of the game board. The chat log is on the right, and a picture of your partner on the left. In the bottom half, there is a trade table and buttons.  Near your partner's picture, you may see tips appear to help guide you!
			<br>
			<div id="pva3">
			In the game, you can send messages and questions to your opponent. You can also move items around on the game board, and send offers. <br>
			Everything you do will appear in the chat log on the right side of the screen so you can look it over.<br>
			</div>
			
			<div id="ava3">
			In the game, your agent can send messages and questions to your opponent. It can also move items around on the game board, and send offers. <br>
			Everything it does will appear in the chat log on the right side of the screen so you can look it over.<br>
			</div>
		    <br>
		</div>
		<div>
			<img class="instruction-pic" id="instr_whole" alt="Picture of the game board" src="img/instr_whole.PNG" width="590" height="426" />
		</div>

		<hr>
		<br>
		<h1>About the Trade Table:</h1> <br>
		<div id="ava4">
		Below is the trade table. With the trade table <strong>your agent can send offers to your partner</strong>.  You cannot interact with this interface and must watch your agent do so for you.
		    Your agent can click any item to pick it up, then click again to place it.
			For example, it can click one of the lamps in the middle and then click it to your side.  It can click multiple times for more items.  Nothing sends until it clicks "Send Offer".<br><br>
			It can also accept or reject <strong>PARTIAL</strong> offers that your partner sends it. <strong>These offers aren't binding, but are helpful in building towards a full offer</strong>.<br>
			Pressing "Formal Accept" is only possible if ALL items are either on its side or your partner's.  If they both agree, the game is finished!  <br><br>
		</div>
		<div id="pva4">
		Below is the trade table. With the trade table <strong>you are able to send offers to your partner</strong>.  It will start grayed out.  Click "Start Offer" to enable it.  You can click any item to pick it up, then click again to place it.<br>
			For example, you can click one of the lamps in the middle and then click it to your side.  You can click multiple times for more items.  Nothing sends until you click "Send Offer".<br><br>
			You can also accept or reject <strong>PARTIAL</strong> offers that your partner sends you. <strong>These offers aren't binding, but are helpful in building towards a full offer</strong>.<br>
			Pressing "Formal Accept" is only possible if ALL items are either on your side or your partner's.  If you both agree, the game is finished!  <br><br>
		</div>
			
		<div>
			<img class="instruction-pic" id="instr_table" alt="Picture of the table" src="img/instr_table.PNG" width="543" height="347" />
		</div>
		<br>
		<hr>
		<br>
		<h1>About Emoticons and Avatars:</h1>
		<br> 
		<div id="pva5">
		The buttons you see below can be used to send emoticons in chat! The blinking emoticon is representing your current emotional state. Use it to communicate how you feel about the negotiation!<br><br>
		</div>
		<div id="ava5">
		The buttons you see below can be used by your agent to send emoticons in chat! The blinking emoticon is representing your current emotional state. Your agent can use it to communicate how it feels about the negotiation!<br><br>
		</div>
		<div>
			<img class="instruction-pic" id="instr_emo" alt="Picture of the emotion buttons" src="img/instr_emo.PNG" width="316" height="90" />
		</div>
		<br>
		<div class ="hidden" id="pva6">
		You will be assigned an avatar that will be visible to your partner.  Your avatar will change facial expressions when you send an emoticon.  You will be able
		to see your opponent's avatar across from the chat box.  <br> Their avatar's facial expression will also change when they send emoticons.  Below is an example of what your avatar could look like.
		</div>
		<div id="ava6">
		Your agent will be assigned an avatar that will be visible to your partner.  Your agent's avatar will change facial expressions when it sends an emoticon.  You will be able
		to see your opponent's avatar across from the chat box.  <br> Their avatar's facial expression will also change when they send emoticons.  Below is an example of what your agent's avatar could look like.
		</div>
		<br>
		<img class="instruction-pic" id="instr_table" alt="Picture of an avatar" src="img/ChrBrad/ChrBrad_SmallSmile.png"/>
		<div>
		<br>
		<hr>
		<br>	
		<h1>About Expressing Preferences:</h1>
		<br>
		<div id="pva7">
		<br>
		Below you can find an image of the preference menu. During the negotiation you can <strong>express your own preferences for items and ask your opponent specific questions about their preferences</strong>.
		<br>
		Clicking either of the first two buttons on the right side will let you <strong>express your preferences for items</strong>. Just click the item you want to talk about once,
		then click again in one of the boxes.  
		<br>
		Here, you can see that you're about to say that you like "MREs" "less than" "ammo boxes".  You can also click the "less than" symbol to turn it into different
		options, like "equal" or "best".<br><br>	
		</div>				
		<div id="ava7">
		<br>
		Below you can find an image of the preference menu. During the negotiation your agent can <strong>express its preferences for items and ask your opponent specific questions about their preferences</strong>.
		<br>
		Clicking either of the first two buttons on the right side will let it <strong>express its preferences for items</strong>. 
		<br>
		Here, you can see that it's about to say that it likes "MREs" "less than" "ammo boxes".  It can also click the "less than" symbol to turn it into different
		options, like "equal" or "best".<br><br>	
		</div>
		</div>
		<div>
			<img class="instruction-pic" id="instr_relation" alt="Picture of the relation options" src="img/instr_relation_mil.PNG" width="671" height="284" />
		</div>
		<div>
		<br>
		<hr>
		<br>
		<h1>Some Final Important Notes:</h1>	
		<div id="pva8">
		<ul>
		<li>The ONLY way to finish the game is to press "Formal Accept" and have your partner also press it, or for time to run out.  Pressing "Accept (non-binding)" will not work.</li>
		<li>If you need to, click the "View Payoffs" button if you need a reminder what you're looking for.</li>
		</ul>
		</div>
		<div id="ava8">
		<ul>
		<li> <strong>Your interface will be disabled and you will watch your agent play for you against your opponent.</strong></li>
		<li>The ONLY way to finish the game is for your agent to press "Formal Accept" and have your partner also press it, or for time to run out.  Pressing "Accept (non-binding)" will not work.</li>
		<li>The items and your preferences WILL CHANGE for each game if you are playing multiple games, so make sure to click the "View Payoffs" button if you need a reminder what you're looking for.</li>
		</ul>
		</div>
		</div>
		<hr>
	</div>	
			<br><br><strong>If you are coming from Qualtrics, you only need to answer the 3 quiz questions below and click 'Start!' to begin the game.<br> <br> <br> </strong>
			<div id="quiz" align="left">
				<br><br>Answer the below questions for the 'start' button to appear!<br><br>
				1) How do you express your emotions?
				<form action="">
					<input type="radio" name="item" value="wrong"> By smiling at your screen<br>
					<input type="radio" name="item" value="wrong"> You can't<br>
					<input id="ans1" type="radio" name="item" value="right"> By using emoticons<br>
					<input type="radio" name="item" value="wrong"> By saying them aloud<br>
				</form>
				<div class="hidden wrong" id="wrong1"><em>Oops!  Scroll back up and check the bold sections!</em></div>
		
				<br><br>
				2) How do you send an offer?
				<form action="">
					<input id="ans2" type="radio" name="pref" value="right"> Click "Start Offer", then manipulate the board, then hit "Send Offer"<br>
		
					<input type="radio" name="pref" value="wrong"> Offers are sent instantly.<br>
					<input type="radio" name="pref" value="wrong"> You can only receive offers, not send them.<br>
					<input type="radio" name="pref" value="wrong"> Click on the offer board at any time.
				</form>
				
				<div class="hidden wrong" id="wrong2"><em>Oops!  Scroll back up and check the bold sections!</em></div>
				<br><br>
				3) Which item is worth the same amount of points to you and your opponent?
				<form action="">
					<input type="radio" name="offer" value="wrong"> MREs<br>
					<input type="radio" name="offer" value="wrong"> ammo boxes<br>
					<input type="radio" name="offer" value="wrong"> It's impossible to know this.<br>
					<input id="ans3" type="radio" name="offer" value="right"> It depends on the opponent's preferences which can be discovered by asking them questions.
				</form>
				<div class="hidden wrong" id="wrong3"><em>Oops!  Scroll back up and check the bold sections!</em></div>
			</div>
			<form target="_self" id="mainPost" action="searching.jsp" method="POST">
				<br><br>
				<div id="dynamicPrefs">
					4) What item do you personally value the most?  It will be your highest valued item in the upcoming negotiations.<br>
					<input type="radio" name="mostPrefIndex" value="0"> lamps<br>
					<input type="radio" name="mostPrefIndex" value="1"> paintings<br>
					<input type="radio" name="mostPrefIndex" value="2"> records<br>
					<input type="radio" name="mostPrefIndex" value="3"> clocks<br><br>
					
					5) What item do you personally value the least?  It will be your lowest valued item in the upcoming negotiations.<br>
					<input type="radio" name="leastPrefIndex" value="0"> lamps<br>
					<input type="radio" name="leastPrefIndex" value="1"> paintings<br>
					<input type="radio" name="leastPrefIndex" value="2"> records<br>
					<input type="radio" name="leastPrefIndex" value="3"> clocks<br><br>
				</div>
				<!--The below is for internal testing -->
				<div id="qualtricsHide">
				<h2>Testing Agent vs Agent Form Section:</h2>
				<p>Note: The following form is intended for internal testing.  This form can be submitted to create an agent as if the user had come from Qualtrics.</p>
					<input type="radio" name="gameChoice" value="self" onclick="hideDiv()"> I want to play the game for myself<br>
				  	<input type="radio" name="gameChoice" value="agent" onclick="showDiv()"> I want to make an agent to play for me (click to fill in form)<br>
				  	<br><br>
				  	<div id="survey" style="display:none;">
					Select expression:
					<select name="expression">
					  <option value="PosNeg">Positive Negative</option>
					  <option value="Neutral">Neutral</option>
					  <option value="Happy">Happy</option>
					  <option value="Angry">Angry</option>
					</select>
					<br><br>
					Select behavior:
					<select name="behavior">
					  <option value="Building">Building</option>
					  <option value="Competitive">Competitive</option>
					</select>
					<br><br>
					Select messages:
					<select name="message">
					  <option value="Negative">Negative</option>
					  <option value="Neutral">Neutral</option>
					  <option value="Positive">Positive</option>
					  <option value="PosNeg">Positive Negative</option>
					</select>
					<br><br>
					Select withholding information:
					<select name="withhold">
					  <option value="Withholding">Able to withhold</option>
					  <option value="Open">Unable to withhold</option>
					</select>
					<br><br>
					Select honesty:
					<select name="honesty">
					  <option value="Honest">Honest</option>
					  <option value="Lying">Lying</option>
					</select>
					</div>
				</div>
				<br><br>
				<input id="MTurkID" name="MTurkID" type="hidden"  value="">
				<input id="qualtricsQ1" name="qualtricsQ1" type="hidden" value="">
				<input id="qualtricsQ2" name="qualtricsQ2" type="hidden" value="">
				<input id="qualtricsQ3" name="qualtricsQ3" type="hidden" value="">
				<input id="qualtricsQ4" name="qualtricsQ4" type="hidden" value="">
				<input id="qualtricsFlag" name="qualtricsFlag" type="hidden" value="ON">
				<input id="gameChoice" name="gameChoice" type="hidden" value="">
				<input id="gameMode" name="gameMode" type="hidden" value="">
				<input id="condition" name="condition" type="hidden" value="">
				<div class="post instructions hidden" style="margin: auto;">
					Congratulations, you answered all three questions correctly.  The 'Start!' button below is now available.  However, we <strong>highly</strong>
					 recommend that you first experiment in the <a href="https://myiago.com/apps/sandbox/searching.jsp" target="_blank">sandbox</a> first.  You can experiment with the interface in a new window before matching with your opponent!
					<br>
					<br>
					<div id="reminderText"></div>
					<br>
					<div id="reminderPhoto"></div>
<!-- 					<script> -->
<!-- // 						var img = new Image(); -->
<!-- // 						var imgdiv = document.getElementById('reminderPhoto'); -->
<!-- // 						var remtxt = document.getElementById('reminderText'); -->
<!-- // 						var cond = decodeURI(getQueryVariable("condition")); -->
<!-- // 						//The followiung logic loads the correct opponent image depending on whether user is playing against agent or human: -->
<!-- // 						if(cond === "hpva") -->
<!-- // 						{ -->
<!-- // 							img.src = "img/pcondition.jpg"; -->
<!-- // 							img.width = 300; -->
<!-- // 							img.height = 300; -->
<!-- // 							remtxt.append("REMINDER: You will negotiate against another human.  You will be connected once a match is found. ") -->
<!-- // 							imgdiv.appendChild(img); -->
<!-- // 						} -->
<!-- // 						else if(cond === "hava") -->
<!-- // 						{ -->
<!-- // 							img.src = "img/pcondition.jpg"; -->
<!-- // 							img.width = 300; -->
<!-- // 							img.height = 300; -->
<!-- // 							remtxt.append("REMINDER: You will program a virtual agent to negotiate against a human.  You will be connected once a match is found. ") -->
<!-- // 							imgdiv.appendChild(img); -->
<!-- // 						} -->
<!-- // 						else if(cond === "apva" ) -->
<!-- // 						{ -->
<!-- // 							img.src = "img/acondition.png"; -->
<!-- // 							img.width = 300; -->
<!-- // 							img.height = 300; -->
<!-- // 							remtxt.append("REMINDER: You will negotiate against a virtual agent. You will be connected once a connection to the agent's server is established.") -->
<!-- // 							imgdiv.appendChild(img); -->
<!-- // 						} -->
<!-- // 						else if(cond === "aava" ) -->
<!-- // 						{ -->
<!-- // 							img.src = "img/acondition.png"; -->
<!-- // 							img.width = 300; -->
<!-- // 							img.height = 300; -->
<!-- // 							remtxt.append("REMINDER: You will program a virtual agent to negotiate against another virtual agent.  You will be connected once a connection to the agent's server is established.") -->
<!-- // 							imgdiv.appendChild(img); -->
<!-- // 						} -->
<!-- 				</script> -->
					<input type="submit" value="Start!" style="height:50px; width:100px;"/>
				</div>
			</form>
		</div>
		<div id="wait" class="hidden">
			<h1>Please waiting for others to join...</h1>
		</div>
		<div id="in-use" class="hidden">
			<h1>You are already in a room.</h1>
		</div>
</body>
</html>