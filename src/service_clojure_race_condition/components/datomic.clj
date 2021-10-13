(ns service-clojure-race-condition.components.datomic
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.datomic.common-config :as common.config]
            [service-clojure-race-condition.datomic.dev-local-config :as datomic.dev-local]))

(defrecord Datomic []
  component/Lifecycle

  (start [this]
    (let [client (datomic.dev-local/client! datomic.dev-local/cfg)
          _ (datomic.dev-local/create-database! client common.config/db-name-transactions)
          conn (datomic.dev-local/connect! client common.config/db-name-transactions)
          _ (datomic.dev-local/create-schema! conn common.config/transaction-schema)
          datomic {:conn conn}]
      (assoc this :datomic datomic)))

  (stop [this]
    (assoc this :datomic nil)))

(defn new-datomic []
  (->Datomic))
