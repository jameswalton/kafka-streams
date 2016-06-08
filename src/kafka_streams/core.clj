(ns kafka-streams.core
  (:import [org.apache.kafka.streams.kstream KStreamBuilder ValueMapper]
           [org.apache.kafka.streams KafkaStreams StreamsConfig]
           [org.apache.kafka.common.serialization Serdes])
  (:gen-class))

(def props
  {StreamsConfig/APPLICATION_ID_CONFIG,    "my-stream-processing-application"
   StreamsConfig/BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
   StreamsConfig/KEY_SERDE_CLASS_CONFIG,   (.getName (.getClass (Serdes/String)))
   StreamsConfig/VALUE_SERDE_CLASS_CONFIG, (.getName (.getClass (Serdes/String)))})

(def config
  (StreamsConfig. props))

(def builder
  (KStreamBuilder.))

(def input-topic
  (into-array String ["my-input-topic"]))

(->
  (.stream builder input-topic)
  (.mapValues (reify ValueMapper (apply [_ v] ((comp str count) v))))
  (.to "my-output-topic"))

(def streams
  (KafkaStreams. builder config))

(defn -main [& args]
  (prn "starting")
  (.start streams)
  (Thread/sleep (* 60000 10))
  (prn "stopping"))