let currentUtteranceTimeout = null
function emotionImg(src) {
	return `<img class='emoticon-item' src='${src}' alt=''>`
}

function agentMessage(msg) {
	return `<div class='message alert alert-secondary agent-bubble'>${msg}</div>`
}

function userMessage(msg) {
	return `<div class='message alert alert-primary user-bubble'>${msg}</div>`
}

function agentRedMessage(msg) {
	return `<div class='message alert alert-secondary override-red agent-bubble'>${msg}</div>`
}

function userRedMessage(msg) {
	return `<div class='message alert alert-primary override-red user-bubble'>${msg}</div>`
}

function agentGreenMessage(msg) {
	return `<div class='message alert alert-secondary override-green agent-bubble'>${msg}</div>`
}

function userGreenMessage(msg) {
	return `<div class='message alert alert-primary override-green user-bubble'>${msg}</div>`
}

function agentEmoteMessage(src) {
	return agentMessage(emotionImg(src))
}

function userEmoteMessage(src) {
	return userMessage(emotionImg(src))
}

function agentWaitingMessage() {
	return "<div class='alert alert-secondary agent-bubble waitingImage'><img class='waitGif' src='img/thinking.gif' alt='Your agent is crafting an offer...' /></div>"
}

function userWaitingMessage() {
	return "<div class='alert alert-primary user-bubble waitingImage'><img class='waitGif' src='img/thinking.gif' alt='Opponent is crafting an offer...' /></div>"
}

function removeWaitingMessage() {
	$(".waitingImage").remove();
}

function scrollChatToTop () {
	$('.chat-panel').scrollTop($('.chat-panel').prop("scrollHeight"));
}

function glowEmotionItem(id, glow) {
	if (glow) {
		$(`#${id}`).addClass("active")
	} else {
		$(`#${id}`).removeClass("active")
	}
}

function disableExchangeTable(disabled) {
    $('#exchange-grid input').attr({ disabled })
    updateExchangeInputCursor(disabled)
	if (disabled) {
        $('#exchange-grid *').addClass("disabled")
	} else {
        $('#exchange-grid *').removeClass("disabled")
    }    
}

function updateExchangeInputCursor(disabled) {
    $(".exchange-table-row-item input").css("cursor", disabled ? "not-allowed" : "pointer")
}

function showChatMessage(content) {
    $(".messageHistory").append(content)
}

function clearChat() {
    $(".messageHistory").empty()
}

function flashMessage(msg) {
    $("#vhCurrentUtterance").text(msg).removeClass('fade').addClass('show')
    if (currentUtteranceTimeout) {
        clearTimeout(currentUtteranceTimeout)
    }
    currentUtteranceTimeout = setTimeout(() => {
        $("#vhCurrentUtterance").removeClass('show').addClass('fade')
    }, 4000);
}

function dialog(content) {
	showDialog(content, undefined)
}

function dialogWaitForEnd(content) {
	holdingForUser = true;
	showDialog(content, function(event) {
		readyToRestart = true;
		holdingForUser = false;
	})
}

function dialogPauseTimer(content) {
	holdingForUser = true;
	showDialog(content, function(event) {
		readyToRestart = false;
		holdingForUser = false;
	})
}

function dialogIntermediate(content) {
	holdingForUser = true;
	showDialog(content, function(event) {        
		readyToRestart = false;
		holdingForUser = false;
		webSocketSend({ tag: "newGameStart", data: "" })
	})
}

function dialogStartGame(content) {
	holdingForUser = true;
	showDialog(content, function(event) {
		readyToRestart = false;
        holdingForUser = false;
        // Start the first game after the initial dialog box has been closed.
        webSocketSend({ tag: "startGame", data: "" })
        establishRtcConnection({ roomId: getRoomId(), userId: getUserId() })
	})
}

function getRoomId() {
	return sessionStorage.getItem('roomId')
}

function getUserId() {
	return sessionStorage.getItem('userId')
}

function dialogRedirect(content) {
	showDialog("Thanks for playing!  Please click ok to be redirected to the final survey!", function(event) {
        window.location.replace(content);
        closeSocket();
    })
}

function showDialog(content, onOk) {
	$('#modalDialogBody').html(content)
	$('#modalDialog').modal('show')
	$("#modalDialogOk").on('click', function(event) {
        event.preventDefault()
        if (typeof onOk === "function") {
        	onOk()
        }
    })
}

function updateButtonPanelItem(id, show, disable) {
	if (disable !== undefined) {
		$(`#${id}`).prop("disabled", !!disable)
	}
	if (show === true) {
		$(`#${id}`).removeClass("d-none")
	} else if (show === false) {
		$(`#${id}`).addClass("d-none")
	}
}

function toggleSurveyDiag(show, data) {
    if (show) {
        $("#surveyWrapper").html(data)
        $('#surveyModalDialog').modal('show')
    } else {
        $('#surveyModalDialog').modal('hide')
        $('.modal-backdrop').remove()
        $("#surveyWrapper").html(data)
    }
}

var $el = $("#mainContainer");
var elHeight = $el.outerHeight();
var elWidth = $el.outerWidth();

var $wrapper = $("#resizeMe");

function doResize(event, ui) {
  
  var scale, origin;
    
  scale = Math.min(
    ui.size.width / elWidth,    
    ui.size.height / elHeight
  );
  
  $el.css({
    transform: "translate(-50%, -50%) " + "scale(" + scale + ")"
  });
  
}


