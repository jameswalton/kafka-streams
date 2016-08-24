(ns kafka-streams.dictionary-words
  (:import [org.apache.kafka.streams.kstream KStreamBuilder KGroupedTable KeyValueMapper ValueMapper]
           [org.apache.kafka.streams KafkaStreams StreamsConfig KeyValue]
           [org.apache.kafka.common.serialization Serdes])
  (:gen-class))

(def props
  {StreamsConfig/APPLICATION_ID_CONFIG "dictionary-words-count"
   StreamsConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"
   StreamsConfig/KEY_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))
   StreamsConfig/VALUE_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))})

; props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

(def config
  (StreamsConfig. props))

(def builder
  (KStreamBuilder.))

(def input-topics
  (into-array String ["dictionary-words-stream"]))

(def input
  (.stream builder (Serdes/String) (Serdes/String) input-topics))

(def word-counts
  (->
    input
    (.flatMapValues (reify ValueMapper (apply [_ value] (prn "value:::" value) [value])))
    (.map (reify KeyValueMapper (apply [_ key word] (prn "key:::" key "value:::" word)
                                  (prn "KeyValue" (KeyValue. word word))
                                  (KeyValue. word word))))
    (.countByKey "Counts")))

(def job
  (KafkaStreams. builder config))

(defn -main [& args]
  (prn "starting...")
  (.start job))

;./bin/kafka-console-consumer \
;--zookeeper localhost:2181 \
;--topic dictionary-words-count-dictionary-words-count-changelog \
;--from-beginning \
;--formatter kafka.tools.DefaultMessageFormatter \
;--property print.key=true \
;--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
;--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

;./bin/kafka-console-consumer --zookeeper localhost:2181 --topic dictionary-words-count-dictionary-words-count-changelog --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer