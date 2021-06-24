//////////FOLLOWING RUNS ASAP \\\\\\\\\\



//globals
var neutralTimeout;
var artChar = "Brad";
var globalTimer = 0;
var timer = 0;
var timerID;
var angerGlow = false;
var happyGlow = false;
var sadGlow = false;
var surprisedGlow = false;
var neutralGlow = true;
var webSocket;
var maxTime = 0;
var closeSafe = false;
var readyToRestart = false;
var holdingForUser = false;
var playerBATNA = 0;
var playerPresentedBATNA = 0;
var totalPlayerPoints = 0;
var disable = false;
var disableStarted = false;
var qualtricsData = "";
var disableFunction = function disabler(e){
    e.stopPropagation();
    e.preventDefault();
}


const EMOTIONS = {
		NEUTRAL: "neutral",
		ANGER: "anger",
		HAPPY: "happy",
		SAD: "sad",
		SURPRISED: "surprised"
}

function feelEmotion(emotion) {
	neutralGlow = emotion === EMOTIONS.NEUTRAL
	angerGlow = emotion === EMOTIONS.ANGER
	happyGlow = emotion === EMOTIONS.HAPPY
	sadGlow = emotion === EMOTIONS.SAD
	surprisedGlow = emotion === EMOTIONS.SURPRISED
}

