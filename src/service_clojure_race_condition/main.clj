(ns service-clojure-race-condition.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.system :as system]))

(defn -main [& args]
  (println "Welcome to my project! These are your args:" args)
  (component/start (system/local-environment)))
