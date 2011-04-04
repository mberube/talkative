
var TalkativePlugin = function() { 

}

TalkativePlugin.prototype.init = function(successCallback, failureCallback) {

	
    return PhoneGap.exec(successCallback, 
    					failureCallback,
    					'TalkativePlugin',
    					'init', 
    					[]);
};

TalkativePlugin.prototype.speak = function(text, successCallback, failureCallback) {

	
    return PhoneGap.exec(successCallback, 
    					failureCallback,
    					'TalkativePlugin',
    					'speak', 
    					[text]);
};

PhoneGap.addConstructor(function() {
	//Register the javascript plugin with PhoneGap
	PhoneGap.addPlugin('talkative', new TalkativePlugin());
	
	//Register the native class of plugin with PhoneGap
	PluginManager.addService("TalkativePlugin","com.talkative.TalkativePlugin");
});