//called when data comes into the WebSocket
function requestAsyncInfo(incomingEvent, socket) {
	var event = $.parseJSON(incomingEvent);
    console.log('event:', event)
	if (event.tag == "points-vh") {
		total = 0;
		for (i = 0; i < event.data.length; i++) {
			$("#labelVHPoints" + i).text(event.data[i]);
			total += event.data[i];
		}
		$("#labelVHPointsTotal").text(total);
	} else if(event.tag == "visibility-points-vh") {
		if(event.data == true)
			$(".labelVHPoints").removeClass("hidden");//insecure
		else
			$(".labelVHPoints").addClass("hidden");
	} else if(event.tag == "agent-description") {
		if(event.data != null) {
			$("#vhDescription").html(event.data)
			$("#vhDescription").removeClass("hidden");
		}
	} else if(event.tag == "visibility-timer") {
		if(event.data == true)
			$("#negoTimer").removeClass("hidden");
		else
			$("#negoTimer").addClass("hidden");
	} else if(event.tag == "max-time") {
		maxTime = event.data;
	} else if (event.tag == "cursors-ready") {
		$("#butCol0Row0").attr('src', "img/item0.png" + "?" + Math.random()); //force a recache.  Ew, I know.
		$("#butCol0Row1").attr('src', "img/item0.png" + "?" + Math.random());
		$("#butCol0Row2").attr('src', "img/item0.png" + "?" + Math.random());

		$("#butCol1Row0").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butCol1Row1").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butCol1Row2").attr('src', "img/item1.png" + "?" + Math.random());

		$("#butCol2Row0").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butCol2Row1").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butCol2Row2").attr('src', "img/item2.png" + "?" + Math.random());

		$("#butCol3Row0").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butCol3Row1").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butCol3Row2").attr('src', "img/item3.png" + "?" + Math.random());

		$("#butCol4Row0").attr('src', "img/item4.png" + "?" + Math.random());
		$("#butCol4Row1").attr('src', "img/item4.png" + "?" + Math.random());
		$("#butCol4Row2").attr('src', "img/item4.png" + "?" + Math.random());

		$("#butItem0").attr('src', "img/item0.png" + "?" + Math.random());
		$("#butItem1").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butItem2").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butItem3").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butItem4").attr('src', "img/item4.png" + "?" + Math.random());

		$("#butItemFirst").attr('src', "img/white.png");
		$("#butItemSecond").attr('src', "img/white.png");
	} else if(event.tag =="config-character-art") {
		switch (event.data) {
			case "Brad":
				artChar = "Brad"
				break
			case "Ellie":
				artChar = "Ellie"
				break
			case "Rens":
				artChar = "Rens"
				break
			case "BradMask":
				artChar = "BradMask"
				break
			case "EllieMask":
				artChar = "EllieMask"
				break
			case "BradMilitaryMask":
				artChar = "BradMilitaryMask"
				break
			case "EllieMilitaryMask":
				artChar = "EllieMilitaryMask"
				break
			case "BradMilitary":
				artChar = "BradMilitary"
				break
			case "EllieMilitary":
				artChar = "EllieMilitary"
				break
			default:
				artChar = "Laura"
		}

		if (artChar == "Brad")
			$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
		else if (artChar == "Ellie")
			$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
		else if  (artChar == "Rens")
			$("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
		else if (artChar == "BradMask")
			$("#vhImg").attr('src', "img/ChrBradMask/ChrBradMask_FaceMask.png");
		else if (artChar == "EllieMask")
			$("#vhImg").attr('src', "img/ChrEllieMask/ChrEllieMask_FaceMask.png");
		else if (artChar == "BradMilitaryMask")
			$("#vhImg").attr('src', "img/ChrBradMilitaryMask/ChrBradMilitaryMask_FaceMask.png");
		else if (artChar == "EllieMilitaryMask")
			$("#vhImg").attr('src', "img/ChrEllieMilitaryMask/ChrEllieMilitaryMask_FaceMask.png");
		else if (artChar == "BradMilitary")
			$("#vhImg").attr('src', "img/ChrBradMilitary/ChrBradMilitary_Neutral.png");
		else if (artChar == "EllieMilitary")
			$("#vhImg").attr('src', "img/ChrEllieMilitary/ChrEllieMilitary_Neutral.png");
		else
			$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
	} else if(event.tag == "player-BATNA") {
		playerBATNA = event.data;
		$("#mySlider").val(event.data);
		$("#mySlider").css("background","green");
		playerPresentedBATNA = event.data;
		$("#batnaValue").text("I already have an offer for " + playerPresentedBATNA + " points.");
	} else if(event.tag == "total-player-points") {
		totalPlayerPoints = event.data;
		$( "#mySlider" ).attr({
			max: event.data
		});
	} else if(event.tag == "hide-quit") {
		updateButtonPanelItem("butFormalQuit", false, true)
	}
	else if (event.tag == "open-survey") {
		survey = event.data;
		if (survey == null || survey == "") {
			submitAll(false);
		} else {
			//This block does not currently check to see if the survey URL is valid.
			text = $.get(survey, function(data) {
                toggleSurveyDiag(true, data)
			}, 'html')
		}
	}
	else
		handleEvent(event, socket)
}
function printMenu(json) {
	// First hide the <div>'s elements completely 
	$(".butText").addClass("hidden");
	$(".butContainer").addClass("hidden");
	$(".compare-item").addClass("hidden");
	$(".compare-panel-message").addClass("hidden");
	$(".buffer").addClass("hidden");
	//$(".compare-panel-message").text("");

	minHeight = 0;
	// Then add every name contained in the list.	
	$.each(json, function(id, text) {
		minHeight += 30;
		$("#" + id).toggleClass("hidden");
		if($("#" + id).hasClass("butText"))
			$("#" + id).val(text);
	});
	
	minPrefHeight = $("#butPrefDiv").height() + 30*3;//accounts for two buttons
	// Then add every name contained in the list.	

	if ($(".messages").height() < minHeight)
		$("#messageBuffer").css("padding-top", minHeight - $(".messages").height());

	if ($(".messages").height() < minPrefHeight)
		$("#buttonBuffer").css("padding-top", minPrefHeight - $(".messages").height());
	
	//browser-specific kludges
	var OSName="Unknown OS";
	if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
	if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
	if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX";
	if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";

	//this is a kludgy fix for people with small screens--this code needs a full css rework to better support odd browsers
	//there may also be some Mac/PC differences--never mind about mobile browsers
	//I suppose I'll keep supporting IE 11 until 2025, when it reaches extended EoL
	if((navigator.userAgent.indexOf("Opera") != -1 || navigator.userAgent.indexOf('OPR') != -1) && OSName != "Windows")//seems to be a mac problem 
	{
		//$(".butText").css("background-color", "#FFFFFE"); //must do this.  FFFFFF won't work--won't cause rerender
		$(".butText").css("flex", "1 0 auto !important"); //Mac does not respect min-height so must disable shrinkage
	}
	else if(navigator.userAgent.indexOf("Chrome") != -1 && OSName != "Windows")//seems to be a mac problem
	{
		//$(".butText").css("background-color", "#FFFFFE"); //must do this.  FFFFFF won't work--won't cause rerender
		$(".butText").css("flex", "1 0 auto !important"); //Mac does not respect min-height so must disable shrinkage
	}
	else if(navigator.userAgent.indexOf("Safari") != -1 && OSName != "Windows")//seems to be a mac problem)
	{
		//$(".butText").css("background-color", "#FFFFFE"); //must do this.  FFFFFF won't work--won't cause rerender
		$(".butText").css("flex", "1 0 auto !important"); //Mac does not respect min-height so must disable shrinkage
	}
	else if(navigator.userAgent.indexOf("Firefox") != -1 ) 
	{
		//leave
	}
	else if((navigator.userAgent.indexOf("MSIE") != -1 ) || (!!document.documentMode == true )) //IF IE > 10 or Edge
	{
		$(".buffer").addClass("hidden");
		$("#gameContainer").css("margin", "0");
		$(".gameWrapper").css("margin", "0");
		$("#gameContainer").css("height", "800");
		$("#gameContainer").css("width", "1200");
		$(".menu-side").css("width", "720");
		$(".vh-side").css("width", "480");
		$(".flex-child").css("margin", "0");
		$(".flex-child-no-grow").css("margin", "0");
		$(".flex-child-no-shrink").css("margin", "0");
	}  
	else 
	{
		//leave
	}
}

