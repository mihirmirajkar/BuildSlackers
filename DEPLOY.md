###### Acceptance Test Use Case #1 - Change Repo

Description: Use Case #1 allows the user of our bot to specify which of their github repos they want the bot to monitor dependencies for.

The repo name that the user asks the bot to switch to will be used by the bot for future commands, such as listing the dependencies, or updating a dependency.

Our bot expects the maven projects in a repo to be in the top level - that is, pom.xml must be in the root of the repository, and not in a directory. The test repositories we have set up adhere to this requirement.

| Test ID | Description | Expected Results | Actual Results
| -------- | --------------- | ------------ | --------------
| ChangeRepo | ** Preconditions ** <br> <br>  1. The user is logged into the Slack team this bot is a member of, and is on the #general channel. <br> <br> ** Steps ** <br> <br> 1. User types '@bsbot2 change project 2. User types 'TestRepo2' | 1. The bot says (in some order) <br> 'a <br> brepo <br> TestRepo1 <br> NoDependenciesRepo <br> TestRepo2' <br> <br> 2. Bot says 'success' |
