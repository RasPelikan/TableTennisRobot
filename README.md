# TableTennisRobot
A robot for table tennis training based on RaspberryPi

### The Ball Tracking Daemon

It uses the RaspberryPi's camera to track the ball. It does some optimizing and offers the results as a stream which is consumed by the Robot Daemon.

### The Robot Daemon
It controls the robot and implements the main functionality like

- Interaction with the user (Buttons, Display)
- Controlling the ball launcher
- Running the training profiles

#### Build
```sh
$ cd RobotDaemon
$ mvn package
```

#### Run
```sh
$ java -jar RobotDaemon/target/ttr-0.1-SNAPSHOT-main.jar ttr.properties 
```

