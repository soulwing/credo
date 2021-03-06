$(document).ready(function() { 
	"use strict";
	validatePassword($("[id$=':exportPassphrase']"), $("[id$=':exportPassphraseAgain']"));
	
	$("#btn-generate").on("click", function(event) { 
		var target = $("[id$=':btn-generate-command']");
		var wrapper = function(event) {
			target.trigger(event);
		};
		$("#exportPassphrase").val("not-empty");
		wrapper.call(target, event);
	});
	
	var $revealButton = $("#btn-reveal");
	$revealButton.on("click", function() { 
		var $input = $("[id$=':exportPassphraseAgain']");
		var $icon = $("#btn-reveal i");
		if ($input.attr("type") == "password") {
			$input.attr("type", "text");
			$input.select();
			$icon.removeClass("glyphicon-eye-open");
			$icon.addClass("glyphicon-eye-close");
		}
		else {
			$input.attr("type", "password");
			$icon.removeClass("glyphicon-eye-close");
			$icon.addClass("glyphicon-eye-open");
		}
	});
});
