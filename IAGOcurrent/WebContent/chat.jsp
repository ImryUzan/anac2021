<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css" />
<!-- <link rel="stylesheet" href="style.css" type="text/css"/> -->
<style type="text/css">
body {
    background: #eeeeee;
}

.banner {
    font-size: 20px;
    font-family: 'Times New Roman', Times, serif;
    width: 400px;
    margin: 10px auto;
    padding: 3px;
    /* border: 3px solid greenyellow;  */
    text-align: center;
}

#main {
    width: 500px;
    height: 700px;
    padding: 5px;
    margin: 0 auto;
    background-color: #bebebe;
}

#history-msg {
    padding: 5px;
    width: 490px;
    height: 400px;
    margin: 10px auto;
    background-color: #f4f8f4;
    overflow: scroll;
}

#input-area {
    width: 490px;
    height: 200px
}

#message {
    width: 400px;
    height: 130px
}

input.send-button {
    width: 20em;  
    height: 2em;
}

.text-center {
    text-align: center
}
</style>

<script>
var webSocket;

function requestAsyncInfo(incomingEvent) {
 	var event = $.parseJSON(incomingEvent);
 	console.log(event);
 	if(event.tag == "MSG") {
 		appendMsg("Opposite: " + event.data);
 	} else if(event.tag == "ERR") {
 		alert(event.data); //TODO show info
 	} else if(event.tag == "END") {
 		alert(event.data); //TODO redirect
 	}
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

function appendMsg(msg) {
	var p = document.createElement('p');
    p.textContent = msg;
    document.getElementById('history-msg').appendChild(p);
}

function sendMsg() {
	var msgInput = document.getElementById('message');
	var msg = msgInput.value;
	if(msg == "" || msg === undefined) return;
	msgInput.value = ""; //clear input area
	appendMsg("I: " + msg);
	document.getElementById('message').focus();
	webSocket.send(JSON.stringify(msg));
}

document.onkeydown = function(e) {
	var keyNum = window.event ? e.keyCode :e.which;
	if(keyNum == 13) {
		sendMsg();
	}
}

</script>

<title>ChatRoom</title>
</head>
<body>
	<div id="main">
		<div class="banner">
			<h1>ChatRoom</h1>
		</div>
		<div id="history-msg">
		</div>
		<div id="input-area">
			<textarea id="message" placeholder="enter to send"></textarea>	
			<input id="send-button" type="button" value="SEND" style="height:50px; width:50px" onclick="sendMsg()">
		</div>
	</div>
	<footer class=container>
		<hr>
		<p class="text-center">copy 2019 Chen Wang</p>
	</footer>
</body>
</html>
