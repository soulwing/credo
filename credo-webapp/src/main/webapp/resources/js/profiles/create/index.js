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

    validatePassword($("#password"), $("#passwordAgain"));
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
