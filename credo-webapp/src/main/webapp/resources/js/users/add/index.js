$(document).ready(function() { 
	"use strict";
    var options = {};
    options.common = {
        bootstrap3: true,
        minChar: 8,
        zxcvbn: true
    };
    options.ui = {
    	verdicts: ["Weak", "Moderate", "Moderate", "Strong", "Very Strong"],
    	container: "#pw-container",
    	showVerdictsInsideProgressBar: true,
    	viewports: {
        	progress: "#pw-progress",
        	verdict: "#pw-verdict"
    	}
    };
    $('#password').pwstrength(options);
    $('#password').focus(passwordSetup);
    $('#password').blur(passwordTeardown);
    $('#password').keyup(clearValidationContent);
    $('#passwordAgain').focus(validationSetup);
    $('#passwordAgain').blur(validationTeardown);
    $('#passwordAgain').keyup(function() { validatePassword(this); });

    forcePasswordUpdate();
    
});

function forcePasswordUpdate() {
    var $password = $('#password');
    if ($password.val().length == 0) return;

    if (typeof zxcvbn != "function") {
		setTimeout(forcePasswordUpdate, 250);
		return;
	}
	
	$password.triggerHandler("keyup");
	var $feedback = $("#pw-container .form-control-feedback");
	if ($password.val().length > 0) {
		$feedback.css("display", 
				"Weak" == $(".password-verdict").text() ? "block" : "none");
	}
}

function passwordSetup() {
	$('#pw-progress').css("display", "block");
	$(this).parent().children(".form-control-feedback").css("display", "none");
}

function passwordTeardown() {
	$("#pw-progress").css("display", "none");
	var $feedback = $(this).parent().children(".form-control-feedback");
	$feedback.css("display", 
			"Weak" == $(".password-verdict").text() ? "block" : "none");
}

function validationSetup() {
	$(this).parent().parent().removeClass("has-success has-error");
	$(this).parent().children(".form-control-feedback")
		.removeClass("glyphicon glyphicon-ok glyphicon-exclamation-sign");
	validatePassword(this);
}

function validatePassword(source) {
	var matches = $('#password').val() == $(source).val();
	var $inputGroup = $(source).parent().parent();
	$inputGroup.toggleClass("has-success", matches);
	var $feedback = $(source).parent().children(".form-control-feedback");
	$feedback.toggleClass("glyphicon glyphicon-ok", matches);
	$feedback.css("display", matches ? "block" : "none");
}

function validationTeardown() {
	var matches = $('#password').val() == $(this).val();
	var $inputGroup = $(this).parent().parent();
	$inputGroup.removeClass("has-success");
	$inputGroup.toggleClass("has-error", !matches);
	var $feedback = $(this).parent().children(".form-control-feedback");
	if (matches) {
		$feedback.removeClass("glyphicon glyphicon-ok");
		$feedback.attr("title", "");
	}
	else {
		$feedback.toggleClass("glyphicon glyphicon-exclamation-sign", !matches);
		$feedback.attr("title", "Password entries do not match.");
		$feedback.css("display", "block");
	}
}

function clearValidationContent() {
	if ($(this).val() != $('#passwordAgain').val()) {
		$('#passwordAgain').val("");
	}
}
