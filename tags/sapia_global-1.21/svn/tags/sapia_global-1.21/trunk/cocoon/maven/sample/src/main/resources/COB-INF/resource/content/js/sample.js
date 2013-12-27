function evalTemplate(link, templateName, id, completionCallback){
	new Ajax.Request(link,
	  {
	    method:'get',
	    onSuccess: function(transport){
	    	var response = transport.responseText || "no response text";
	   		$("response").update(response);
	        var result = TrimPath.processDOMTemplate(templateName, response.evalJSON());
    		$(id).update(result);
    		completionCallback();
	    },
	    onFailure: function(){alert("Error occurred"); }
	  });    

}

function outputJSON(link, responseElementId){
	new Ajax.Request(link,
	  {
	    method:'get',
	    onSuccess: function(transport){
	    	var response = transport.responseText || "no response text";
	   		$(responseElementId).update(response);
	    },
	    onFailure: function(){alert("Error occurred"); }
	  });    
}

function evalJSON(link, responseElementId, callBack){
	new Ajax.Request(link,
	  {
	    method:'get',
	    onSuccess: function(transport){
	    	var response = transport.responseText || "no response text";
	   		$(responseElementId).update(response);
	   		var json = response.evalJSON();
	   		callBack(json);
	    },
	    onFailure: function(){alert("Error occurred"); }
	  });    
}

