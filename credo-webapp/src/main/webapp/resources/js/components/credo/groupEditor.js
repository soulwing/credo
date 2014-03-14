$(document).ready(function() {
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
	
	var optionSort = function(a, b) { return a.text > b.text; };
	
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

	if (!$resetButton.data("enabled")) {
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
