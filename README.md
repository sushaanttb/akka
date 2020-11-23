<h1> A basic File Reader implementation using Akka Actors and Java8 </h1>

Purpose: Helps getting started with the "Actor Model" and modelling our typical programming use cases. 

Approach: 
A new Fileactor per file name :"abc.txt" so if we have 100 files, 100 file actors 

  1. FileActor : Every fileactor will parse the file and create a new child lineActor for all lines to be parsed and "tell" it to parse it. On receiving processing notification, it will "tell" ArchivalActor to process it for archival. 
  2. LineActor The line actor will parse the line as per business logic, send it to CassandraActor. Additionally if it's the last line of file , it will "tell" file Actor that processing is completed. 
  3. CassandraActor: For dumping records in DB. 
  4. ArchivalActor : To archive the file once it's processed.
