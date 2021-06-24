function handleEvent(event, socket) {
    const fn = window[event.tag];
    if (typeof fn === "function") {
        fn.apply(null, [event, socket]);
    } else if (fn === undefined) {
        console.log("Could not find event handler:", event.tag)
    }
}

function interfaceDisabled(event, socket)
{
	console.warn("Interface disabling code missing!", event.tag)
}


function objectLocs(event, socket) {
    removeWaitingMessage();
    var formalLegal = true;
    var data = event.tag == "SEND_OFFER" ? event.data.offer.offer : event.data; //objectLocs is the raw array, offer is an Event

    $('[class^="item-col"], [class*=" item-col"]').addClass("hidden")
    for (var col = 0; col < data.length; col++) {
        $(".item-col" + col).removeClass("hidden");
        if (data[col][1] != 0)
            formalLegal = false;
        for (var row = 0; row < data[col].length; row++)
            $("#itemLabelCol" + col + "Row" + row).text(data[col][row]);
    }

    var obj = new Object();
    obj.tag = "request-points";
    obj.data = "";
    socket.send(JSON.stringify(obj));

    var obj2 = new Object();
    obj2.tag = "request-points-vh";
    obj2.data = "";
    socket.send(JSON.stringify(obj2));
    return formalLegal
}

function GAME_START(event, socket) {
    console.log('GAME_START:', event)
}

function GAME_END(event, socket) {
    console.log('GAME_END:', event)
}

function SEND_OFFER(event, socket) {
    const formalLegal = objectLocs(event, socket)
    if (formalLegal) {
        updateButtonPanelItem("butFormalAccept", true, false)
        $("#butFormalAccept").prop("value", "Send Formal Acceptance Proposal");
        updateButtonPanelItem("butAccept", false, undefined)
    }
    else {
        updateButtonPanelItem("butFormalAccept", false, true)
        updateButtonPanelItem("butAccept", true, false)
    }

    updateButtonPanelItem("butFormalQuit", false, true)
    updateButtonPanelItem("butReject", true, undefined)
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
    $("#exchange-grid").removeClass("fade-in-and-out")
    $("#exchange-grid").addClass("fade-in-and-out")
    
    if (containsAgentData(event)) {
        showChatMessage(agentMessage(event.data.message))
        //flashMessage(event.data.message)
    } else {
        showChatMessage(userMessage(event.data.message))
    }

    scrollChatToTop()

    updateButtonPanelItem("butSendOffer", false, undefined)
    updateButtonPanelItem("butStartOffer", true, false)
    disableExchangeTable(true)
}

