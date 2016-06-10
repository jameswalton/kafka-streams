# kafka-streams

## Quickstart

### Download confluent
```
wget http://packages.confluent.io/archive/3.0/confluent-3.0.0-2.11.zip
unzip confluent-3.0.0-2.11.zip

cd confluent-3.0.0/
```

### Start zookeeper and kafka
```
./bin/zookeeper-server-start ./etc/kafka/zookeeper.properties
./bin/kafka-server-start ./etc/kafka/server.properties
```

### Create input and output topics
```
./bin/kafka-topics --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partitions 1 \
--topic my-input-topic

./bin/kafka-topics --create \
--zookeeper localhost:2181 \
--replication-factor 1 \
--partitions 1 \
--topic my-output-topic
```

### Compile and run the application
```
lein uberjar
java -jar target/kafka_streams.jar
```

### Produce some data
```
./bin/kafka-console-producer \
--broker-list localhost:9092 \
--topic my-input-topic
```

### See the output
```
./bin/kafka-console-consumer \
--zookeeper localhost:2181 \
--topic my-output-topic \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
