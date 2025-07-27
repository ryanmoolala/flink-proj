Got bored this summer and decided to create this project, which is a real-time data processing and analysis program that ingests football game events, processes them using stream processing, and displays the results on a dynamic dashboard. 

## Tech Stack

- **Apache Kafka:**  
  Used as the messaging backbone. Kafka receives input messages (e.g., football events) and publishes them to topics. Kafka is run in Docker for ease of deployment.

- **Apache Flink:**  
  Utilized for stream data processing. A Flink job (e.g., in Main.java) analyzes and processes incoming data streams from Kafka, performing tasks such as aggregations and transformations in real time.

- **Express & Node.js:**  
  An Express server is set up to connect to Apache Kafka via KafkaJS. The server creates a Server-Sent Events (SSE) endpoint that pushes processed results (e.g., scoreboard updates, timeline events) to connected frontend clients.

- **Next.js & React:**  
  The frontend is built with Next.js and React. It uses components like Scoreboard and Timeline to display live data from the Express SSE endpoint. The UI dynamically updates as new events arrive.

## How It Works

1. **Data Ingestion & Messaging (Kafka):**  
   - Football event data is produced (for example, using the Kafka Console Producer) and sent to topics like `football-input`.
   - Kafka distributes these messages to consumers.

2. **Stream Processing (Flink):**  
   - A dedicated Flink job consumes data from Kafka, processes the events (e.g., calculating current scores or analyzing game highlights), and produces processed outputs to another topic (e.g., `football-output`).

3. **Real-Time Data Delivery (Express + SSE):**  
   - An Express server uses KafkaJS to consume messages from the topic.  
   - The server exposes an `/events` endpoint that uses Server-Sent Events (SSE) to push processed data to the frontend as soon as messages arrive.

4. **Dynamic Dashboard (Next.js/React):**  
   - The frontend sets up an EventSource connection to the Express SSE endpoint (`http://localhost:3000/events`).  
   - Components like Scoreboard and Timeline receive and render the real-time data, updating the display as new events are processed.


## Data source
For demonstration purposes, this project uses shot log data sourced from [FBref](https://fbref.com/en/matches/e307ecc0/Spain-England-July-14-2024-UEFA-Euro-2024).



# 🐳 Apache Kafka Quickstart

## ⚙️ 5. Kafka CLI Commands

Kafka command-line tools are located in the `./bin` directory.

### 🔹 List Topics

```bash
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 🔹 Create a Topic

```bash
./bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create \
  --topic football-input \
  --partitions 1 \
  --replication-factor 1
```

```bash
./bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create \
  --topic football-output \
  --partitions 1 \
  --replication-factor 1
```

### 🔹 Start a Producer

```bash
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic football-input
```

Type your messages and press Enter to send them to the topic.

### 🔹 Start a Consumer

Consume messages from the beginning of the topic:

```bash
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic football-output \
```

-verbose:class
