(ns service-clojure-race-condition.datomic.transactions-test
  (:require [clojure.test :refer :all]
            [service-clojure-race-condition.datomic.pro-config :as datomic.pro]
            [service-clojure-race-condition.datomic.common-config :as common.config])
  (:import (java.util UUID))
  (:use clojure.pprint))

;(def client (datomic.pro/client! datomic.pro/cfg))
;(def conn (datomic.pro/connect! client common.config/db-name-transactions))
;(datomic.pro/create-schema! conn common.config/transaction-schema)
;
;(datomic.pro/list-databases! client)
;
;(def transaction
;  {:id          (UUID/randomUUID)
;   :description "Iphone 7s"
;   :amount      1000})
;
;(upsert-one!
;  {:conn conn}
;  transaction)
;
;(def transactions (find-all! {:conn conn}))
;(pprint transactions)
;
;(delete! {:conn conn} (:id transaction))
;
;(doseq [transaction transactions]
;  (delete! {:conn conn} (:id transaction)))
