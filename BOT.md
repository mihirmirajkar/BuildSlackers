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
