const express = require('express');
const app = express();
const PORT = 3000;
const cors = require('cors');

const { Kafka } = require('kafkajs');
const kafka = new Kafka({
    clientId: 'my-app',
    brokers: ['localhost:9092']
});

let consumer = kafka.consumer({ groupId: "test-group" });

app.use(cors({ origin: "http://localhost:3001" }));

app.get('/events', (req, res) => {
    res.setHeader('Content-Type', 'text/event-stream');
    res.setHeader('Cache-Control', 'no-cache');
    res.setHeader('Connection', 'keep-alive');

    runKafkaConsumer(res).catch(error => {
        res.write(`data: Error: ${error.message}\n\n`);
    });
    
    // Clear the interval when the connection is closed
    req.on('close', () => {
        console.log('Connection closed');
        res.end();
    });
})

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
})

async function runKafkaConsumer(res) {
    res.write('date: Waiting for football logs...!\n\n');
    await consumer.connect();
    console.log("Kafka consumer connected");
    await consumer.subscribe({ topic: 'football-output', fromBeginning: false });
    console.log("Kafka consumer subscribed");
    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
        let value = message.value.toString();
        res.write(`data: ${value}\n\n`);
        },
  });
}