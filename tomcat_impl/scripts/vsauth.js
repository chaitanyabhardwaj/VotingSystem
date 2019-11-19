	'use strict';

	function VSAuth() {
		const inst = this;
		this.vsUser = null;
	};

	function VSUser(displayName, username) {
		const inst = this;
		this.displayName = displayName;
		this.username = username;
	}

	VSAuth.prototype.createUser = function(params, callback) {
		var response=null;
		$.post('CreateUser', $.param(params), function(res) {
			response = res;
			console.log(res);
		}).done(function() {
			callback(response);
		});
	};

	VSAuth.prototype.getUser = function(callback) {
		const inst = this;
		if(this.vsUser != null) return this.vsUser;
		$.post('GetUser', $.param({}), function(res) {
			if(res != null && res.result_code) {
				if(res.user != null)
					inst.vsUser = new VSUser(res.user.displayName, res.user.username);
			}
		}).done(function() {
			callback(inst.vsUser);
		});
	};

	VSAuth.prototype.getUserAuth = function(params, callback) {
		const inst = this;
		if(this.vsUser != null) return this.vsUser;
		$.post('GetUserAuth', $.param(params), function(res) {
			if(res != null && res.result_code) {
				if(res.user != null && res.user.username == params.username)
					inst.vsUser = new VSUser(res.user.displayName, res.user.username);
			}
		}).done(function() {
			callback(inst.vsUser);
		});
	};

	VSAuth.prototype.logout = function(callback) {
		const inst = this;
		$.post('LogOut', $.param({}), function(res) {
			if(res != null && res.result_code) {
				inst.vsUser = null;
			}
		}).done(function() {
			callback();
		});
	};
