(ns service-clojure-race-condition.components.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database []
  component/Lifecycle

  (start [this]
    (assoc this :store (atom {})))

  (stop [this]
    (assoc this :store nil)))

(defn new-database []
  (->Database))
