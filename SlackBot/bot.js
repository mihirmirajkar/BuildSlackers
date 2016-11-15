var Botkit = require('botkit');
var express = require('express');
var app = express();

var request = require('request');
var fs = require("fs");
var Promise = require('bluebird');
var parse = require('parse-link-header');

var urlRoot = "http://localhost:8080/SpringMVC/decisioncontroller/executecommand/";
//var childProcess = require("child_process");

var controller = Botkit.slackbot({
  debug: false,
  //logLevel: 7
  //include "log: false" to disable logging
  //or a "logLevel" integer from 0 to 7 to adjust logging verbosity
});

// connect the bot to a stream of messages
var myBot = controller.spawn({
  token: process.env.ALTCODETOKEN,
  incoming_webhook: {
	url:  "https://hooks.slack.com/services/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"
  }
}).startRTM()

controller.setupWebserver(3000, function(err, webserver) {
  controller.createWebhookEndpoints(webserver);
});

// give the bot something to listen for.
//controller.hears('string or regex',['direct_message','direct_mention','mention'],function(bot,message) {
// Listener for change project trigger
controller.hears('(change|switch|different)(.*)(repo|project)',['direct_message', 'mention', 'direct_mention'], function(bot,message)
{
  bot.startConversation(message, getNewProjectName);
});

getNewProjectName = function(response, convo){
	// Making call to the REST Service
	var reply = "";
	var command = "ls";
	var options = getOption(command);
	// Send a http request to url and specify a callback that will be called upon its return.
	request(options, function (error, response, body) 
	{
		//console.log(body);
		var obj = JSON.parse(response.body);
		
		convo.say(obj.responseMessage);
		convo.ask("What is the name of the project you would like to switch to?", function(response, convo) {
			command = "cp_" + response.text;
			options = getOption(command);
            
			request(options, function (error, response, body) 
			{
				var obj = JSON.parse(response.body);
				convo.say(obj.responseMessage);
                convo.next();
			});
			
	  });
	});
}
function getOption(command){
	var updatedOptions = {
		url: urlRoot + command,
		method: 'GET',
		headers: {
			"User-Agent": "EnableIssues",
			"content-type": "application/json"
		}
	};
	return updatedOptions;
}
function getListProject(myCommand)
{

	var reply = "";
	var options = {
		url: urlRoot + "ls",
		method: 'GET',
		headers: {
			"User-Agent": "EnableIssues",
			"content-type": "application/json"
		}
	};

	// Send a http request to url and specify a callback that will be called upon its return.
	request(options, function (error, response, body) 
	{
		var obj = JSON.parse(response.body);
		reply = obj.responseMessage;
	});
	
	return reply;

}

//Listener for list dependencies trigger
//controller.hears('list dependencies', ['direct_message', 'mention', 'direct_mention'], function(bot,message){
  //Call function to get the dependency list
  //May need outgoing webhook here
//});

//NOTE: Incoming webhook code does not go here
//Need incoming webhook for getting 'list dependencies' list
//Need incoming webhook for result of 'change project' action

//Incoming webhook url: https://hooks.slack.com/services/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
// JSON payloads should be sent to that url
// TO USE: Send data to above webhook url with JSON string as body of a POST request

//Outgoing webhook token: Ls6YiyqOybS3RyPjQR4lCGJX
// Use token to verify request came from our slack team

// /change command token: CL7QzsCATFeKMoiJcoeoRWkk

//NOTE: OUR APPLICATION WILL NEED TO BE HOSTED AT A PUBLIC IP ADDRESS OR DOMAIN NAME FOR WEBHOOKS TO WORK
//NOTE: MAY NEED MULTIPLE OUTGOING WEBHOOKS
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
controller.hears('(list|show|what|which|how)(.*)(dependenc|jar|link)',['direct_message', 'mention', 'direct_mention'], function(bot,message)
{
  bot.startConversation(message, listDependency);
});

listDependency = function(response, convo){
    //convo.say("The following are the dependencies for the current project.");
    //convo.say("They are displayed as follows: ");
    //convo.say("dependencyGroup:artifactID:currentVersion:newerVersions");
	// Making call to the REST Service
	var reply = "";
	var command = "ld";
	var options = getOption(command);
	// Send a http request to url and specify a callback that will be called upon its return.
	request(options, function (error, response, body) 
	{
		var obj = JSON.parse(response.body);
		console.log(obj.responseMessage);
		convo.say(obj.responseMessage);
        convo.next();
		
	});
}



controller.hears('(Check(.*)updat)|(updat(.*)dep)|(updat)',['direct_message', 'mention', 'direct_mention'], function (bot, message)
{
    bot.startConversation(message, updateDependency);
});

updateDependency = function(response, convo){
	// Making call to the REST Service
	var reply = "";
	var command = "up";
	var options = getOption(command);
	//command = "ls";
	// Send a http request to url and specify a callback that will be called upon its return.
	request(options, function (error, response, body) 
	{
		//console.log(body);
		var obj = JSON.parse(response.body);
		
		convo.say(obj.responseMessage);
        if (obj.responseMessage !== "This bot has not yet been configured to monitor a project." && 
        obj.responseMessage !== "No updates are available for any dependencies.") {
            //insert check here for if updates available or not
		convo.ask("Which dependency would you like to update? " + 
                        "Please enter the number of the dependency.", function(response, convo) {
			command = "up_"+response.text;
			options = getOption(command);
			request(options, function (error, response, body) 
			{
				var obj = JSON.parse(response.body);
				convo.say(obj.responseMessage);
                convo.next();
			});
                        });
        }
        //insert check here for if updates available or not
		/*convo.ask("Which dependency would you like to update? " + 
                        "Please enter the number of the dependency.", function(response, convo) {
			command = "up_"+response.text;
			options = getOption(command);
			request(options, function (error, response, body) 
			{
				var obj = JSON.parse(response.body);
				convo.say(obj.responseMessage);
                convo.next();
			});

	  });*/
	});
}
//$$$$$$$$$$$$$$$$$$$$$$
//setInterval(invokeUpdate, 100000);		//change the timer here
//invokeUpdate();