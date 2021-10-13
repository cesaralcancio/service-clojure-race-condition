(ns service-clojure-race-condition.components.datomic-pro
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.datomic.common-config :as common.config]
            [service-clojure-race-condition.datomic.pro-config :as datomic.pro]))

(defrecord Datomic []
  component/Lifecycle

  (start [this]
    (let [client (datomic.pro/client! datomic.pro/cfg)
          conn (datomic.pro/connect! client common.config/db-name-transactions)
          _ (datomic.pro/create-schema! conn common.config/transaction-schema)
          datomic {:conn conn}]
      (assoc this :datomic datomic)))

  (stop [this]
    (assoc this :datomic nil)))

(defn new-datomic []
  (->Datomic))
