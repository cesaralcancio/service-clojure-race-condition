(ns service-clojure-race-condition.datomic.peer.peer-transactions
  (:require [datomic.api :as d]
            [service-clojure-race-condition.datomic.common-config :as common])
  (:import (java.util Date)))

(def transaction-schema
  (conj
    common/transaction-schema
    {:db/ident :add-transaction
     :db/fn    #db/fn {:lang   :clojure
                       :params [db transaction]
                       :code   (let [all (d/pull db '[*] "transactions")]
                                 (if (> (:amount transaction) 0)
                                   [{:transaction/id          (:id transaction)
                                     :transaction/description (:description transaction)
                                     :transaction/amount      (:amount transaction)
                                     :transaction/created-at  (:created-at transaction)}]
                                   (datomic.api/cancel {:cognitect.anomalies/category :cognitect.anomalies/incorrect
                                                        :cognitect.anomalies/message  "Amount should be higher than 0"})))}
     :db/doc   "Transaction ID"}))

(defn upsert-one-atomic!
  [{:keys [conn]} transaction]
  (let [t (-> transaction
              (assoc :amount (bigdec (:amount transaction)))
              (assoc :created-at (now)))]
    (d/transact conn [[:add-transaction t]])))

(defn now
  [] (new Date))

(defn upsert-one!
  "Update or insert one record"
  [{:keys [conn]}
   {:keys [id description amount]}]
  (d/transact conn
              [[:db/add "temporary-new-db-id" :transaction/id id]
               [:db/add "temporary-new-db-id" :transaction/description description]
               [:db/add "temporary-new-db-id" :transaction/amount (bigdec amount)]
               [:db/add "temporary-new-db-id" :transaction/created-at (now)]]))

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
  (d/transact conn [[:db/retract [:transaction/id id] :transaction/id]
                    [:db/retract [:transaction/id id] :transaction/description]
                    [:db/retract [:transaction/id id] :transaction/amount]
                    [:db/retract [:transaction/id id] :transaction/created-at]]))