function webSocketSend(event) {
    webSocket.send(JSON.stringify(event))
}

function openSocket(){
    console.log('openSocket')
	// Ensures only one connection is open at a time
	if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
		writeResponse("WebSocket is already opened.");
		return;
	}

	var loc = window.location, new_uri;
	
	//NOTE: This is the proper way of ensuring the correct protocols in server setup that does not rely on reverse proxying.
	//Often, Tomcat or similar servers will be set up (perfectly safely) unencrypted behind an encrypted static server.
	//In this case, particularly with Apache, the URLs can become mangled because the ASF is dumb.
	//While you can rectify this mangling manually, it will confuse the code below, which does not know the presence
	//of a proxy, and will therefore use the wrong protocol.
	//Therefore, please ONLY use this code if you know what you're doing.
	
	if (loc.protocol === "https:") {
		new_uri = "wss:";
	} else {
		new_uri = "ws:";
	}
	
	//In most setups, use the following code for unencrypted Tomcat servers behind reverse proxies.  This can, however,
	//cause problems with mixed content errors.
	//new_uri = "ws:";
	
	//Now continue
	new_uri += "//" + loc.host;
	new_uri += loc.pathname + "/ws";
	// Create a new instance of the websocket
	webSocket = new WebSocket(new_uri);

	/**
	 * Binds functions to the listeners for the websocket.
	 */
	webSocket.onopen = function(event) {
		startup(event)
	}

	webSocket.onmessage = function(event) {
		requestAsyncInfo(event.data, webSocket);
	}

	webSocket.onclose = function(event) {
		clearInterval(timerID);

		if(closeSafe == false) {
			dialog("The connection was terminated early. Your opponent may have left.");

			// This block isn't working. Intended to allow us to write final data on an unsafe close
			webSocketSend({ tag: "unsafe-close", data: "" });
		}
		console.log('Onclose called' + JSON.stringify(event));
		console.log('code is' + event.code);
		console.log('reason is ' + event.reason);
		console.log('wasClean  is' + event.wasClean);
		$("input").off("click");
		$("button").off("click");
		$(".tableSlot").off("click");
	}
}

function closeSocket() {
	closeSafe = true;
	webSocket.close();
}

function startup(event) {
	startup(event, true);
}

function startup() {
	startup(null, false);
}

function startup(event, useEvent) {

	if (window.performance.navigation.type == 1) {
        webSocketSend({ tag: "page-refreshed", data: "" })
	}
	if (useEvent) {
		var debugdata = 'Onopen called' + JSON.stringify(event) + '\ncode is' + event.code + '\nreason is ' + event.reason + '\nwasClean  is' + event.wasClean;
		console.log(debugdata);
        webSocketSend({ tag: "debug", data: debugdata })
	}
    webSocketSend({ tag: "button", data: "root" })
    webSocketSend({ tag: "button", data: "butCol0Row0" })
    webSocketSend({ tag: "butCol0Row0", data: "root" })
    showChatMessage(agentMessage("Hello!")) //this forces IE to refresh the formatting
    flashMessage("Hello!")
    webSocketSend({ tag: "request-visibility", data: "vh-points" })
    webSocketSend({ tag: "request-max-time", data: "" })
    webSocketSend({ tag: "request-visibility", data: "timer" })
    webSocketSend({ tag: "request-agent-description", data: "" })
    webSocketSend({ tag: "request-agent-art", data: "" })
    //get the actual value of the user's BATNA
    webSocketSend({ tag: "request-player-batna", data: "" })
    //get the total possible points for the player.
    webSocketSend({ tag: "request-total-player-points", data: "" }) 
    webSocketSend({ tag: "start-threads", data: "" })
}

