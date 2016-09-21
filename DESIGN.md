### Problem Statement

The cost of fixing issues has been shown to be proportional to how quickly the issue is discovered. Issues that are discovered late in the software development lifecycle have a higher time cost to fix than issues that are discovered earlier. Continuous integration attempts to discover issues as early as possible, by constantly building the software and running any automated tests. However, continuous integration can be a complicated process, and requires human effort to check on the state of the build and respond to any issues discovered. If a programmer makes a mistake and opens a pull request that does break the build, it can require a lot of needless effort to determine why the build broke, and the longer we go without fixing the issue the harder it will be to locate the problem later.


### Bot Description

Our bot, BuildSlackers, simplifies the continuous integration process to help developers deliver better, more reliable software. BuildSlackers can be triggered either through a message in slack, or a Pull Request on Github. BuildSlackers notifies users when a build has started, and when a build ends. If a build has ended successfully, it prompts the user if it wants to merge the Pull Request, if it was triggered by one. If a build fails, it prompts the user to enter an issue in Github in order to track the reason for the failed build. The bot interacts with users through slack. BuildSlackers falls into the category of a DevOps bot.

A bot is a good solution for this problem because it can reduce the complexity of configuring a build for developers. BuildSlackers will hide much of the complexity of configuring a continuous integration environment from developers. BuildSlackers will automatically alert the developers if a build breaks, so developers do not need to monitor the status of a build, but can spend their time doing other things. BuildSlackers also allows information about the builds to be seamlessly integrated into their conversations on slack, so the developers can quickly find the status of a build without needing to be at the server that performs the continuous integration. 
  

### Architecture Design

##### Additional Patterns

