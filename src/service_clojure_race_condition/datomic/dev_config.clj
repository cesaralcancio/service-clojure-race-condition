(ns service-clojure-race-condition.datomic.dev_config
  (:require [datomic.client.api :as d]))

(def db-name-transactions "transactions")

; https://docs.datomic.com/client-api/datomic.client.api.html#var-client
(def client-local
  (d/client {:server-type :dev-local
             :system      "dev"
             :storage-dir "/Users/cesar.alcancio/personal/datomic/storage"}))

(def transaction-schema
  [{:db/ident       :transaction/id
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Transaction ID"}
   {:db/ident       :transaction/description
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Transaction description"}
   {:db/ident       :transaction/amount
    :db/valueType   :db.type/bigdec
    :db/cardinality :db.cardinality/one
    :db/doc         "Transaction amount"}
   {:db/ident       :transaction/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant the record is created"}])

(defn list-databases! [client]
  (d/list-databases client {}))
; (list-databases! client-local)

(defn create-database! [client db-name]
  (d/create-database client {:db-name db-name}))
; (create-database! client-local db-name-transactions)

(defn delete-database! [client db-name]
  (d/delete-database client {:db-name db-name}))
; (delete-database! client-local db-name-transactions)

(defn conn! [client db-name]
  (d/connect client {:db-name db-name}))

(defn create-schema! [conn schema]
  (d/transact conn {:tx-data schema}))
