<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<div class="modal fade" id=surveyModalDialog tabindex="-1" data-backdrop="static" data-keyboard="false" role="dialog" aria-labelledby="surveyModalDialogLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Survey</h5>
            </div>
            <div class="modal-body">
                <div class="survey instructions">
                    <p>
                        Please answer these short few questions before continuing to the next game. Your partner will NOT see your answers. You will negotiate with the same Artificial Intelligence partner you had in the last negotiation, regardless of your answers.
                    </p>
                    Press the submit button when you're finished making your selections.
                </div>
                <br>
                <div class="iagoSurvey">
                    <p class="survey-question">(1) How much do you like your opponent?</p>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like1" value="1">
                        <label>Strongly Dislike</label>
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like2" value="2">
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like3" value="3">
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like4" value="4">
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like5" value="5">
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like6" value="6">
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="like" id="like7" value="7">
                        <label>Strongly Like</label>
                    </div>
                    <br>
                    <p class="survey-question">(2) Did you agree to do a favor for your opponent in the last game?</p>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="favor" id="favorYes" value="Yes">
                        <label>Yes</label>
                    </div>
                    <div class="survey-form-item">
                        <input class="iagoCheckableQuestion" type="radio" name="favor" id="favorNo" value="No">
                        <label>No</label>
                    </div>
                </div>
                <br>

             
            </div>
            <div class="modal-footer">
                <input type="button" value="Submit" class="btn btn-primary" onclick="submitAll()">
            </div>
        </div>
    </div>
</div>