//Only change the VH photo if the expression was sent by the computer
function SEND_EXPRESSION(event, socket) {
    clearTimeout(neutralTimeout);
    removeWaitingMessage();
    
    //cut short for limited characters
    if (artChar == "BradMask")
    {
    	$("#vhImg").attr('src', "img/ChrBradMask/ChrBradMask_FaceMask.png");
    	return;
    }
    if (artChar == "EllieMask")
    {
    	$("#vhImg").attr('src', "img/ChrEllieMask/ChrEllieMask_FaceMask.png");
    	return;
    }
    if (artChar == "BradMilitaryMask")
    {
    	$("#vhImg").attr('src', "img/ChrBradMilitaryMask/ChrBradMilitaryMask_FaceMask.png");
    	return;
    }
    if (artChar == "EllieMilitaryMask")
    {
    	$("#vhImg").attr('src', "img/ChrEllieMilitaryMask/ChrEllieMilitaryMask_FaceMask.png");
    	return;
    }
    if (artChar == "BradMilitary")
    {
    	$("#vhImg").attr('src', "img/ChrBradMilitary/ChrBradMilitary_Neutral.png");
    	return;
    }
    if (artChar == "EllieMilitary")
    {
    	$("#vhImg").attr('src', "img/ChrEllieMilitary/ChrEllieMilitary_Neutral.png");
    	return;
    }
    
    $("#vhImg").css("box-shadow", "0 0 30px DarkRed");
    
    let emotion = "";
    if (event.data.message == "angry") {
        emotion = emotionImg('img/angry_face.png');

        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Angry.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Angry.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Angry.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Angry.jpg");
        }
    }
    if (event.data.message == "happy") {
        emotion = emotionImg('img/happy_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Smile.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Smile.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Smile.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Smile.jpg");
        }
    }
    if (event.data.message == "surprised") {
        emotion = emotionImg('img/shocked_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Surprise.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Surprise.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Suprise.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Surprise.jpg");
        }
    }
    if (event.data.message == "disgusted") {
        emotion = emotionImg('img/shocked_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Disgust.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Disgust.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Disgust.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Disgust.jpg");
        }
    }
    if (event.data.message == "afraid") {
        emotion = emotionImg('img/shocked_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Fear.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Fear.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Fear.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Fear.jpg");
        }
    }
    if (event.data.message == "sad") {
        emotion = emotionImg('img/sad_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Sad.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Sad.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Sad.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Sad.jpg");
        }
    }
    if (event.data.message == "insincereSmile") {
        emotion = emotionImg('img/happy_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_SmallSmile.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_SmallSmile.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_SmallSmile.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_SmallSmile.jpg");
        }
    }
    if (event.data.message == "neutral") {
        emotion = emotionImg('img/neutral_face.png');
        if (event.data.owner == 1) {
            if (artChar == "Brad")
                $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
            else if (artChar == "Ellie")
                $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
            else if (artChar == "Rens")
                $("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
            else if (artChar == "Laura")
                $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
        }
    }

    if (containsAgentData(event)) {
        showChatMessage(agentMessage(emotion))
    }
    else {
        showChatMessage(userMessage(emotion))
    }

    neutralTimeout = setTimeout(function () {
        if (artChar == "Brad")
            $("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
        else if (artChar == "Ellie")
            $("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
        else if (artChar == "Rens")
            $("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
        else if (artChar == "Laura")
            $("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
        $("#vhImg").css("box-shadow", "");

    }, event.data.duration);

    scrollChatToTop()
}

function OFFER_IN_PROGRESS(event, socket) {
    if (containsAgentData(event)) {
        showChatMessage(agentWaitingMessage())
    } else {
        showChatMessage(userWaitingMessage())
    }
    scrollChatToTop()
    updateButtonPanelItem("butSendOffer", false, false)
    updateButtonPanelItem("butStartOffer", true, false)

    $(".itemButton").prop("disabled", true);
    disableExchangeTable(true)
}
//TODO deprecated
function SEND_MESSAGE(event, socket) {
    console.warn("A generic SEND_MESSAGE event has occurred... this shouldn't happen!")
}

function SEND_MESSAGE__FAVOR_REQUEST(event, socket) {
    removeWaitingMessage()
    if (containsAgentData(event)) {
        showChatMessage(agentGreenMessage(event.data.message))
        flashMessage("TIP: Your opponent has asked for a favor!")
        updateButtonPanelItem("butAccept", false, undefined)
        updateButtonPanelItem("butReject", false, undefined)
        updateButtonPanelItem("butAcceptFavor", true, false)
        updateButtonPanelItem("butRejectFavor", true, false)
    } else {
        showChatMessage(userGreenMessage(event.data.message))
        updateButtonPanelItem("butAcceptFavor", false, undefined)
        updateButtonPanelItem("butRejectFavor", false, undefined)
    }
    scrollChatToTop()
}

function SEND_MESSAGE__NONE(event, socket) {
    removeWaitingMessage()
    if (containsAgentData(event)) {
        showChatMessage(agentMessage(event.data.message))
    } else {
        showChatMessage(userMessage(event.data.message))
    }
    scrollChatToTop()
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
    console.info("A SEND_MESSAGE event with a NONE subclass occurred... are you sure this is what you wanted?")
}

function messageProcessGeneric(event, socket, tip)
{
	removeWaitingMessage()
    if (containsAgentData(event)) {
        if (tip !== undefined) 
        	flashMessage(tip)
        else
        	flashMessage(event.data.message)
        showChatMessage(agentMessage(event.data.message))
    } else {
        showChatMessage(userMessage(event.data.message))
    }
    scrollChatToTop()
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
}

function messageProcessGreenGeneric(event, socket, tip)
{
	removeWaitingMessage()
    if (containsAgentData(event)) {
        if (tip !== undefined)
        	flashMessage(tip)
        else
        	flashMessage(event.data.message)
        showChatMessage(agentGreenMessage(event.data.message))
    } else {
        showChatMessage(userGreenMessage(event.data.message))
    }
    scrollChatToTop()
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
}


function SEND_MESSAGE__FAVOR_ACCEPT(event, socket) {
    messageProcessGreenGeneric(event, socket)
}

function SEND_MESSAGE__FAVOR_REJECT(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__FAVOR_RETURN(event, socket) {
	messageProcessGreenGeneric(event, socket, "TIP: Your opponent is returning the favor!")
}

function SEND_MESSAGE__PREF_REQUEST(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__PREF_INFO(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__PREF_SPECIFIC_REQUEST(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__PREF_WITHHOLD(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Your oppoent is reluctant to tell you something...")
}

function SEND_MESSAGE__BATNA_REQUEST(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Use the general options menu to discuss!")
}

function SEND_MESSAGE__BATNA_INFO(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__OFFER_REQUEST_POS(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Send an offer!")
}

function SEND_MESSAGE__OFFER_REQUEST_NEG(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Send an offer!")
}

function SEND_MESSAGE__OFFER_PROPOSE(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__THREAT_POS(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Your opponent may walk away...")
}

function SEND_MESSAGE__THREAT_NEG(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Your opponent may walk away...")
}

function SEND_MESSAGE__CONFUSION(event, socket) {
	messageProcessGeneric(event, socket, "TIP: Be careful of contradicting yourself.")
}

function SEND_MESSAGE__OFFER_ACCEPT(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__OFFER_REJECT(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__GENERIC_POS(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__GENERIC_NEG(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__TIMING(event, socket) {
	messageProcessGeneric(event, socket)
}

function SEND_MESSAGE__PROPOSAL(event, socket) {
	messageProcessGeneric(event, socket)
}

function FORMAL_ACCEPT(event, socket) {
    updateButtonPanelItem("butFormalAccept", true, false)
    updateButtonPanelItem("butAccept", false, undefined)
    updateButtonPanelItem("butFormalQuit", false, true)
    $("#butFormalAccept").prop("value", "Accept Formally (WILL END GAME)");
}

function FORMAL_QUIT(event, socket) {
    updateButtonPanelItem("butFormalQuit", true, false)
    updateButtonPanelItem("butAccept", false, undefined)
    updateButtonPanelItem("butFormalAccept", false, true)
    $("#butFormalQuit").prop("value", "Formally Walk Away (WILL END GAME)");
}

function menu(event, socket) {
    printMenu(event.data);
}

function cursorStatus(event, socket) {
    if (event.data == "default") {
        $("body").css("cursor", "default")
        updateExchangeInputCursor($('#exchange-grid .card-body').hasClass("disabled"))
    } else {
        $("body, .exchange-table-row-item input").css("cursor", "url(" + event.data + "),crosshair")
    }    
}

function points(event, socket) {
    total = 0;
    for (i = 0; i < event.data.length; i++) {
        $("#labelPoints" + i).text(event.data[i]);
        total += event.data[i];
    }
    $("#labelPointsTotal").text(total);
}

function history(event, socket) {
    dialog(event.data);
}

function playerPointStruct(event, socket) {
    var html = event.data.replace(/\r?\n/g, "<br />");
    dialog(html);
}

function playerPointStructAdv(event, socket) {
    var html = event.data.replace(/\r?\n/g, "<br />");
    dialog(html);
}

function compareFirstItem(event, socket) {
    if (event.data == -1)
        $("#butItemFirst").attr('src', "img/white.png");
    else if (event.data == 0)
        $("#butItemFirst").attr('src', "img/item0.png" + "?" + Math.random());
    else if (event.data == 1)
        $("#butItemFirst").attr('src', "img/item1.png" + "?" + Math.random());
    else if (event.data == 2)
        $("#butItemFirst").attr('src', "img/item2.png" + "?" + Math.random());
    else if (event.data == 3)
        $("#butItemFirst").attr('src', "img/item3.png" + "?" + Math.random());
    else if (event.data == 4)
        $("#butItemFirst").attr('src', "img/item4.png" + "?" + Math.random());
    else
        $("#butItemFirst").attr('src', "img/item0.png" + "?" + Math.random());
}

function compareSecondItem(event, socket) {
    if (event.data == -1)
        $("#butItemSecond").attr('src', "img/white.png");
    else if (event.data == 0)
        $("#butItemSecond").attr('src', "img/item0.png" + "?" + Math.random());
    else if (event.data == 1)
        $("#butItemSecond").attr('src', "img/item1.png" + "?" + Math.random());
    else if (event.data == 2)
        $("#butItemSecond").attr('src', "img/item2.png" + "?" + Math.random());
    else if (event.data == 3)
        $("#butItemSecond").attr('src', "img/item3.png" + "?" + Math.random());
    else if (event.data == 4)
        $("#butItemSecond").attr('src', "img/item4.png" + "?" + Math.random());
    else
        $("#butItemSecond").attr('src', "img/item0.png" + "?" + Math.random());
}

function chatTextTemp(event, socket) {
    $(".compare-panel-message").removeClass("hidden");
    $(".compare-panel-message").text(event.data);
}

function chatTextFinalized(event, socket) {
    showChatMessage(userMessage(event.data))
    scrollChatToTop()
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
    updateButtonPanelItem("butFormalQuit", false, true)
}

function chatTextFinalizedLiar(event, socket) {
    showChatMessage(userRedMessage(event.data))
    scrollChatToTop()
    updateButtonPanelItem("butAcceptFavor", false, undefined)
    updateButtonPanelItem("butRejectFavor", false, undefined)
    updateButtonPanelItem("butFormalQuit", false, true)
}

function emoteMessage(event, socket) {
    if (event.data == "neutral")
        showChatMessage(userEmoteMessage('img/neutral_face.png'))
    if (event.data == "angry")
        showChatMessage(userEmoteMessage('img/angry_face.png'))
    if (event.data == "happy")
        showChatMessage(userEmoteMessage('img/happy_face.png'))
    if (event.data == "surprised")
        showChatMessage(userEmoteMessage('img/shocked_face.png'))
    if (event.data == "sad")
        showChatMessage(userEmoteMessage('img/sad_face.png'))
    scrollChatToTop()
}

function offerFinalized(event, socket) {
    removeWaitingMessage();
    dialogWaitForEnd(event.data);
}

function notMatched(event, socket) {
    removeWaitingMessage();
    dialogPauseTimer(event.data);
}

function intermediate(event, socket) {
    dialogIntermediate(event.data);

}

function trueEnd(event, socket) {
    dialogRedirect(event.data);
}

function negotiationEnd(event, socket) {
    removeWaitingMessage();
    dialogWaitForEnd(event.data);
}

function negotiationWarn(event, socket) {
    removeWaitingMessage();
    dialog("Only one minute remains in this negotiation!");
}

function GAME_END(event, socket) {
	console.log("GAME_END received");
    timer = 0;
    neutralGlow = true;
    angerGlow = false;
    heppyGlow = false
    sadGlow = false
    surprisedGlow = false;

    $(".issueButton").prop("disabled", true);
    disableExchangeTable(true)

    clearChat()
    
//TODO make "videoGame" a real variable   
//    if(videoGame)
//    	disconnectRtc()
   

    updateButtonPanelItem("butFormalAccept", false, true)
    updateButtonPanelItem("butFormalQuit", false, true)

    socket.send(JSON.stringify({ tag: "ngPing", data: "ngPing" }));

    startup();
}

function containsAgentData(event) {
    return event.data && event.data.owner == 1
}

function containsUserData(event) {
    return event.data && event.data.owner == 0
}