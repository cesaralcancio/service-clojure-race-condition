(ns service-clojure-race-condition.components.datomic
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.datomic.dev_config :as d.config]))

(defrecord Datomic []
  component/Lifecycle

  (start [this]
    (let [database (d.config/create-database! d.config/client-local)
          conn (d.config/conn! d.config/client-local)
          _ (d.config/create-schema! conn d.config/transaction-schema)
          datomic {:database database :conn conn}]
      (assoc this :datomic datomic)))

  (stop [this]
    (assoc this :datomic nil)))

(defn new-datomic []
  (->Datomic))
