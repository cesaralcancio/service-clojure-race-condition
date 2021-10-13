(ns service-clojure-race-condition.datomic.common-config)

(def db-name-transactions "transactions")

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
