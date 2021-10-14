(ns service-clojure-race-condition.components.datomic-peer
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.datomic.common-config :as common.config]
            [service-clojure-race-condition.datomic.peer.peer-pro-config :as datomic.peer]))

(defrecord Datomic []
  component/Lifecycle

  (start [this]
    (let [_ (datomic.peer/create-database! datomic.peer/base-uri datomic.peer/db-name-transactions)
          conn (datomic.peer/connect! datomic.peer/full-uri-transactions)
          _ (datomic.peer/create-schema! conn common.config/transaction-schema)
          datomic {:conn conn}]
      (assoc this :datomic datomic)))

  (stop [this]
    (assoc this :datomic nil)))

(defn new-datomic []
  (->Datomic))
