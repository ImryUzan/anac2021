let webRtcPeer
let rtcWebSocket

const connectSocket = (roomId, userId) => {
    const host = ['localhost', '127.0.0.1'].indexOf(document.location.host.split(':')[0]) === -1 ? 'signaling.iagoonline.com' : '127.0.0.1:3000'
    const wsProtocol = location.protocol !== 'https:' ? 'ws:' : 'wss:'
    const url = `${wsProtocol}//${host}/ws/${roomId}/${userId}`
    return new WebSocket(url)
}

const disconnectRtc = () => {
    console.log('disconnectRtc')
    document.querySelector('#local-video').style = "display: none"
    document.querySelector('#remote-video').style = "display: none"
    rtcWebSocket.close()
    webRtcPeer && webRtcPeer.dispose()
    rtcWebSocket = null
}

const sendMessage = (message) => {
    if (rtcWebSocket) {
        rtcWebSocket.send(JSON.stringify(message))
    }
}

const handleIceCandidate = (candidate) => {
    sendMessage({
        action: 'iceCandidate',
        candidate: candidate
    })
}

const handleError = (error) => {
    console.error(error)
}

const createWebRTCPeer = (options) => {
    options.configuration = {
        iceServers: [
            { "urls": "stun:stun.l.google.com:19302" },
            // { "urls": "turn:111.222.333.444", "username": "user", "credential": "pass" }
        ]
    }

    webRtcPeer = kurentoUtils.WebRtcPeer.WebRtcPeerSendrecv(options, function (error) {
        if (error) return handleError(error)
        this.generateOffer(handleOffer)
    })
}

function handleOffer(error, sdpOffer) {
    if (error) return handleError(error)
    sendMessage({
        action: 'offer',
        offer: sdpOffer
    })
}

function establishRtcConnection({ roomId, userId }) {
    console.log('establishRtcConnection:', roomId, userId)
    rtcWebSocket = connectSocket(roomId, userId)
    window.onbeforeunload = function () {
        disconnectRtc()
    }
    rtcWebSocket.onmessage = function (event) {
        const message = JSON.parse(event.data)
        if (message.action === 'iceCandidate') {
            webRtcPeer.addIceCandidate(message.candidate)
        } else if (message.action === 'answer') {
            webRtcPeer.processAnswer(message.answer)
        } else {
            handleError('Unrecognized message: ' + event.data)
        }
    }

    setTimeout(function () {
        const localVideo = document.querySelector('#local-video')
        const remoteVideo = document.querySelector('#remote-video')
        createWebRTCPeer({
            localVideo: localVideo,
            remoteVideo: remoteVideo,
            onicecandidate: handleIceCandidate,
            mediaConstraints: {
                audio: false,
                video: {
                    width: 640,
                    framerate: 15
                }
            }
        })
        localVideo.style = "display: flex"
        remoteVideo.style = "display: flex"
    }, 500)
}
