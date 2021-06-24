/*
 The following JS section parses any values received in the URL and inserts them into variables which get passed to searching.jsp on page submit.  When GB and GBUtils receive these variables
 on the backend, they will take in these variables to determine how to construct the current game.  If the user passes in data for variabels q1-q4, then they are trying to construct an agent
 to play for them with those 4 values corresponding to the values that their agent should have.  If q1-q4 are not populated then the user is trying to play the game for themselves.
 */
var urlString = window.location.href;	//URL string from URL bar

//shows survey content:

var webSocket;
var roomUID;

function createShortName() {
    return Math.random().toString(36).substring(2, 6)
}

function requestAsyncInfo(incomingEvent) {
	var event = $.parseJSON(incomingEvent);
	console.log(event);
 	if(event.tag == "MATCH") {
		//var form = document.forms[0];
		//form.submit();
 		$("#mainpage").removeClass("hidden");
		$("#wait").addClass("hidden");
		$("#in-use").addClass("hidden");
		roomUID = event.data;
		const userId = createShortName()
		// where to clear that when game finished?
		storeUserData({
			roomId: roomUID,
			userId
		})
		alert(`match, roomUID: ${roomUID}, userId: ${userId}`);
	} else if(event.tag == "WAIT") {
		$("#mainpage").addClass("hidden");
		$("#wait").removeClass("hidden");
	} else if(event.tag == "REG") {
		$("#mainpage").addClass("hidden");
		$("#in-use").removeClass("hidden");
	}
}

function storeUserData(userData) {
	sessionStorage.setItem('roomId', userData.roomId)
	sessionStorage.setItem('userId', userData.userId)
}

function openSocket(){
	// Ensures only one connection is open at a time
	if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
		writeResponse("WebSocket is already opened.");
		return;
	}

	var loc = window.location, new_uri;
	if (loc.protocol === "https:") {
		new_uri = "wss:";
	} else {
		new_uri = "ws:";
	}
	new_uri += "//" + loc.host;
	new_uri += loc.pathname + "/ws";
	// Create a new instance of the websocket
	webSocket = new WebSocket(new_uri);
	
	function sendMsg(message) {
		webSocket.send(JSON.stringify(message));
	}

	/**
	 * Binds functions to the listeners for the websocket.
	 */
	webSocket.onopen = function(event){
		 //alert("socket open");
		// sendMsg("hello world");
	};
	
	webSocket.onmessage = function(event){
		//alert(event.data);
		requestAsyncInfo(event.data);
		//alert(event.tag);
	};

	webSocket.onclose = function(event)
	{
		console.log('Onclose called' + JSON.stringify(event));
		console.log('code is' + event.code);
		console.log('reason is ' + event.reason);
		console.log('wasClean  is' + event.wasClean);
		$("input").off("click");
		$(".tableSlot").off("click");
	};
}

openSocket();

var urlString = window.location.href;
console.log(urlString);
function showDiv() {
	   document.getElementById("survey").style.display = "block";
	}

//Hides content from being shown on page:
function hideDiv() {
	   document.getElementById("survey").style.display = "none";
	}

//Gets query variables from URL
function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);	//get URL as query
       var vars = query.split("&");						//split URL by & delimiter
       
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}

