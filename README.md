###Build

[![Build Status](https://travis-ci.org/kjlubick/DevOps-Project.svg?branch=master)](https://travis-ci.org/kjlubick/DevOps-Project) [![Coverage Status](https://coveralls.io/repos/kjlubick/DevOps-Project/badge.svg?branch=master)](https://coveralls.io/r/kjlubick/DevOps-Project?branch=master)

Dependencies (and the rest of the build process) are managed by Maven.  To build:   
1. Install Maven.  
2. Clone repository.  Navigate a command prompt to the folder.  
3. Run `mvn clean install`, which will build and install all three projects.

Useful Maven Commands:  
- Run `mvn clean install` from parent directory to do a full build.  
- Run `mvn eclipse:eclipse` to build an Eclipse project for one of the modules.  
- Run `mvn eclipse:eclipse -DdownloadSources=true  -DdownloadJavadocs=true` to build an Eclipse project with javadocs and sources linked.  
- Run `mvn clean compile assembly:single` in one of the module folders to build a jar with dependencies included [source](http://stackoverflow.com/a/574650/1447621)
- Run `mvn test jacoco:report` from parent directory to run tests and generate a jacoco coverage report.
- Run `mvn findbugs:findbugs` from parent directory to generate xml findbugs reports



###Test
The primary component in our project that we are interested in testing is the spreadsheet analyzer. We wrote several unit tests for this component. We supplemented these tests with [Randoop](https://code.google.com/p/randoop/), a constraint-based test generation package, to increase test coverage and generate regression tests for this module. Our original tests were pretty thorough, so the generated tests only [increased our coverage by a little bit](https://coveralls.io/builds/2069537).

Here is a screenshot of the Randoop configuration process.
![randoop](https://cloud.githubusercontent.com/assets/5032534/6547600/16bf44d6-c5b3-11e4-9dcd-33c6679ec35d.PNG)

We used jacoco and coveralls to measure and report on test coverage. The reports are available at this link here: [![Coverage Status](https://coveralls.io/repos/kjlubick/DevOps-Project/badge.svg?branch=master)](https://coveralls.io/r/kjlubick/DevOps-Project?branch=master).

###Analysis
We configured our build process to run FindBugs on our code.  This was easily achieved by using the Findbugs maven plugin and [adding it to our pom](https://github.com/kjlubick/DevOps-Project/commit/45be481f785d0014846acc54d66106d99f542d59). We also created a [custom FindBugs detector](https://github.com/kjlubick/DevOps-CustomFindBugs) that makes warnings if non-trivial methods lack comments - the longer the method, the more severe the bug. 

If any "high" severity bugs with a bug rank of 15 or lower are detected, [the build fails](https://travis-ci.org/kjlubick/DevOps-Project/builds/53095587):
![image](https://cloud.githubusercontent.com/assets/6819944/6562989/ff0adab4-c673-11e4-92fe-30f00d6c6cd5.png)

Additionally, we wrote a [small script](https://github.com/kjlubick/DevOps-Project/blob/b1a8efa3302ea592c5a6c54c564a0306939b6f07/coverageChecker.py) that will [fail the build](https://travis-ci.org/kjlubick/DevOps-Project/builds/53696975) if test coverage isn't high enough, specifically, if any class has more than 100 total uncovered statements, although this is a tunable parameter. 

![image](https://cloud.githubusercontent.com/assets/6819944/6563033/6611180e-c674-11e4-931c-bcda41f33283.png)


##Previous Milestones##

###Build###
**Travis CI**
Initially we configured our build to run on 
[Travis CI](https://travis-ci.org/DeveloperLiberationFront/Spreadsheet-Common-Crawler)

**Jenkins**
Since Travis CI did not meet the project requirements, we created a Jenkins server running on a local vagrant machine.  Here is a screenshot of the web interface:   
![image](https://cloud.githubusercontent.com/assets/6819944/6066907/9461437c-ad3e-11e4-9915-1f8e286b3131.png)

As indicated above, Maven manages our dependencies and the Jenkins server uses Maven to run the build process using. Here is a screenshot of a successful build notification. 

![image](https://cloud.githubusercontent.com/assets/5032534/6063888/789df68c-ad28-11e4-82f3-62788cf0002a.png)

Because we are running this locally, that is, without a public ip address, we could not setup a git hook.  However, we set up a Git poll that simply pings the GitHub repository every 15 minutes (this is adjustable) and makes a build if there are changes

![image](https://cloud.githubusercontent.com/assets/6819944/6066588/2fe9c9d4-ad3c-11e4-9d09-bcf6879ac020.png)


We created a slave vm (also using vagrant) that the master connects to via SSH to setup and execute tasks:  
![image](https://cloud.githubusercontent.com/assets/6819944/6066396/d1dc4d4a-ad3a-11e4-9742-f4be53e9a58a.png)

Here is a screenshot of the log when "slave 3" picked up a build job:
![image](https://cloud.githubusercontent.com/assets/6819944/6066633/8d4c50a6-ad3c-11e4-88dd-c6b029c5cf69.png)

Our Jenkins config file and the config file for our job are found under `jenkins/`.