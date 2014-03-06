$(document).ready(function() { 
	"use strict";
    $('#exportPassphrase').keyup(clearValidationContent);
    $('#exportPassphraseAgain').focus(validationSetup);
    $('#exportPassphraseAgain').blur(validationTeardown);
    $('#exportPassphraseAgain').keyup(function() { validatePassword(this); });
});

function validationSetup() {
	$(this).parent().parent().removeClass("has-success has-error");
	$(this).parent().children(".form-control-feedback")
		.removeClass("glyphicon glyphicon-ok glyphicon-exclamation-sign");
	validatePassword(this);
}

function validatePassword(source) {
	var matches = $('#exportPassphrase').val() == $(source).val();
	var $inputGroup = $(source).parent().parent();
	$inputGroup.toggleClass("has-success", matches);
	var $feedback = $(source).parent().children(".form-control-feedback");
	$feedback.toggleClass("glyphicon glyphicon-ok", matches);
	$feedback.css("display", matches ? "block" : "none");
}

function validationTeardown() {
	var matches = $('#exportPassphrase').val() == $(this).val();
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
		$feedback.attr("title", "Passphrase entries do not match.");
		$feedback.css("display", "block");
	}
}

function clearValidationContent() {
	if ($(this).val() != $('#exportPassphraseAgain').val()) {
		$('#exportPassphraseAgain').val("");
	}
}
