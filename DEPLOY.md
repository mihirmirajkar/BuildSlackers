###### Acceptance Test Use Case #1 - Change Repo

Description: Use Case #1 allows the user of our bot to specify which of their github repos they want the bot to monitor dependencies for.

The repo name that the user asks the bot to switch to will be used by the bot for future commands, such as listing the dependencies, or updating a dependency.

Our bot expects the maven projects in a repo to be in the top level - that is, pom.xml must be in the root of the repository, and not in a directory. The test repositories we have set up adhere to this requirement.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| ChangeRepo | **Preconditions** <br> <br>  1. The user is logged into the Slack team this bot is a member of, and is on the #general channel. <br> <br> **Steps** <br> <br> 1. User types '@bsbot2 change project' <br> <br> 2. User types 'TestRepo2' | 1. The bot says (in some order) <br> 'a <br> brepo <br> TestRepo1 <br> NoDependenciesRepo <br> TestRepo2' <br> What is the name of the project you would like to switch to? <br> <br> 2. Bot says 'success' |

  
###### Acceptance Test Use Case #3 -Update Dependencies

Description: Use Case #3 This allows the users to update the dependency they want by listing out all the dependencies that can be updated and after the user selects the dependency which he/she wants the bot to update then the bot will update and will push the newly update the project to the originalrepo.

Our bot expects that the project has an error free pom.xml file which maven can read and which maven will use to read which dependencies does the project have and which ones can be upated.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| UpdateDependencies | **Preconditions** <br> <br>  1. The project that the user wants to work on should not have errors in pom.xml file. <br> 2. The user has changed the the project he.she wants to work on by sending the command change repo to the bot. <br> <br> **Steps** <br> <br> 1. User types '@bsbot2 update dependency' <br> <br> 2. User types the number of dependency he/she wants to update | 1. The bot says (in some order) <br> Your project can be updated to the following dependencies:<br> 1. junit:junit:3.8.1:4.12<br> 2. io.dropwizard.metrics:metrics-core:3.1.0:3.1.2 <br> Which dependency would you like to update? Please enter the number of the dependency. <br> <br> 2. Bot says 'Updatte Successful' |
