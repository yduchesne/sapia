/*

An instance of the I18N class is used to store the data obtained from Cocoon
catalogues in JSON format.

*/

var I18N = Class.create({
  initialize: function(){
    this.catalogues = new Hash();
  	this.messages = new Hash();
  },
  load: function(root) {
    var catalogue = root.catalogues.catalogue;
    if(this.isArray(catalogue)){
      for(i = 0; i < catalogue.length; i++){
        return this.fill(catalogue[i]);
      }
    }
    else{
      return this.fill(catalogue);
    }	
  },
  
  fill: function(catalogue){
	var tempMessages;
	if(catalogue.id == undefined){
	  tempMessages = this.messages;
	} 
	else{
      tempMessages = new Hash();
  	  this.catalogues.set(catalogue.id, tempMessages);
	}
    if(this.isArray(catalogue.message)){
	  for(i = 0; i < catalogue.message.length; i++){
        var msg = catalogue.message[i];
        tempMessages.set(msg.key, msg.cdata);
	  }
    }
    else{
      if(catalogue.message.key == key){
        tempMessages.set(catalogue.message.key, catalogue.message.cdata);
      }    
    }
  },
  
  get: function(key, catalogue) {
    var tempMessages = this.catalogues.get(catalogue);
    var msg;
    if(tempMessages != undefined){
	  msg = tempMessages.get(key);
    }
    if(msg == undefined){
	  msg = this.messages.get(key);
	}
	if(msg == undefined){
  	  return "UNTRANSLATED";        
	}
	return msg;
  },
  
  isArray: function(obj) {
    if (obj.constructor.toString().indexOf("Array") == -1){
      return false;
    }
    else{
      return true
    }
  }
});

