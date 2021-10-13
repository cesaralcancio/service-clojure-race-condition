(ns service-clojure-race-condition.datomic.transactions-test
  (:require [clojure.test :refer :all]
            [service-clojure-race-condition.datomic.pro-config :as datomic.pro]
            [service-clojure-race-condition.datomic.common-config :as common.config]
            [service-clojure-race-condition.datomic.transactions :as datomic.transactions])
  (:import (java.util UUID))
  (:use clojure.pprint))

(def client (datomic.pro/client! datomic.pro/cfg))
(def conn (datomic.pro/connect! client common.config/db-name-transactions))
(datomic.pro/create-schema! conn common.config/transaction-schema)

(datomic.pro/list-databases! client)

(def transaction
  {:id          (UUID/randomUUID)
   :description "Iphone 7s"
   :amount      1000})

(datomic.transactions/upsert-one!
  {:conn conn}
  transaction)

(def transactions
  (datomic.transactions/find-all! {:conn conn}))
(pprint transactions)

(datomic.transactions/delete!
  {:conn conn} (:id transaction))

(doseq [transaction transactions]
  (datomic.transactions/delete!
    {:conn conn} (:id transaction)))
