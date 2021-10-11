(ns service-clojure-race-condition.controllers.transaction
  (:require [service-clojure-race-condition.datomic.transactions :as d.transactions]
            [service-clojure-race-condition.logic.transactions :as l.transactions]
            [datomic.client.api :as d]
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

(defn process!
  [datomic trx]
  (let [transaction (assoc trx :id (UUID/randomUUID))
        conn (:conn datomic)
        db (d/db conn)
        transactions (d.transactions/find-all! db)]
    (if-not (l.transactions/exceeded-limit? transactions)
      (do (d.transactions/upsert-one! conn transaction)
          (ok transaction))
      (bad-request transaction))))

(defn find-all!
  [datomic]
  (let [conn (:conn datomic)
        transactions (d.transactions/find-all! (d/db conn))]
    (ok transactions)))
