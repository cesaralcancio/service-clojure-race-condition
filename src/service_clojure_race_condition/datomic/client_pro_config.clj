(ns service-clojure-race-condition.datomic.client-pro-config
  (:use clojure.pprint)
  (:require [datomic.client.api :as d]))

(def cfg
  {:server-type        :peer-server
   :access-key         "myaccesskey"
   :secret             "mysecret"
   :endpoint           "localhost:8998"
   :validate-hostnames false})

(defn client!
  [config]
  (d/client config))

(defn list-databases! [client]
  (d/list-databases client {}))
; (list-databases! client-local)

(defn create-database! [client db-name]
  (d/create-database client {:db-name db-name}))
; (create-database! client-local db-name-transactions)

(defn delete-database! [client db-name]
  (d/delete-database client {:db-name db-name}))
; (delete-database! client-local db-name-transactions)

(defn connect!
  [client db-name]
  (d/connect client {:db-name db-name}))

(defn create-schema!
  [conn schema]
  (d/transact conn {:tx-data schema}))
