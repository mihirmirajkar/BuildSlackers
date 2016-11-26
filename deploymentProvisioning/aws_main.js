var AWS = require('aws-sdk');
var fs = require ('fs');
var AWSconfig = require('./bs.json');
//configure AWS security tokens
var ansibleLocation = AWSconfig.ansibleLocation;
	//console.log("This is sparta! "+ansibleLocation)
AWS.config.update({accessKeyId: AWSconfig.aws_access_key_id,
	secretAccessKey: AWSconfig.aws_secret_access_key});

//set your region
AWS.config.update({region: 'us-east-1'});

var ec2 = new AWS.EC2();

var params = {
	ImageId: 'ami-40d28157',	//ubuntu
	InstanceType: 't2.micro',
	KeyName: 'buildslackers',
	MinCount: 1, MaxCount: 1
};

ec2.runInstances(params,function(err,data) {
	if (err) {
	console.log("Could not create instance", err);
	return;
	}

	//store instance id to get ip address
	var instanceId = data.Instances[0].InstanceId;
	console.log("\nAWS instance created with instanceId:", instanceId);

	var params = {
	InstanceIds: [instanceId]
	};

	console.log("\nWaiting for AWS instance to get started...");
	var sleep = require('sleep');
	sleep.sleep(80);
	console.log("AWS instance is up and running... getting details of AWS instance");

	//getting description
	ec2.describeInstances(params, function(err, data)
	{
		if (err)
		{
		console.log(err,err.stack);
		}
		else
		{
		//write ip address
			var ipAddress = data.Reservations[0].Instances[0].PublicIpAddress;
			console.log("IP address of AWS instance: " +ipAddress);
			var inventoryContent = "node1 ansible_ssh_host=" + ipAddress+ " ansible_ssh_user=ubuntu" + " ansible_ssh_private_key_file=" +ansibleLocation + "/buildslackers.pem\n";
			console.log("\n\nThis is the content: "+inventoryContent);
			fs.appendFile(ansibleLocation + "/inventory", inventoryContent, function(fileErr)
			{
			if(fileErr)
			{
				return console.log("Error writing in inventory file:\n" + fileErr);
			}
			console.log("inventory file update");
			});
		}
	});;
});
			