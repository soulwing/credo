
$(document).ready(function() { 
	var passwordTimeout = function() { };
	
	var $password = $("[id$=':password']");
	var $inputGroup = $password.parent().parent();
	var $feedback = $password.parent().children(".form-control-feedback");
	
	$password.on("input", function(event) {
		var source = this;
		var $correct = $("[id$=':correct']");
		var applyFeedback = function(ok) {
	    	$inputGroup.toggleClass("has-success", ok);
	    	$feedback.toggleClass("glyphicon glyphicon-ok", ok);
	    	$feedback.toggleClass("hidden", !ok);
		};
		var validate = function(data) {
			if (data.status == "success") {
				$correct = $("[id$=':correct']");
				applyFeedback($correct.val() == "true");
			}			
		};		
		var ajaxRequest = function() {
			jsf.ajax.request(source, event, {
				render: $correct.attr("id"),
				onevent: validate,
				onerror: function() { applyFeedback(false); }
			});			
		};
		window.clearTimeout(passwordTimeout);
		passwordTimeout = window.setTimeout(ajaxRequest, 250);
		return false;
	});	
});