function buttonHandler(button) {
	//some of this hiding does not conform to code-hiding standards/encapsulation.  Will be revised to server-side.
	if(button.data == "butStartOffer") {
		$(".itemButton").prop("disabled", false);
		updateButtonPanelItem("butStartOffer", false, undefined)
		updateButtonPanelItem("butSendOffer", true, undefined)
		updateButtonPanelItem("butAccept", false, undefined)
		updateButtonPanelItem("butReject", false, undefined)
		updateButtonPanelItem("butAcceptFavor", false, undefined)
		updateButtonPanelItem("butRejectFavor", false, undefined)

		disableExchangeTable(false)
	}

	if(button.data == "butSendOffer") {
		$(".itemButton").prop("disabled", true);
		updateButtonPanelItem("butSendOffer", false, undefined)
		updateButtonPanelItem("butStartOffer", true, undefined)

		disableExchangeTable(true)
	}

	if(button.data == "butAccept" || button.data == "butReject") {
		updateButtonPanelItem("butAccept", false, undefined)
		updateButtonPanelItem("butReject", false, undefined)
	}
	
	if(button.data == "butAccepFavor" || button.data == "butRejectFavor") {
		updateButtonPanelItem("butAcceptFavor", false, undefined)
		updateButtonPanelItem("butRejectFavor", false, undefined)
	}

	if(button.data == "butSend") {
        webSocketSend({ tag: "sendBATNA", data: String(playerPresentedBATNA) })
	}

	if(button.data == "butItemComparison") {
		if($("#butItemComparison").attr('src') == "img/ltsymbol.jpg") {
			$("#butItemComparison").attr('src', 'img/gtsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "GT";
		} else if($("#butItemComparison").attr('src') == "img/gtsymbol.jpg") {
			$("#butItemComparison").attr('src', 'img/best.png');
			$("#butItemSecond").addClass("reallyHidden");
			button.data += "BEST";
		} else if($("#butItemComparison").attr('src') == "img/best.png") {
			$("#butItemComparison").attr('src', 'img/least.png');
			$("#butItemSecond").addClass("reallyHidden");
			button.data += "LEAST";
		} else if($("#butItemComparison").attr('src') == "img/least.png") {
			$("#butItemComparison").attr('src', 'img/eqsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "EQUAL";
		} else if($("#butItemComparison").attr('src') == "img/eqsymbol.jpg") {
			$("#butItemComparison").attr('src', 'img/ltsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "LT";
		}
	}

	switch(button.data) {
		case "butAnger":
			feelEmotion(EMOTIONS.ANGER)
			break
		case "butSad":
			feelEmotion(EMOTIONS.SAD)
			break
		case "butNeutral":
			feelEmotion(EMOTIONS.NEUTRAL)
			break
		case "butSurprised":
			feelEmotion(EMOTIONS.SURPRISED)
			break
		case "butHappy":
			feelEmotion(EMOTIONS.HAPPY)
			break
		default:
	}

    webSocketSend(button)

	//this needs to be done immediately, as it is simply waiting for input from the servlet
	if(button.data.indexOf("butExpl") > -1) {
		showChatMessage(userMessage($("#" + button.data).val()))
		updateButtonPanelItem("butAcceptFavor", false, undefined)
		updateButtonPanelItem("butRejectFavor", false, undefined)
		scrollChatToTop()
	}	
}

function submitAll() {
	submitAll(true);
}

function submitAll(validSurvey) {
	if(validSurvey === undefined || validSurvey) {
//		if ($('input:iagoCheckableQuestion', this).is(':checked')){
//			console.log("something was checked");
			var survey = "{" + $(".iagoCheckableQuestion:checked").map(function() {
				return ("\"" + $(this).attr("id") + "\":\"" + $(this).val() + "\"");
			}).get() + "}";
            webSocketSend({ tag: "survey", data: JSON.parse(survey) })
//			
//		    } else {
//		    	console.log("nothing was checked");
//		        alert("Please answer all the questions on the survey and press the submit button when you're finished making your selections.");
//		        return false;
//		    }
	}

    webSocketSend({ tag: "dialogClosed", data: "" })
    $(".gameWrapper").removeClass("hidden")
    toggleSurveyDiag(false, "")
}

////////////////////////////FOLLOWING RUNS AFTER ELEMENTS LOADED \\\\\\\\\\\\\\\\\\\\\\\\\\\\
$(document).ready(function() 
		{
	
	openSocket();
	
	//scaling to more reasonable size
	var starterData = { 
			  size: {
			    width: $wrapper.width(),
			    height: $wrapper.height()
			  }
			}
	doResize(null, starterData);

	var tempID = "root";
	
	timerID = setInterval(function(){

		globalTimer++;
		if(globalTimer == 10) {
            webSocketSend({ tag: "notify-thread", data: "" })
			globalTimer = 0;
		}

		if(holdingForUser) {
			return;
		}

		timeRemaining = maxTime - timer;
		
		timer++;

		if(timeRemaining < 0)
			timeRemaining = 0;
		//If the timer just started (which is 420 seconds) and two agents are playing each other, then activate the UI disable:
		if(disable === true && disableStarted === false){
				disableStarted = true;
				document.addEventListener("click",disableFunction,true);
				console.log("stopped interraction")
		}
		//Reactivate the UI at the end of the game if two agents are playing each other so the user can proceed to the next section:
		if(timeRemaining === 1 && disable === true){
			document.removeEventListener("click", disableFunction, true)
			disable = false;
			console.log("got this far")
		}
		mins = Math.floor(timeRemaining / 60);
		secs = Math.floor(timeRemaining % 60);
		trueSecs = secs + "";
		if(secs < 10)
			trueSecs = "0" + secs;

		$("#negoTimer").text("Time Remaining: " + mins + ":" + trueSecs);

		if(readyToRestart) {
			readyToRestart = false;
			holdingForUser = true;
            webSocketSend({ tag: "openSurvey", data: "" })
			$(".gameWrapper").addClass("hidden");
		}

		if(timer % 5 == 0) {
            webSocketSend({ tag: "time", data: timer })
		}

		glowEmotionItem("butAnger", timer % 2 == 0 && angerGlow)
		glowEmotionItem("butSad", timer % 2 == 0 && sadGlow)
		glowEmotionItem("butNeutral", timer % 2 == 0 && neutralGlow)
		glowEmotionItem("butSurprised", timer % 2 == 0 && surprisedGlow)
		glowEmotionItem("butHappy", timer % 2 == 0 && happyGlow)
	}, 1000);


	//disable the offer grid to start
	$(".issueButton").prop("disabled", true);
	disableExchangeTable(true)

	//dialogStartGame("Welcome to IAGO! Prepare to begin!"); //TODO remove deprecated code no longer relevant (intermediate now used)		

	//specialty event to capture grid buttons with larger area
	$(".tableSlot").click(function(event) {
		if(event.target.id.indexOf("divCol") >= 0) {
			const data = "but" + event.target.id.substring(event.target.id.indexOf("Col"))
			buttonHandler({ tag: "button", data })
		}
			});

	// Add an event that triggers when ANY button
	// on the page is clicked...
	$("input").click(function(event) {
		//send a message in the socket
		buttonHandler({ tag: "button", data: event.target.id })
	})

	$("button").click(function(event) {
		//send a message in the socket
        buttonHandler({ tag: "button", data: event.target.id })
	})

	$("#mySlider").on('input', function () {
		playerPresentedBATNA = $("#mySlider").val();
		$("#batnaValue").text("I already have an offer for " + playerPresentedBATNA + " points.");
		trueBATNASpan = totalPlayerPoints * .05;

		lowerBATNARange = playerBATNA-trueBATNASpan;	//Allow +- 5% for a "truthful" BATNA.
		higherBATNARange = playerBATNA+trueBATNASpan;
		
		if (playerPresentedBATNA < lowerBATNARange) {
			//user is lying about BATNA
			$("#mySlider").css("background","red");
			$("#batnaDescription").text("This form of lying makes you look weak.");
		} else if (playerPresentedBATNA >= lowerBATNARange && playerPresentedBATNA < higherBATNARange) {
			//user is telling the truth (first condition isn't strictly necessary but is included for clarity)
			$("#mySlider").css("background","green");
			$("#batnaDescription").text("This is the truth.");
		} else {
			//user is lying about BATNA
			$("#mySlider").css("background","red");
			$("#batnaDescription").text("This form of lying makes you look strong.");
		}
	});

	$("#mySlider").mouseup(function()
			{
		trueBATNASpan = totalPlayerPoints * .05;
		lowerBATNARange = playerBATNA-trueBATNASpan;
		higherBATNARange = playerBATNA+trueBATNASpan;
		//user is telling the truth about his/her BATNA.
		if(playerPresentedBATNA >= lowerBATNARange && playerPresentedBATNA < higherBATNARange){
			$("#mySlider").val(playerBATNA);
			playerPresentedBATNA = playerBATNA;
			$("#batnaValue").text("I already have an offer for " + playerPresentedBATNA + " points.");
			$("#batnaDescription").text("This is the truth.");
		}
			});

	$(function () {
		$('[data-toggle="tooltip"]').tooltip()
	})
});