
function validatePassword($password, $passwordAgain) {
	var validator = {
	    validatePassword: function(source) {
	    	var $password = $(source).data("$password");
	    	var matches = $password.val() == $(source).val()
	    	           && $password.val().length > 0;
	    	var $inputGroup = $(source).parent().parent();
	    	$inputGroup.toggleClass("has-success", matches);
	    	var $feedback = $(source).parent().children(".form-control-feedback");
	    	$feedback.toggleClass("glyphicon glyphicon-ok", matches);
	    	$feedback.css("display", matches ? "block" : "none");
	    },
		setup: function() {
			$(this).parent().parent().removeClass("has-success has-error");
			$(this).parent().children(".form-control-feedback")
				.removeClass("glyphicon glyphicon-ok glyphicon-exclamation-sign");
		},
	    teardown: function() {
	    	var $password = $(this).data("$password");
	    	var matches = $password.val() == $(this).val();
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
	};

    $password.data("$passwordAgain", $passwordAgain);
    $passwordAgain.data("$password", $password);

    $password.keyup(function() { 
    	var $passwordAgain = $(this).data("$passwordAgain");
    	if ($(this).val() != $passwordAgain.val()) {
    		$passwordAgain.val("");
    	}
    });
    
    $passwordAgain.focus(function() { 
    	validator.setup(); 
    	validator.validatePassword(this); 
    });
    
    $passwordAgain.blur(validator.teardown);
    
    $passwordAgain.keyup(function() { 
    	validator.validatePassword(this); 
    });
};
