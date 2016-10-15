
var Botkit = require('botkit');
var express = require('express');
var app = express();

//var childProcess = require("child_process");

var controller = Botkit.slackbot({
  debug: false
  //include "log: false" to disable logging
  //or a "logLevel" integer from 0 to 7 to adjust logging verbosity
});

// connect the bot to a stream of messages
var myBot = controller.spawn({
  //token: "xoxb-75397174165-smfPcGve2r4oeIXmMRYVuCLk",
  token: process.env.ALTCODETOKEN,
  incoming_webhook: {
    url: "https://hooks.slack.com/services/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"
  }
}).startRTM()

controller.setupWebserver(3000, function(err, webserver) {
  controller.createWebhookEndpoints(webserver);
});

// give the bot something to listen for.
//controller.hears('string or regex',['direct_message','direct_mention','mention'],function(bot,message) {
// Listener for change project trigger
controller.hears('change project',['direct_message', 'mention', 'direct_mention'], function(bot,message)
{
  bot.startConversation(message, getNewProjectName);
});

getNewProjectName = function(response, convo){
  convo.ask("What is the name of the project you would like to switch to?", function(response, convo) {
    convo.say("Got it. Switching project.");
    //call function to switch the project, response should contain project name
    //outgoing webhook?
    //To post response to outgoing webhook respond with "text": "my response"
    convo.next(); //Terminate conversation with status == 'completed'
  });
}

//Listener for list dependencies trigger
controller.hears('list dependencies', ['direct_message', 'mention', 'direct_mention'], function(bot,message){
  //Call function to get the dependency list
  //May need outgoing webhook here
});

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







