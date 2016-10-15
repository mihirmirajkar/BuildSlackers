### Use Cases

###### Use Case 1 - Check for dependencies and update


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
