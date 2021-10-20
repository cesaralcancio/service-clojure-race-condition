(ns service-clojure-race-condition.datomic.transactions-peer-test
  (:require [clojure.test :refer :all]
            [service-clojure-race-condition.datomic.peer.peer-pro-config :as config]
            [service-clojure-race-condition.datomic.peer.peer-transactions :as transactions]
            [service-clojure-race-condition.datomic.common-config :as common])
  (:import (java.util UUID)
           (java.util.concurrent ExecutionException))
  (:use clojure.pprint))

(config/create-database! config/base-uri config/db-name-transactions)
(def conn (config/connect! config/full-uri-transactions))
(config/create-schema! conn transactions/transaction-schema)

(config/list-databases! config/base-uri)

(def transaction
  {:id          (UUID/randomUUID)
   :description "Iphone 7s"
   :amount      1000})

(transactions/upsert-one!
  {:conn conn}
  transaction)

; atomic
(def transaction-atomic
  {:id          (UUID/randomUUID)
   :description "Iphone 7s"
   :amount      500})

(try
  @(transactions/upsert-one-isolated!
     {:conn conn}
     transaction-atomic)
  (catch ExecutionException e
    (println "Error: " e)))

; list
(def transactions
  (transactions/find-all! {:conn conn}))
(pprint transactions)

(transactions/delete!
  {:conn conn}
  (:id transaction))

(doseq [transaction transactions]
  (transactions/delete!
    {:conn conn} (:id transaction)))
