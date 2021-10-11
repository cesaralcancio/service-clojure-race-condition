(ns service-clojure-race-condition.datomic.transactions
  (:require [datomic.client.api :as d])
  (:import (java.util Date)))

(defn now [] (new Date))

(defn upsert-one!
  "Update or insert one record"
  [conn {:keys [id description amount]}]
  (d/transact conn
              {:tx-data [[:db/add "temporary-new-db-id" :transaction/id id]
                         [:db/add "temporary-new-db-id" :transaction/description description]
                         [:db/add "temporary-new-db-id" :transaction/amount (bigint amount)]
                         [:db/add "temporary-new-db-id" :transaction/created-at (now)]]}))

(defn find-all!
  [db]
  (d/q '[:find ?id ?description ?amount ?created-at
         :keys id description amount created-at
         :where
         [?e :transaction/id ?id]
         [?e :transaction/description ?description]
         [?e :transaction/amount ?amount]
         [?e :transaction/created-at ?created-at]]
       db))
