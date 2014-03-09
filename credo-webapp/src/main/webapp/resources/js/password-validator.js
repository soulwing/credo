
function validatePassword($password, $passwordAgain) {
	var validator = {
		updateFeedback: function(ok, $inputGroup, $feedback) {
	    	$inputGroup.toggleClass("has-success", ok);
	    	$feedback.toggleClass("glyphicon glyphicon-ok", ok);
	    	$feedback.css("display", ok ? "block" : "none");			
		},
		negativeFeedback: function($inputGroup, $feedback) {
	    	$inputGroup.removeClass("has-success");
	    	$feedback.removeClass("glyphicon glyphicon-ok");
	    	$inputGroup.addClass("has-error");
    		$feedback.addClass("glyphicon glyphicon-exclamation-sign");
	    	$feedback.css("display", "block");			
    		$feedback.attr("title", "Password entries do not match.");
		},
		removeFeedback: function($inputGroup, $feedback) {
	    	$inputGroup.removeClass("has-success has-error");
			$feedback.removeClass("glyphicon glyphicon-ok glyphicon-exclamation-sign");
			$feedback.attr("title", "");
		},
	    validatePassword: function(source) {
	    	var $password = $(source).data("$password");
	    	var $inputGroup = $(source).parent().parent();
	    	var $feedback = $(source).parent().children(".form-control-feedback");
	    	var matches = $password.val() == $(source).val()
	    	           && $password.val().length > 0;
	    	this.updateFeedback(matches, $inputGroup, $feedback);
	    },
		setup: function(source) {
	    	var $inputGroup = $(source).parent().parent();
	    	var $feedback = $(source).parent().children(".form-control-feedback");
	    	this.removeFeedback($inputGroup, $feedback);
		},
	    teardown: function(source) {
	    	var $password = $(source).data("$password");
	    	var $inputGroup = $(source).parent().parent();
	    	var $feedback = $(source).parent().children(".form-control-feedback");
	    	var matches = $password.val() == $(source).val();
	    	if (matches) {
	    		this.removeFeedback($inputGroup, $feedback);
	    	}
	    	else {
	    		this.negativeFeedback($inputGroup, $feedback);
	    	}
	    }
	};

    $password.data("$passwordAgain", $passwordAgain);
    $passwordAgain.data("$password", $password);

    $password.on("input", function() { 
    	var $passwordAgain = $(this).data("$passwordAgain");
    	if ($(this).val() != $passwordAgain.val()) {
    		$passwordAgain.val("");
    	}
    });
    
    $passwordAgain.focus(function() {
    	validator.setup.call(validator, this);
    	validator.validatePassword.call(validator, this); 
    });
    
    $passwordAgain.blur(function() { 
    	validator.teardown.call(validator, this); 
    });
    
    $passwordAgain.on("input", function() { 
    	validator.validatePassword.call(validator, this); 
    });
};
