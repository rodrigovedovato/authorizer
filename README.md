# About this project

The authorizer is a command line application written in Scala that reads instructions from the standard input and outputs to the standard ouput

## How this repository is organized

| Folder   | Description                                     |
|----------|-------------------------------------------------|
| source  | Application sources                             |
| builder  | Dockerfile used to build the application        |
| Makefile | All the recipes needed to build the application |

The `source` folder is organized using Scala's [default folder structure](https://www.scala-sbt.org/1.x/docs/Directories.html)

## Instructions

Right now, the authorizer only supports twolink instructions: create account, and process transaction, both using the json format:

### Create instruction:

```json
{"account": {"active-card": true, "available-limit": 100}}
```
### Process Transaction

```json
{"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z"}}
```

Time follows the [ISO-8061](https://www.w3.org/TR/NOTE-datetime) format

## Build & Run

In order to build and run the authorizer, you must install:

- Make
- Docker
- Java 11

### Building

The build process is based on Docker, so you don't have to install sbt locally in order to build the source code. To generate the executable, simply run `make build` and it will generate a file called `authorizer.tar`

### Running

Once you generate the `authorizer.tar` file, can place it wherever you want and run `tar -xvf authorizer.tar` to untar the contents. To run the authorizer, simply run `./authorizer/bin/authorizer < transactions`, where `transactions` is the file with the instructions you wish to process

#### Sample files

You can find some sample files under the `source/src/test/resources/` folder. The files that can be used for testing are the ones with the `in` suffix. Example:

```
./authorizer/bin/authorizer < ./source/src/test/resources/no-problem-scenario-in
```