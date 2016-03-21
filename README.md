LabManager
==========

[![Build Status](https://travis-ci.org/aaruff/LabManager.svg?branch=master)](https://travis-ci.org/aaruff/LabManager)

LabManager is a tool for remotely executing and monitor program on several computers, from a single location. LabManager
consists of two programs: The controller program, which sends application start/stop requests to one or more computers,
and monitors and displays their state. The executor program simply carries out application requests on behalf of the controller.

## Requirements
Operating System: Windows (XP - 10)
Java Version: 1.8

## Installation
The LabManager tool requires two separate installation steps, one for the controller and the executor.
The control program, is the program which sends application start/stop requests, and monitors its state. The execution program
is installed on one or more computers, and executes applications on the controllers behalf.

### Controller Installation
The controller consists of an executable `LabManager-Controller.exe`, and three configuration files:
* app-config.yaml
* lab-layout.yaml
* log4j.properties

1. Copy the `server` folder containing all of the above files into a directory of your choice (e.g. `C:\LabManager\controller`).
2. Edit the `app-config.yaml` file and declare all of the applications you would like to run on the executors. For example:
```
example
```
3. Edit the `lab-layout.yaml` file and declare all of the computers running the executor program. For example:
```
example
```
4. Edit the `log4j.properties` file. For example:
```
example
```
For more examples see [this tutorial](http://www.mkyong.com/logging/log4j-log4j-properties-examples/).


### Executor Installation
The controller consists of an executable `LabManager-Executor.exe`, and two configuration files:
* config.properties
* log4j.properties

1. Copy the `client` folder containing all of the above files into a directory of your choice (e.g. `C:\LabManager\executor`).
2. Edit the `config.properties` file and specify the controller computer's IP address, it's port, and the executor
(local computer's) computer name.
```
example
```
4. Edit the `log4j.properties` file. For example:
```
example
```
For more examples see [this tutorial](http://www.mkyong.com/logging/log4j-log4j-properties-examples/).
