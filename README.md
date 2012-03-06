Jenkins Plugin: Deployinator
==============================

This is a plugin for [Jenkins][http://jenkins-ci.org] Continuous Integration Server.  This plugin provides hooks (CLI) for associating Deployinator stacks to a Jenkins pipeline.

Installation
---------------------------

Install the `modularized-build-pipeline-view` plugin on Jenkins from:
https://github.com/elblinkin/mvn-repo/raw/master/snapshots/org/jvnet/hudson/plugins/modularized-build-pipeline-plugin/1.2-SNAPSHOT/modularized-build-pipeline-plugin-1.2-SNAPSHOT.hpi

Install Maven 2.2.1 or higher

    cd ~/jenkins-deployinator
    mvn package && mvn install
    scp target/deployinator.hpi user@jenkins.server:/opt/jenkins/.jenkins/plugins/.

The last part is to place the `hpi` file into the plugins directory.  You can also do this through the Jenkins interface if you do not have `ssh` access to the Jenkins server.

Contribute
---------------------------

You're interested in contributing to this Etsy-made Jenkins plugin?

Here are the basic steps:

fork `jenkins-deployinator`

1. Clone your fork
2. Hack away
3. If you are adding new functionality, document it in the Wiki or this README
4. If necessary, rebase your commits into logical chunks, without errors
5. Push the branch up to GitHub
6. Send a pull request to the etsy/jenkins-master-project project

We'll do our best to get your changes in!

[jenkins]: http://jenkins-ci.org
[etsy]: http://www.etsy.com
[blog post]: TBD

