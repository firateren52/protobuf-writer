#Protobuf based file writer
##Http server:

receives POST requests with JSON object and converts JSON object to protocol buffer formats
There are three proto message types: 

User(id, name)
Item(id, name)
Items: Item list

##Requirements

Java 8 , Apache Maven 3

##Build and Run

Project tested on macOS but should run on other OS without problem.

To build and run the project run ./run.sh. This script:

1) builds the app with maven

2) run test methods with maven

3) deploys the app to tomcat server  with maven

4) sends test post request (with cURL) with curl

##Build

Build steps:

mvn clean package

##Run

Run steps:

mvn spring-boot:run

##Implementation 
These are steps when /users post request called.

1) Spring REST receives JSON object make validations and call the UserWriterService instance.
UserWriterService instance is singleton and there are three different subclasses of MessageWriterService for three different protobuf messages. 

2) MessageWriterService.saveAsync() method add messages to a blocking queue. A single thread reader takes these messages from queue and send to FileService instance. There is one FileService instance per MessageWriterService instance. There is also MessageWriterService.save() method which directly calls FileService. Multiple threads can call this method so locking mechanisms needed.

3) FileService.writeMessage method first checks if rollover required for file or not according to rolling over strategy which is timeout in our case. If rollover required then it will close the file, move it with a different name and recreate a new file with previous name(similiar to log files). Then message.writeDelimitedTo will be called and message will be written to the file. 
For every protobuf message type there is a unique folder and file prefix name. For instance Users.proto messages will be written to data/users/users.data file. 

##Tests

Tests were done with curl. Examples:

curl -d '{"name":"firat eren","id":123343}' -H "Content-Type:application/json" -X POST http://127.0.0.1:8017/users
curl -d '{"name":"item1","id":4332}' -H "Content-Type:application/json" -X POST http://127.0.0.1:8017/items


##Experiments

1) During benchmark tests i found that MessageWriterService.saveAsync is slightly faster than MessageWriterService.save method. I also tried to improve saveAsync method with bulk write(flush) because there is one flush in every writeDelimitedTo method call and i wanted to reduce that. But it wasn't faster so i removed that method. 

2) In tests i write a huge amounts of messages and then read all messages from files and compare them with actual messages one by one.

3) I didn't have time to do "docker compose" part.