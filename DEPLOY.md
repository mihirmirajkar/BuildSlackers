###### Acceptance Test Use Case #1 - Change Repo

Description: Use Case #1 allows the user of our bot to specify which of their github repos they want the bot to monitor dependencies for.

The repo name that the user asks the bot to switch to will be used by the bot for future commands, such as listing the dependencies, or updating a dependency.

Our bot expects the maven projects in a repo to be in the top level - that is, pom.xml must be in the root of the repository, and not in a directory. The test repositories we have set up adhere to this requirement.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| ChangeRepo | **Preconditions** <br> <br>  1. The user is logged into the Slack team this bot is a member of, and is on the #general channel. <br> <br> **Steps** <br> <br> 1. User types '@bsbot3 change project' <br> <br> 2. User types 'TestRepo2' | 1. The bot says (in some order) <br> 'a <br> brepo <br> TestRepo1 <br> NoDependenciesRepo <br> TestRepo3 <br> TestRepo2 <br> SomeJavaProject' <br> What is the name of the project you would like to switch to? <br> <br> 2. Bot says 'success' |


###### Acceptance Test Use Case #2 -List Dependencies

Description: Use Case #2 This usecase tells the user what all dependencies can be updated and also shows what all newer versions are available or the dependency.

Our bot expects the maven projects in a repo to be in the top level - that is, pom.xml must be in the root of the repository, and not in a directory. The test repositories we have set up adhere to this requirement.

NOTE: There is a large delay in the commands to list dependencies as the server takes a long time to execute the maven commands.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| UpdateDependencies | **Preconditions** <br> <br>  1. The project that the user wants to work on should not have errors in pom.xml file. <br> 2. The user has changed the the project he.she wants to work on by sending the command 'change repo' to the bot. <br> <br> **Steps** <br> <br> 1. User types '@bsbot2 list dependencies' | 1. The bot says (in some order) <br>The following dependencies can be updated to the versions listed:<br>junit:junit:3.8.1:[3.8.2, 4.0, 4.1, 4.2, 4.3, 4.3.1, 4.4, 4.5, 4.6, 4.7, 4.8, 4.8.1, 4.8.2, 4.9, 4.10, 4.11-beta-1, 4.11, 4.12-beta-1, 4.12-beta-2, 4.12-beta-3, 4.12]<br> io.dropwizard.metrics:metrics-core:3.1.0:[3.1.1, 3.1.2]<br>org.slf4j:slf4j-api:1.7.7:[]<br>com.beust:jcommander:1.58:[] |
  

###### Acceptance Test Use Case #3 -Update Dependencies

Description: Use Case #3 This allows the users to update the dependency they want by listing out all the dependencies that can be updated and after the user selects the dependency which he/she wants the bot to update then the bot will update and will push the newly update the project to the originalrepo.

Our bot expects that the project has an error free pom.xml file which maven can read and which maven will use to read which dependencies does the project have and which ones can be upated.

NOTE: There is a large delay in the commands to update dependencies as the server takes a long time to execute the maven commands.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| UpdateDependencies | **Preconditions** <br> <br>  1. The project that the user wants to work on should not have errors in pom.xml file. <br> 2. The user has changed the the project he.she wants to work on by sending the command change repo to the bot. <br> <br> **Steps** <br> <br> 1. User types '@bsbot2 update dependency' <br> <br> 2. User types the number of dependency he/she wants to update | 1. The bot says (in some order) <br> Your project can be updated to the following dependencies:<br> 1. junit:junit:3.8.1:4.12<br> 2. io.dropwizard.metrics:metrics-core:3.1.0:3.1.2 <br> Which dependency would you like to update? Please enter the number of the dependency. <br> <br> 2. Bot says 'Update Successful' |

Link for the Demo:
You can watch the demo for this project at
https://youtu.be/aaTTc-D_PJ8
