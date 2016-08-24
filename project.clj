(defproject kafka-streams "0.1.0-SNAPSHOT"
  :description "WIP"
  :url "https://www.github.com/jameswalton/kafka-streams"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :main kafka-streams.core

  :uberjar-name "kafka_streams.jar"

  :repositories {"confluent" {:url "http://packages.confluent.io/maven/"}}

  :dependencies [[cheshire "5.6.3"]
                 [org.apache.kafka/kafka-streams "0.10.0.0-cp1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]])
