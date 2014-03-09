
$(document).ready(function() { 
	var passwordTimeout = function() { };
	var $password = $("[id$=':password']");
	$password.on("input", function(event) {
		var source = this;
		var $actual = $("[id$=':actual']");
		var applyFeedback = function(ok) {
			var $source = $("[id$=':password']");
	    	var $inputGroup = $source.parent().parent();
	    	$inputGroup.toggleClass("has-success", ok);
	    	var $feedback = $source.parent().children(".form-control-feedback");
	    	$feedback.toggleClass("glyphicon glyphicon-ok", ok);
	    	$feedback.css("display", ok ? "block" : "none");
		};
		var validate = function(data) {
			if (data.status == "success") {
				var $actual = $("[id$=':actual']");
				var $expected = $("[id$=':expected']");
				applyFeedback($expected.val() == $actual.val());
			}			
		};		
		var ajaxRequest = function() {
			jsf.ajax.request(source, event, {
				render: $actual.attr("id"),
				onevent: validate,
				onerror: function() { applyFeedback(false); }
			});			
		};
		window.clearTimeout(passwordTimeout);
		passwordTimeout = window.setTimeout(ajaxRequest, 250);
		return false;
	});	
});
