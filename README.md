# Flow
The entire flow consists of two main services that work together to process and manage orders.
1.	cashinvoice-order-service - create order and save the order as a JSON file.
2.	cashinvoice-integration-service - poll the order JSON file and process it further (e.g., send to an external system).


# cashinvoice-integration-service

Service Responsibility

The cashinvoice-integration-service is responsible for:
•	Polling the filesystem for newly created order JSON files
•	Reading and deserializing order data
•	Performing validation and transformation logic
•	Processing orders for integration purposes
•	Sending order data to an external system (or placeholder integration logic)
•	Ensuring reliable and repeatable processing of order files


Core Tech Stack

Java        : 21
Spring Boot : 3.3.5
Build Tool  : Maven
Before running the cashinvoice-integration-service, RabbitMQ must be running and accessible on port 5672.

This service depends on RabbitMQ for message-based communication and integration processing.

For convenience, a Docker Compose configuration is provided with the project to run RabbitMQ in a Docker container.

Running RabbitMQ using Docker Compose
•	Ensure Docker and Docker Compose are installed on the machine
•	Navigate to the directory containing the Docker Compose file
•	Start RabbitMQ by running:

docker-compose up -d



Build and Run Instructions
1.	Build the Project

From the project root directory, run:

mvn clean package

This will generate an executable JAR file in the target directory.
2.	Run the Application

Navigate to the target directory and run:

java -jar cashinvoice-integration-service-0.0.1-SNAPSHOT.jar

The application will start and begin polling for order JSON files.



Input Directory

The integration service monitors the following directory for order files:

input/orders

Each file represents a single order created by the cashinvoice-order-service.
The input/orders directory should be same as the cashinvoice-order-service input directory, which store the order JSON files.


Notes
•	This service is designed to run independently.
•	It follows a file-based integration approach for simplicity and decoupling.
•	The architecture allows easy extension to message queues or event-driven systems in the future.
•	Ensure the input directory exists before starting the application.
•	Logs and processing status can be monitored via the console output.
•	In the application.yml file input directory should be configured as per the cashinvoice-order-service.