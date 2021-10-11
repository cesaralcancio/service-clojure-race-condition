(ns service-clojure-race-condition.controllers.transaction
  (:require [service-clojure-race-condition.datomic.transactions :as d.transactions]
            [service-clojure-race-condition.logic.transactions :as l.transactions]
            [datomic.client.api :as d])
  (:import (java.util UUID)))

(defn process-transaction
  [datomic trx]
  (let [transaction (assoc trx :id (UUID/randomUUID))
        conn (:conn datomic)
        db (d/db conn)
        transactions (d.transactions/find-all! db)]
    (if-not (l.transactions/exceeded-limit? transactions)
      (do (d.transactions/upsert-one! conn transaction)
          {:status 200})
      {:status 400})))
