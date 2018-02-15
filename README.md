# *FoodTruckFinder*

**FoodTruckFinder** is a simple command-line program that prints out a list of food trucks that are currently open in San Francisco.

## Libraries used (included in lib folder)
- [google-gson](https://github.com/google/gson) - A Java serialization/deserialization library to convert Java Objects into JSON and back

## Requirements
* MacOS or Linux OS
* To compile and run you must have the following installed: java version "1.8.0_111"

## To compile and run
1. cd to /FoodTruckfinder directory
2. To compile program and dependencies:
```java
javac -cp ./lib/gson-2.8.1.jar FoodTruckFinder.java FoodTruck.java
```
3. To run the program:
```java
java -cp .:./lib/gson-2.8.1.jar FoodTruckFinder
```