//The following loads immediately after accessing the page and considers the URL data to determine game type and parameters
window.onload = function() {

	document.getElementById("MTurkID").value = decodeURI(getQueryVariable("id"));		//user's MTurkID value
	document.getElementById("gameMode").value = decodeURI(getQueryVariable("gameMode"));		//gameMode for HH
	document.getElementById("qualtricsQ1").value = decodeURI(getQueryVariable("q1"));	//q1 represents the user's choice for their agent's behavior
	q1 = decodeURI(getQueryVariable("q1"));
	if(q1 === "Use both positive and negative emotional signals"){
		document.getElementById("qualtricsQ1").value = "PosNeg";
	}
	else if(q1 === "Use only positive emotional signals"){
		document.getElementById("qualtricsQ1").value = "Positive";
	}
	else if(q1 === "Use only negative emotional signals"){
		document.getElementById("qualtricsQ1").value = "Negative";
	}
	else if(q1 === "Use neither positive nor negative signals"){
		document.getElementById("qualtricsQ1").value = "Neutral";
	}
	else{
		document.getElementById("qualtricsQ1").value = "Undefined";
	}
	q2 = decodeURI(getQueryVariable("q2"));												//q2 represents the user's choice for their agent's competitiveness
	if(q2 === "Fair concession-making"){
		document.getElementById("qualtricsQ2").value = "Building";
	}
	else if(q2 === "Tough concession-making"){
		document.getElementById("qualtricsQ2").value = "Competitive";
	}
	else{
		document.getElementById("qualtricsQ2").value = "Undefined";
	}
	q3 = decodeURI(getQueryVariable("q3"));												//q3 represents the user's choice for their agent's ability to withhold
	if(q3 === "Withhold information from the opponent unless opponent reveals"){
		document.getElementById("qualtricsQ3").value = "Withholding";
	}
	else if(q3 === "Openly exchange information with the opponent"){
		document.getElementById("qualtricsQ3").value = "Open";
	}
	else{
		document.getElementById("qualtricsQ3").value = "Undefined";
	}
	q4 = decodeURI(getQueryVariable("q4"));												//q4 represents the user's choice for their agent's honesty
	if(q4 === "It is ok to misrepresent information to the opponent"){
		document.getElementById("qualtricsQ4").value = "Lying";
	}
	else if(q4 === "The agent should only reveal truthful information"){
		document.getElementById("qualtricsQ4").value = "Honest";
	}
	else{
		document.getElementById("qualtricsQ4").value = "Undefined";
	}
	//NOTE: If any value assigned 'undefined' gets passed through then the agent will not be constructed since this is not a valid choice
	document.getElementById("condition").value = decodeURI(getQueryVariable("condition"));	//respresents game condition (either player vs. agent or agent vs. agent)
	condition = decodeURI(getQueryVariable("condition"));
	var dynamic = (decodeURI(getQueryVariable("dynamic")) === "true");
	
	//The below selectively hides and shows page text depending on game condition because the wording must be changed depending on whether user is playing for themselves or not:
	if(condition == "hava" || condition == "aava"){
		document.getElementById("pva1").style.display = "none";
		document.getElementById("pva2").style.display = "none";
		document.getElementById("pva3").style.display = "none";
		document.getElementById("pva4").style.display = "none";
		document.getElementById("pva5").style.display = "none";
		document.getElementById("pva6").style.display = "none";
		document.getElementById("pva7").style.display = "none";
		document.getElementById("pva8").style.display = "none";
	}
	if(condition == "hpva" || condition == "apva"){
		document.getElementById("ava1").style.display = "none";
		document.getElementById("ava2").style.display = "none";
		document.getElementById("ava3").style.display = "none";
		document.getElementById("ava4").style.display = "none";
		document.getElementById("ava5").style.display = "none";
		document.getElementById("ava6").style.display = "none";
		document.getElementById("ava7").style.display = "none";
		document.getElementById("ava8").style.display = "none";
	}
	
	//If any value q1-q4 is empty, then the user is coming from Qualtrics but is playing against the opponent themself:
	if( q1 === "" || q2 === "" || q3 === "" || q4 === ""){
		document.getElementById("qualtricsHide").style.display = "none";
		document.getElementById("gameChoice").value = "player";
		document.getElementById("qualtricsFlag").value = "ON";			 //tells Java to make a Qualtrics agent instead of a default form agent
	}
	else{
		document.getElementById("qualtricsHide").style.display = "none";
		document.getElementById("gameChoice").value = "agent";			 //this has to be set manually if doing a Qualtrics agent and tells Java that the game is still agent vs agent
		document.getElementById("qualtricsFlag").value = "OFF";			 //tells Java to not make an agent
	}
	
	//***
	//This section manages the attention check code
	//***
	
	$(function() {
		$("#butContinue").click(function(e) {
			$('.questions').removeClass("hidden");
			$('html, body').animate({
		        scrollTop: $('.questions').offset().top - 20
		    }, 'slow');
		});
	});
	
	if(dynamic)
	{
		$(function() {
			$("input").click(function(e) {
				if($("input[name=item]:checked").val() == "right" && $("input[name=pref]:checked").val() == "right" &&
						$("input[name=offer]:checked").val() == "right" && $("input[name=mostPrefIndex]").is(":checked") 
						&& $("input[name=leastPrefIndex]").is(":checked"))
				{
					$('.post').removeClass("hidden");
					$('html, body').animate({
				        scrollTop: $('.post').offset().top - 20
				    }, 'slow');
				}
				if($("input[name=item]:checked").val() == "wrong")
					$("#wrong1").removeClass("hidden");
				else
					$("#wrong1").addClass("hidden");
				if($("input[name=pref]:checked").val() == "wrong")
					$("#wrong2").removeClass("hidden");
				else
					$("#wrong2").addClass("hidden");
				if($("input[name=offer]:checked").val() == "wrong")
					$("#wrong3").removeClass("hidden");
				else
					$("#wrong3").addClass("hidden");
			});
		});
	}
	else
	{
		$("#dynamicPrefs").addClass("hidden");

		$(function() {
			$("input").click(function(e) {
				if($("input[name=item]:checked").val() == "right" && $("input[name=pref]:checked").val() == "right" &&
						$("input[name=offer]:checked").val() == "right")
				{
					$('.post').removeClass("hidden");
					$('html, body').animate({
				        scrollTop: $('.post').offset().top - 20
				    }, 'slow');
				}
				if($("input[name=item]:checked").val() == "wrong")
					$("#wrong1").removeClass("hidden");
				else
					$("#wrong1").addClass("hidden");
				if($("input[name=pref]:checked").val() == "wrong")
					$("#wrong2").removeClass("hidden");
				else
					$("#wrong2").addClass("hidden");
				if($("input[name=offer]:checked").val() == "wrong")
					$("#wrong3").removeClass("hidden");
				else
					$("#wrong3").addClass("hidden");
			});
		});
	}
	
	

};

