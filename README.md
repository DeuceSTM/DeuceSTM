[![Build Status](https://secure.travis-ci.org/DeuceSTM/DeuceSTM.png)](http://travis-ci.org/DeuceSTM/DeuceSTM)

Forums:  [Users](http://groups.google.com/group/deuce-stm), [Developers](http://groups.google.com/group/deuce-stm-developers)

# Deuce STM

## Introduction

Deuce comes with few benchmarks and unit tests which are not part of the 
downloaded but can be checkout from the SVN. The tests are JUnit3 tests and 
can be run by any tool that can run JUnit tests. All the tests are under 
src/test directory.

Also under the src/test exist few known benchmarks which are 
already adapted to run under Deuce including JSTAMP, STMBENCH7, IntSet 
and Bank. 

### Naming convention

Under the src directory you can find two directories: 
   `/java` --> deuce agent sources.
   `/test` --> deuce unit tests and benchmarks sources.

## Getting started

### Building 

The build is using [Ant](https://ant.apache.org/) script `build.xml`.
To build all is needed to call ant.
```bash
$ ant
```

### Download
http://sites.google.com/site/deucestm/download/deuceAgent-1.3.0.jar

More information can be found here: 

* [Wiki](https://github.com/DeuceSTM/DeuceSTM/wiki/_pages)  
* [Site](http://sites.google.com/site/deucestm/documentation)

### System requirements

   - Operating system: Any-OS
   - Processor: Any-Processor
   - Compiler: JDK6+ 
   - Libs and other inst req.: JDK6+

# Running

Running Deuce can be done in two options:
   * Online e.g. `java -javaagent:bin/deuceAgent.jar -cp my.jar myMain`
   * Offline e.g. `java -jar deuceAgent.jar my.jar out_my.jar`


[Read More](https://github.com/DeuceSTM/DeuceSTM/wiki/_pages)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/DeuceSTM/deucestm/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

