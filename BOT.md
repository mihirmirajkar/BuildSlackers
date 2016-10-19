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
![Screencast](http://www4.ncsu.edu/~dwrice/Videos/CSC510%20Bot.mp4)
