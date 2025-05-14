# 🐳 Apache Kafka Docker Quickstart

This guide walks you through running **Apache Kafka 4.0.0** in Docker and executing basic Kafka CLI operations such as creating topics, producing, and consuming messages.

## 📦 1. Pull Kafka Docker Image

```bash
docker pull apache/kafka:4.0.0
```

## 🚀 2. Run Kafka Container

```bash
docker run -p 9092:9092 apache/kafka:4.0.0
```

This runs Kafka and maps container port 9092 to your local machine.

## 🔍 3. Check Container Status

```bash
docker ps
```

Find the container ID or name from the output.

## 🖥️ 4. Access the Kafka Container

```bash
docker exec -it <container_id_or_name> /bin/bash
```

Then navigate to the Kafka installation directory:

```bash
cd /opt/kafka/
```

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
  --topic my-topic \
  --partitions 1 \
  --replication-factor 1
```

### 🔹 Start a Producer

```bash
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-topic
```

Type your messages and press Enter to send them to the topic.

### 🔹 Start a Consumer

Consume messages from the beginning of the topic:

```bash
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic my-topic \
  --from-beginning
```
