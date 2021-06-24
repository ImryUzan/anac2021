<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <link href="css/iago.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>IAGO</title>
</head>

<body>
    <main>
        <div id="resizeMe">
            <div class="container" id="mainContainer">

                <div class="column">
                    <div class="card user-info" id="userContainer" hidden="true">
                        <div class="card-body">
                            <video id="remote-video" class="remote" autoplay></video>
                            <video id="local-video" class="local" autoplay></video>
                            <div class="time" id="negoTimerVideo">Time Remaining: 6:40</div><!-- //TODO fix timer ID -->
                        </div>
                    </div>
                    <div class="card vh-info" id="vhContainer">
                        <div class="card-body">
                            <div class="avatar">
                                <img class="rounded" id="vhImg" alt="Avatar of other player" src="img/unset_vh.png">
                            </div>
                            <div class="info" id="descriptionContainer">
                                <!--  <div class="name">Laura</div>-->
                                <div class="status text-muted" id="vhDescription">Your opponent will be described here!</div>
                                <div class="alert alert-light" id="vhCurrentUtterance"></div>
                                <div class="time" id="negoTimer">Time Remaining: 0:00</div>
                            </div>
                        </div>
                    </div>
                    <div class="card exchange-table" id="exchange-grid">
                        <div class="card-body">
                            <div class="container">
                                <div class="row">
                                    <small class="exchange-table-row-item text-muted">Opponent's</small>
                                </div>
                                <div class="row exchange-table-row">
                                    <div class="exchange-table-row-item item-col0">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol0Row0"> <span class="badge badge-secondary count-badge" id="itemLabelCol0Row0">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col1">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol1Row0"> <span class="badge badge-secondary count-badge" id="itemLabelCol1Row0">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col2">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol2Row0"> <span class="badge badge-secondary count-badge" id="itemLabelCol2Row0">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col3">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol3Row0"> <span class="badge badge-secondary count-badge" id="itemLabelCol3Row0">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col4">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol4Row0"> <span class="badge badge-secondary count-badge" id="itemLabelCol4Row0">0</span>
                                    </div>
                                </div>

                                <div class="row">
                                    <small class="exchange-table-row-item text-muted">Undecided</small>
                                </div>
                                <div class="row exchange-table-row">
                                    <div class="exchange-table-row-item item-col0">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol0Row1"> <span class="badge badge-secondary count-badge" id="itemLabelCol0Row1">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col1">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol1Row1"> <span class="badge badge-secondary count-badge" id="itemLabelCol1Row1">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col2">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol2Row1"> <span class="badge badge-secondary count-badge" id="itemLabelCol2Row1">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col3">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol3Row1"> <span class="badge badge-secondary count-badge" id="itemLabelCol3Row1">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col4">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol4Row1"> <span class="badge badge-secondary count-badge" id="itemLabelCol4Row1">0</span>
                                    </div>
                                </div>

                                <div class="row">
                                    <small class="exchange-table-row-item text-muted">Yours</small>
                                </div>
                                <div class="row exchange-table-row">
                                    <div class="exchange-table-row-item item-col0">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol0Row2"> <span class="badge badge-secondary count-badge" id="itemLabelCol0Row2">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col1">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol1Row2"> <span class="badge badge-secondary count-badge" id="itemLabelCol1Row2">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col2">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol2Row2"> <span class="badge badge-secondary count-badge" id="itemLabelCol2Row2">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col3">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol3Row2"> <span class="badge badge-secondary count-badge" id="itemLabelCol3Row2">0</span>
                                    </div>
                                    <div class="exchange-table-row-item item-col4">
                                        <input class="rounded border exchange-table-row-item-image" type="image" src="img/white.png" id="butCol4Row2"> <span class="badge badge-secondary count-badge" id="itemLabelCol4Row2">0</span>
                                    </div>
                                </div>

                                <div class="row">
                                    <small class="exchange-table-row-item text-muted">Your
                                        points</small>
                                    <div class="exchange-table-row-item item-col1"></div>
                                    <div class="exchange-table-row-item item-col2"></div>
                                    <div class="exchange-table-row-item item-col3"></div>
                                    <div class="exchange-table-row-item item-col4"></div>
                                    <small class="exchange-table-row-item text-muted total">Total</small>
                                </div>

                                <div class="row exchange-table-row total-row">
                                    <div class="exchange-table-row-item item-col0">
                                        <strong class="border" id="labelPoints0"> 0 </strong>
                                    </div>
                                    <div class="exchange-table-row-item item-col1">
                                        <strong class="border" id="labelPoints1"> 0 </strong>
                                    </div>
                                    <div class="exchange-table-row-item item-col2">
                                        <strong class="border" id="labelPoints2"> 0 </strong>
                                    </div>
                                    <div class="exchange-table-row-item item-col3">
                                        <strong class="border" id="labelPoints3"> 0 </strong>
                                    </div>
                                    <div class="exchange-table-row-item item-col4">
                                        <strong class="border" id="labelPoints4"> 0 </strong>
                                    </div>
                                    <div class="exchange-table-row-item total">
                                        <strong class="border" id="labelPointsTotal"> 0 </strong>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="card but-panel">
                        <div class="card-body">
                            <button type="button" id="butStartOffer" class="btn btn-outline-primary btn-sm" data-toggle="tooltip" title="Lets you move items on the table">Start an offer</button>
                            <button type="button" id="butSendOffer" class="btn btn-outline-primary btn-sm d-none" data-toggle="tooltip" title="Sends the current offer">Send
                                your offer</button>
                            <button type="button" id="butFormalAccept" class="btn btn-outline-primary btn-sm d-none" data-toggle="tooltip" title="If opponent accepts, game will end">Formally
                                accept offer</button>
                            <button type="button" id="butFormalQuit" class="btn btn-outline-danger btn-sm d-none" data-toggle="tooltip" title="If opponent quits, game will end">Formally
                                quit game</button>
                            <button type="button" id="butAccept" class="btn btn-outline-success btn-sm d-none" data-toggle="tooltip" title="Accepts an offer, but isn't binding">Accept
                                offer (non-binding)</button>
                            <button type="button" id="butReject" class="btn btn-outline-danger btn-sm d-none" data-toggle="tooltip" title="Rejects an offer, but isn't binding">Reject
                                offer (non-binding)</button>
                            <button type="button" id="butAcceptFavor" class="btn btn-outline-success btn-sm d-none" data-toggle="tooltip" title="Accepts a favor request, but isn't binding">Accept
                                favor (non-binding)</button>
                            <button type="button" id="butRejectFavor" class="btn btn-outline-danger btn-sm d-none" data-toggle="tooltip" title="Rejects a favor request, but isn't binding">Reject
                                favor (non-binding)</button>
                            <button type="button" id="butViewPayoffs" class="btn btn-outline-secondary btn-sm">View Payoffs</button>
                        </div>
                    </div>
                </div>



                <div class="column">
                    <div class="card chat-panel">
                        <div class="card-body">
                            <div class="messageHistory"></div>
                        </div>
                    </div>
                    <div class="card emoticons-panel">
                        <small class="flex-child"> Tip: The glowing icon is the one
                            you're currently showing! </small>
                        <div class="card-body">
                            <input class="emoticon-item button" id="butAnger" type="image" src="img/angry_face.png" /> <input class="emoticon-item button" id="butSad" type="image" src="img/sad_face.png" /> <input class="emoticon-item button" id="butNeutral" type="image" src="img/neutral_face.png" /> <input class="emoticon-item button" id="butSurprised" type="image" src="img/shocked_face.png" /> <input class="emoticon-item button" id="butHappy" type="image" src="img/happy_face.png" />
                        </div>
                    </div>
                    <div class="card chat-buttons">
                        <div class="messages flex-child flex-vert scrolling" id="messages">
                            <div class="buffer flex-child" id="messageBuffer"></div>
                            <div class="compare-panel-message flex-child" id="craftingMessageString">Move the items into the boxes to
                                describe what you want to say!</div>
                            <div class="compare-panel-message flex-child" id="BATNAInstructions">If you want to share your actual
                                walk-away value, press "Send" now. If you'd like to send a higher
                                walk-away value, move the slider to the right!</div>
                            <input class="butText flex-child button" type="button" id="butYouLike" value="foo"></input> <input class="butText flex-child button" type="button" id="butILike" value="foo"></input> <input class="butText flex-child button" type="button" id="butNoBut" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom1" value="foo"></input> <input class="butText flex-child btn-outline-success btn-sm button" type="button" id="butCustom2" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom3" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom1_1" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom2_1" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom3_1" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom1_2" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom2_2" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom3_2" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom1_3" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom2_3" value="foo"></input> <input class="butText flex-child button" type="button" id="butCustom3_3" value="foo"></input>

                            <div class="butContainer flex-child hidden" id="butItemsDiv">
                                <input class="rounded border compare-item" type="image" src="img/white.png" id="butItem0"> <input class="rounded border compare-item" type="image" src="img/white.png" id="butItem1"> <input class="rounded border compare-item" type="image" src="img/white.png" id="butItem2"> <input class="rounded border compare-item" type="image" src="img/white.png" id="butItem3"> <input class="rounded border compare-item" type="image" src="img/white.png" id="butItem4">
                            </div>
                            <div class="butContainer flex-child hidden" id="butItemsComparison">
                                <input class="rounded border compare-item" type="image" src="img/white.png" id="butItemFirst"> <input class="rounded border compare-item" type="image" src="img/gtsymbol.jpg" alt="best?" id="butItemComparison">
                                <input class="rounded border compare-item" type="image" src="img/white.png" id="butItemSecond">
                            </div>
                            <input class="butText flex-child button hidden" type="button" id="butExpl0" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl1" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl2" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl3" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl4" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl5" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl6" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl7" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl8" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl9" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl10" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl11" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl12" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl13" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl14" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl15" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl16" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl17" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl18" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl19" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl20" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl21" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl22" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl23" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl24" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl25" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl26" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl27" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl28" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl29" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl30" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butExpl31" value="foo"></input>

                            <div class="butText flex-child flex-vert button hidden" id="batnaSlider">
                                <input class="slider" type="range" value="foo" min="0" max="foo" id="mySlider"></input>
                                <div class="flex-child" id="batnaValue">I already have an
                                    offer for __ points.</div>
                                <div class="flex-child" id="batnaDescription">This is the
                                    truth.</div>
                            </div>
                            <input class="butText flex-child button hidden" type="button" id="butConfirm" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butSend" value="foo"></input> <input class="butText flex-child button hidden" type="button" id="butBack" value="foo"></input>
                        </div>
                    </div>
                </div>


                <div id="debug"></div>



            </div>
            <div id="surveyWrapper"></div>
            <div class="modal fade" id=modalDialog tabindex="-1" data-backdrop="static" data-keyboard="false" role="dialog" aria-labelledby="modalDialogLabel" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalDialogLabel">Game Message</h5>
                        </div>
                        <div class="modal-body" id="modalDialogBody">...</div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal" id="modalDialogOk">OK</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <footer class="hidden">
            <small> Icons made by <a href="https://www.flaticon.com/authors/pixel-perfect" title="Pixel perfect">Pixel perfect</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
            </small>
        </footer>
    </main>



    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

    <script src="js/iago.js" type="text/javascript"></script>
    <script src="js/utils.js" type="text/javascript"></script>
    <script src="js/ui.js" type="text/javascript"></script>
    <script src="js/events.js" type="text/javascript"></script>
    <script src="js/kurento-utils.min.js" type="text/javascript"></script>
    <script src="js/conference-client.js" type="text/javascript"></script>

</body>

</html>