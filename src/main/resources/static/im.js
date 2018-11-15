
(function(){

if(window.IM){
	console.error('global variable "im" already exists');
	return;
}
function jsonToQuery(json){
			var ret = "";
			for(var name in json){
				ret = ret + name + '='+ json[name] + "&";
			}
			if(ret != ""){
				ret = ret.substr(0,ret.length - 1);
			}
			return ret;
		}




var CLIENT_ID = Math.floor(Math.random() * new Date().getTime());
var  CALLBAKK_METHOD_NAME_PREFIX = 'IM_Event_'+new Date().getTime() + "_" + Math.ceil(Math.random() * 0xffffffffffff);
var SERVER = "http://localhost:8081/message";




var loopPull = function(options){
	var IS_PING = false;
	var script = null;
	options.clientId = CLIENT_ID;
	options.callback = CALLBAKK_METHOD_NAME_PREFIX;

	function createScriptElement(){
		var script = document.createElement("script");
		script.defer = true;
		script.src = SERVER + "?" + jsonToQuery(options);
		script.onload = function(){IS_PING = false;}
		script.onerror = function(){IS_PING = false;}
		return script;
	}



	function pingMsg(){
		script = createScriptElement();
		document.body.append(script);
		IS_PING = true;
	}


	pingMsg();
	setInterval(function(){
		if(!IS_PING){
			script.remove();
			pingMsg();
		}
	},2000);


}


var IM = function(){
	var thisobj = this;
	this.events = {};
	window[CALLBAKK_METHOD_NAME_PREFIX] = function(msgs){
		for(var i = 0; msgs &&i < msgs.length; i++){
			var msg = JSON.parse(msgs[i]);
			if(msg.type){
				thisobj.events[msg.type](msg);
			}else{
				console.warn("event type not define")
			}
		}
	}
}

IM.prototype.on = function(type,call) {
	this.events[type] = call;
};

IM.prototype.loopPull = function(options){
	loopPull(options);
}


window.IM = IM;
})()
