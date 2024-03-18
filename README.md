# SD-Project: Cloud Computing Service with Faas

## Table of Contents
1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Dependencies](#dependencies)
4. [Cloning the Repository](#cloning-the-repository)
5. [Compiling and Running](#compiling-and-running)
6. [Group's Report](#groups-report)
7. [Conclusion](#conclusion)
8. [Developed by](#developed-by)

## Overview

The project for the subject Sistemas Distribuídos (Distributed Systems) consists in the implementation of cloud computing services with Function-as-a-Service (FaaS) functionality.
With this project we aim to design and develop a cloud computing service capable of executing user-defined functions in a distributed environment. The core functionality revolves around enabling clients to submit task code for execution on remote servers, with an emphasis on efficient resource utilization and scalability.

## Key Features

- `Function-as-a-Service (FaaS) Capability:` The system allows users to submit task code to be executed as functions on remote servers, abstracting away infrastructure management and enabling rapid development and deployment of applications.

- `User Authentication and Registration:` Users are required to authenticate themselves before interacting with the service, ensuring security and access control.

- `Task Execution:` Clients can submit tasks, and the system executes these tasks on available servers, returning results or error messages upon completion.

- `Status Monitoring:` Users can query the current status of the service, including available memory and the number of pending tasks in the queue, providing insights into system performance and utilization.

- `Advanced Task Submission:` Clients can submit multiple task requests without waiting for previous responses, improving system responsiveness and user experience.

- `Task Execution Order:` The system guarantees a fair and efficient order of task execution, preventing tasks from waiting indefinitely in the queue.

- `Distributed Implementation:` The system is designed to be distributed, with a central server managing task queues and multiple worker servers responsible for task execution. This architecture enhances scalability and fault tolerance.

- `Communication Protocol:` The system utilizes a custom binary communication protocol over TCP sockets to ensure efficient and reliable communication between client and server components.

- `Client Library and User Interface:` The system provides a user interface for interacting with the service, enabling seamless integration into client applications and facilitating testing and demonstration.
  
## Dependencies

If you're using a Java-friendly IDE like IntelliJ IDEA, there are no external dependencies to worry about. However, if you're working with a different IDE or manually compiling the project, you may need to resolve dependencies manually.

## Compiling and Running

To ensure the program functions as intended, follow these steps:

1. Navigate to the `TrabalhoG27` folder.

2. Set up the main server:
   - Compile and run the `Server.java` file.

3. For each client:
   - Compile and run the `Client.java` file.

4. For each worker server:
   - Compile and run the `WorkerServer.java` file.

## Developed by
Rodrigo Viana Ramos Casal Novo (A100534)

José Afonso Lopes Correia (A100610)

Mariana Antunes Silva (A100702)

João Paulo Campelo Gomes (A100747)
