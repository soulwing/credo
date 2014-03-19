
$(document).ready(function() { 
	var passwordTimeout = function() { };
	var $password = $("[id$=':password']");
	$password.on("input", function(event) {
		var source = this;
		var $correct = $("[id$=':correct']");
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
