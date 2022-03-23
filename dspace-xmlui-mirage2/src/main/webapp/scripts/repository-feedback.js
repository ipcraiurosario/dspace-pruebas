var xhr = new XMLHttpRequest();
var path = '/submissions';
$.ajax({
	url: path,
	xhr: function() {
		return xhr;
	},
	success: function (response) {
		if(xhr.responseURL == window.location.origin + path) {
			var a = $(response).find('#user-dropdown-toggle');
			var name = a['0']['innerText'];
			var input = $(response).find('input[name="user"]');
			var email = input['0']['value'];
			var uri = window.location.href;
			if(email != '') {
				$.ajax({
					method: 'POST',
			        	url: 'https://crai-app.urosario.edu.co/repository-feedback/repository-feedback.php',
					data: { 'rfname': name, 'rfemail': email, 'rfuri': uri},
				        success: function (response) {
						$( '.item-summary-view-metadata' ).after(response);
					}
				});
			}
		}
	}
});
