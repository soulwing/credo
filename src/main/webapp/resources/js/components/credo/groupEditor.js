$(document).ready(function() {
	var $owner = $("[id$=':owner']");
	var $ownerStatus = $("[id$=':ownerStatus']");
	var $inputGroup = $owner.parent().parent();
	var $feedback = $owner.parent().children(".form-control-feedback");
	var $helpNotFound = $("#help-not-found");
	var $helpInaccessible = $("#help-inaccessible");
	var $ownerErrors = $("[id$=':ownerErrors']");

	var $members = $("#members");
	var $available = $("#available");
	var $memberFilter = $("#member-filter");
	var $memberFilterButton = $("#btn-member-filter");
	var $availableFilter = $("#available-filter");
	var $availableFilterButton = $("#btn-available-filter");
	var $filteredMembers = $("#filtered-members");
	var $filteredAvailable = $("#filtered-available");
	var $addButton = $("#btn-add");
	var $removeButton = $("#btn-remove");
	var $resetButton = $("#btn-reset");
	var $submitButton = $("input[type='submit']");
	
	var groups = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			url: $owner.parent().data("autocomplete-url"),
			// the data is returned as an array of string group names
			// convert to an array of objects with a name property
			filter: function(list) {
				return $.map(list, function(group) { return { name: group }; });
			}
		}
	});
	
	groups.clearPrefetchCache();
	groups.initialize();
	
	$owner.typeahead(
		{
			minLength: 1,
			highlight: true,
		},
		{
			name: 'groups',
			displayKey: 'name',
			source: groups.ttAdapter()
		}
	);
	
	var optionSort = function(a, b) { return a.text > b.text; };
	
	var updateFeedback = function() {
		if ($ownerErrors.text().length > 0) {
			$inputGroup.removeClass("has-warning has-success");
			$inputGroup.addClass("has-error");
			$feedback.removeClass("glyphicon glyphicon-ok");
			$feedback.addClass("glyphicon glyphicon-exclamation-sign");
			$feedback.removeClass("hidden");
			$helpNotFound.addClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.removeClass("hidden");
			return;
		}		
		var status = $ownerStatus.val();
		if (status == "EXISTS") {
			hideFeedback();
		}
		else if (status == "NOT_FOUND") {
			$inputGroup.removeClass("has-error has-success");
			$inputGroup.addClass("has-warning");
			$feedback.removeClass("glyphicon glyphicon-ok");
			$feedback.addClass("glyphicon glyphicon-exclamation-sign");
			$feedback.removeClass("hidden");
			$helpNotFound.removeClass("hidden");
			$helpInaccessible.addClass("hidden");
			$ownerErrors.addClass("hidden");
		}
		else if (status == "INACCESSIBLE") {
			$inputGroup.removeClass("has-warning has-success");
			$inputGroup.addClass("has-error");
			$feedback.removeClass("glyphicon glyphicon-ok");
			$feedback.addClass("glyphicon glyphicon-exclamation-sign");
			$feedback.removeClass("hidden");
			$helpNotFound.addClass("hidden");
			$helpInaccessible.removeClass("hidden");
			$ownerErrors.addClass("hidden");
		}
	};

	$owner.on("focus", function(event) { 
		hideFeedback();
		return false;
	});
	
	$owner.on("blur", function(event) { 
		var source = this;
		var ajaxRequest = function() { 
			jsf.ajax.request(source, event, {
				execute: $owner.attr("id"),
				render: $ownerStatus.attr("id") + " " + $ownerErrors.attr("id"),
				resetValues: true,
				onevent: function(data) {
					if (data.status == "success") {
						$ownerErrors = $("[id$=':ownerErrors']");
						updateFeedback();
					}			
				}
			});
		};
		ajaxRequest();
		return false;
	});

	if ($ownerStatus.val() != "EXISTS") {
		updateFeedback();
	}

	var split = function($a, $b) {
		var $source = $("[id$=':members']");
		var $m = $source.children().filter("option:selected");
		$m.remove();
		$m.prop("selected", false);
		$a.empty();
		$a.append($m);
		$b.empty();
		$b.append($source.children());
		$source.empty();
	};
	
	var transfer = function($a, $b) {
		var $selected = $a.children().filter("option:selected");
		$selected.remove();
		$b.append($selected);
		var $sorted = $b.children().sort(optionSort);
		$sorted.remove();
		$b.append($sorted);
	};
	
	var filter = function(text, $visible, $filtered) {
		$visible.append($filtered.children());
		$filtered.empty();
		var re = new RegExp(text, "i");
		var $noMatch = $visible.children().filter(function(index) {
			var text = this.text;
			return text.match(re) == undefined; 
		});
		$noMatch.remove();
		$filtered.append($noMatch);
		var $sorted = $visible.children().sort(optionSort);
		$sorted.remove();
		$visible.append($sorted);
		$visible.children().prop("selected", false);
	};
	
	var bindListeners = function() {
		split($members, $available);

	};

	$members.on("focus", function(event) { 
		$available.children().filter("option:selected").prop("selected", false);
	});
	
	$available.on("focus", function(event) { 
		$members.children().filter("option:selected").prop("selected", false);
	});

	$memberFilter.on("input", function(event) {
		var text = $(this).val();
		filter(text, $members, $filteredMembers);
		return false;
	});

	$memberFilterButton.on("click", function(event) {
		$memberFilter.val("");
		filter("", $members, $filteredMembers);
		return false;
	});
	
	$availableFilter.on("input", function(event) {
		var text = $(this).val();
		filter(text, $available, $filteredAvailable);
		return false;
	});
	
	$availableFilterButton.on("click", function(event) {
		$availableFilter.val("");
		filter("", $available, $filteredAvailable);
		return false;
	});
	
	$addButton.on("click", function(event) {
		$memberFilter.val("");
		filter("", $members, $filteredMembers);
		transfer($available, $members);
		return false;
	});
	
	$removeButton.on("click", function(event) {
		$availableFilter.val("");
		filter("", $available, $filteredAvailable);
		transfer($members, $available);
		return false;
	});
	
	$resetButton.on("click", function(event) {
		$memberFilter.val("");
		$availableFilter.val("");
		$available.empty();
		$filteredMembers.empty();
		$filteredAvailable.empty();
		var boundMembers = $("[id$=':members']").attr("id");
		jsf.ajax.request(this, event, {
			render: boundMembers,
			onevent: function(data) {
				if (data.status == "success") {
					split($members, $available);
				}			
			}
		});
		return false;
	});

	var editing = $("#editing").text();
	if (editing == "false") {
		$resetButton.prop("disabled", true);
	}
	
	$submitButton.on("click", function(event) { 
		var $boundMembers = $("[id$=':members']");
		var $selected = $members.children().prop("selected", true);
		var $notSelected = $available.children().prop("selected", false);
		$boundMembers.append($selected);
		$boundMembers.append($notSelected);
		return true;
	});
	
	bindListeners();
});
