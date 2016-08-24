(ns kafka-streams.page-views
  (:require [cheshire.core :refer [generate-string parse-string]]
            [clojure.tools.logging :as log])
  (:import [org.apache.kafka.clients.consumer ConsumerConfig]
           [org.apache.kafka.streams.kstream KStreamBuilder ValueJoiner ValueMapper]
           [org.apache.kafka.streams KafkaStreams StreamsConfig]
           [org.apache.kafka.common.serialization Serdes])
  (:gen-class))

(def props
  {StreamsConfig/APPLICATION_ID_CONFIG    "page-views"
   StreamsConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"
   StreamsConfig/KEY_SERDE_CLASS_CONFIG   (.getName (.getClass (Serdes/String)))
   StreamsConfig/VALUE_SERDE_CLASS_CONFIG (.getName (.getClass (Serdes/String)))
   ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "earliest"})

(def config
  (StreamsConfig. props))

(def builder
  (KStreamBuilder.))

(def page-views-topic
  (into-array String ["page-views"]))

(def users-topic
  (into-array String ["users"]))

(def users-table
  (.table builder (Serdes/String) (Serdes/String) "users"))

(->
  (.stream builder (Serdes/String) (Serdes/String) page-views-topic)
  (.leftJoin users-table (reify ValueJoiner
                           (apply [this page-view user]
                             (let [page-view (parse-string page-view)
                                   user (parse-string user)
                                   join {:user_id (get page-view "user_id")
                                         :name (get user "name")
                                         :age (get user "age")
                                         :page (get page-view "page")}]
                               (log/info "joining page view" page-view "with user" user)
                               (generate-string join)))))
  (.to (Serdes/String) (Serdes/String) "page-views-with-user"))

(def job
  (KafkaStreams. builder config))

(defn -main [& args]
  (log/info "starting...")
  ;(.cleanUp job)
  (.start job))



(comment
  ;./bin/kafka-topics --create --topic page-views --zookeeper localhost:2181 --replication-factor 1 --partitions 1
  ;./bin/kafka-topics --create --topic users --zookeeper localhost:2181 --replication-factor 1 --partitions 1
  ;./bin/kafka-topics --create --topic page-views-with-user --zookeeper localhost:2181 --replication-factor 1 --partitions 1
  ;
  ;./bin/kafka-console-producer --broker-list localhost:9092 --topic users --property parse.key=true --property key.separator="--"
  ;1--{"name": "Bob", "age": 88}
  ;2--{"name": "Fred", "age": 77}
  ;;
  ;./bin/kafka-console-producer --broker-list localhost:9092 --topic page-views --property parse.key=true --property key.separator="--"
  ;1--{"user_id": 1, "page": "home"}
  ;1--{"user_id": 1, "page": "about"}
  ;2--{"user_id": 2, "page": "home"}
  ;2--{"user_id": 2, "page": "contact"}
  ;2--{"user_id": 2, "page": "jobs"}
  )