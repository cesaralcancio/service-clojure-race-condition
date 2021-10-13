(ns service-clojure-race-condition.datomic.transactions
  (:require [datomic.client.api :as d])
  (:import (java.util Date)))

(defn now
  [] (new Date))

(defn upsert-one!
  "Update or insert one record"
  [{:keys [conn]}
   {:keys [id description amount]}]
  (d/transact conn
              {:tx-data [[:db/add "temporary-new-db-id" :transaction/id id]
                         [:db/add "temporary-new-db-id" :transaction/description description]
                         [:db/add "temporary-new-db-id" :transaction/amount (bigdec amount)]
                         [:db/add "temporary-new-db-id" :transaction/created-at (now)]]}))

(defn find-all!
  [{:keys [conn]}]
  (d/q '[:find ?id ?description ?amount ?created-at
         :keys id description amount created-at
         :where
         [?e :transaction/id ?id]
         [?e :transaction/description ?description]
         [?e :transaction/amount ?amount]
         [?e :transaction/created-at ?created-at]]
       (d/db conn)))

(defn delete!
  [{:keys [conn]} id]
  (try
    (d/transact conn {:tx-data [[:db/retract [:transaction/id id] :transaction/id]
                                [:db/retract [:transaction/id id] :transaction/description]
                                [:db/retract [:transaction/id id] :transaction/amount]
                                [:db/retract [:transaction/id id] :transaction/created-at]]})
    (catch Exception e (do (println e) {}))))
