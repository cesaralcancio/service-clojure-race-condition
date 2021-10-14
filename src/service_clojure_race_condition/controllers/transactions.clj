(ns service-clojure-race-condition.controllers.transactions
  (:require [service-clojure-race-condition.datomic.peer.peer-transactions :as d.transactions]
            [service-clojure-race-condition.logic.transactions :as l.transactions]
            [cheshire.core :as cheshire])
  (:import (java.util UUID)))

(defn response
  [status body]
  {:status  status
   :body    (cheshire/generate-string body)
   :headers {"Content-Type" "application/json"}})

(defn ok [body]
  (response 200 body))

(defn bad-request [body]
  (response 400 body))

(defn find-all!
  [datomic]
  (let [transactions (d.transactions/find-all! datomic)]
    (ok transactions)))

(defn process!
  [datomic trx]
  (let [transaction (assoc trx :id (UUID/randomUUID))
        transactions (d.transactions/find-all! datomic)]
    (if-not (l.transactions/exceeded-limit? transactions)
      (do (d.transactions/upsert-one! datomic transaction)
          (ok transaction))
      (bad-request transaction))))

(defn delete!
  [datomic id]
  (let [id-uuid (UUID/fromString id)
        _ (d.transactions/delete! datomic id-uuid)]
    (ok {:transaction-id id-uuid})))
