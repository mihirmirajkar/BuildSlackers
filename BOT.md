#### Changes in Project
Our initial thought was to have our bot run on a timer to check for newer versions of dependencies and try to update to them, but we decided that that is not the best decision. Since whenever we find newer versions of a dependency we are still dependent on having the user confirm that he wants the bot to include the latest dependency in his project, we figured the gain of having the bot schedule itself is not worth it. The big appeal of having the bot check automatically, is that the user could just forget about it. However, if the bot scheduled itself, it would reach a point where it is stuck, needing input from the user on whether or not to update. We decided it would make more sense to only check when the user asks the bot to, so that the bot isn't spinning and wasting resources waiting for a user's response when a user might not be available for a couple of hours or even days. 

We also thought that we would implement the Observable pattern in our bot. However, we decided against this, because we decided to use a WebServer to listen for the commands from slack and pass those commands onto the "backend" portion of our bot. Within this architecture, we didn't think the Observable pattern made sense. So, we instead implemented the MVC pattern for our WebServer, using Spring MVC. 
Because we are using a webserver, at this point it is only being hosted locally, so running the selenium tests (or trying to use the bot) from any machine other than our own which is hosting the webserver will fail at this point.

### Mocking
We have several different kinds of mock data - mostly relating to projects and their dependencies. We anticipate our bot keeping track of a user's projects on Github, and checking those for dependency updates. So, we have a mock Github user and mock repos for that user. We also mocked out the dependencies portion of the project. For the dependencies, we used Maven's repository to come up with realistic versions and newer versions for a couple of actual dependencies. We then added this mock data so that the projects will be told that these are their dependencies, rather than using actual maven projects at this point. We did try using actual maven projects with dependencies (very small projects with a list of dependencies we controlled), and this is in TestProjectID, but we decided that that was too close to actual implementation, and so we switched it back to using these objects of dependencies that we create and that are not tied to any project.

### Use Cases

###### Use Case 1 - Check for dependencies and update
```
Use Case: List all newer versions of a dependency
1 Preconditions
   User must have specified the project to check.
   Project must have a pom.xml file
2 Main Flow
   User uses slack to tell Bot to check for updates [S1]. Bot finds which dependencies have updates [S2]. Bot tests each update for each dependency to see if the project can compile and pass unit tests with it [S3]. Bot provides to user list of dependencies and their latest update that passed compilation and unit tests check [S4]. User selects one dependency to update [S5]. Bot updates project to that dependency version [S6]. Bot pushes updated project to Github [S7].
3 Subflows
  [S1] User: '@buildslackersbot check for updates'
  [S2] Bot uses maven to find updateable dependency versions.
  [S3] Bot creates copy of project for each dependency and updates that dependency until it no longer passes compilation and unit tests.
  [S4] Bot lists dependencyGroup:artifactID:currVersion:latestVersion
  [S5] User uses the dependencyGroup and artifactID to uniquely identify a dependency to update.
  [S6] Bot updates the original pom.xml to use the latest "good" version of the dependency the user specified.
  [S7] Bot pushes updated project to Github.
4 Alternative Flows
  [E1] User tells bot to update to a dependency that is not part of the project. 
  [E2] No dependencies can be updated.
```

###### Use Case 2 - List all versions of a dependency which are newer than the current version
```
Use Case: List all newer versions of a dependency
1 Preconditions
   User must have specified the project to check.
   Project must have a pom.xml file
2 Main Flow
   User will ask bot to list newer versions of all dependencies [S1]. Bot provides list of dependencies, their current version, and any newer versions.[S2].
3 Subflows
  [S1] User commands 'list newer dependencies'
  [S2] Bot will return list of dependency names, the current version, and any newer versions
4 Alternative Flows
  [E1] No dependencies have any newer versions. 
```


###### Use Case 3 - Switch which project BuildSlackers is tracking

```
Use Case: Switch Project
1 Precondition
   User must have the access to the project he/se wants to switch to.
2 Main Flow
   User will ask the bot to switch to another project [S1]. Bot will list out all the projects and ask user to select [S2]. Bot will clone the repo to the local directory [S3]
3 Subflows
   [S1] User posts command 'Switch Project'
   [S2] Bot lists all the users projects and asks to select one.
   [S3] Bot will clone the the repo requested by the user in the local drive
4 Alternative Flows
   [E1] No project name matches with the one requested by the user.
   [E2] User doesn't have the access to the repo he/she is requesting to switch to.
```

The following is the screencast of the 3 use cases along with the selenium tests being run and passing
[Screencast](http://www4.ncsu.edu/~dwrice/Videos/CSC510%20Bot.mp4)

#### Travis CI and sauce labs
We also tried to integrate Travis CI and sauce labs. To do this, we moved this project over to public Github. That project is at https://github.com/danwrice/BuildSlackers. We were able to integrate Travis CI, and we integrated sauce labs into our Selenium tests when we run them locally, but we were not able to fully bring together sauce labs for each Travis CI build. Each Travis build will try to run the sauce labs, but we did not set up a tunnel for them, so the sauce labs portion will just return immediately.
