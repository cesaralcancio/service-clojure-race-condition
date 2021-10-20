(ns service-clojure-race-condition.end-to-end.transactions-it
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.system :as system]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [cheshire.core :as cheshire]
            [clojure.pprint :as c.pp])
  (:use clojure.pprint))

;(def component-result (component/start (system/local-environment)))
;(def component-result (component/start (system/test-environment)))
(def component-result (component/start (system/peer-environment)))
(def server (-> component-result :http-server :http-server :server))

(def all-transactions
  (-> (::http/service-fn @server)
      (test/response-for
        :get
        "/transactions")
      :body
      (cheshire/parse-string true)))
(c.pp/pprint all-transactions)

(test/response-for
  (::http/service-fn @server)
  :post
  "/transactions-v2"
  :headers {"Content-Type" "application/json"}
  :body "{\"description\":\"Iphone 10\",\"amount\":200}")

(def transaction-id (-> all-transactions first :id))
(println "Removing: " transaction-id)
(test/response-for
  (::http/service-fn @server)
  :delete
  (str "/transactions/" transaction-id))






; insert 100 times sequentially
(dotimes [i 100]
  (test/response-for
    (::http/service-fn @server)
    :post
    "/transactions"
    :headers {"Content-Type" "application/json"}
    :body "{\"description\":\"Iphone 10\",\"amount\":200}"))

; insert 100 times race condition
(dotimes [i 100]
  (.start
    (Thread.
      (fn []
        (test/response-for
          (::http/service-fn @server)
          :post
          "/transactions"
          :headers {"Content-Type" "application/json"}
          :body "{\"description\":\"Iphone 10\",\"amount\":200}")))))

; insert 100 times race condition but handled by transactor
(dotimes [i 100]
  (.start
    (Thread.
      (fn []
        (test/response-for
          (::http/service-fn @server)
          :post
          "/transactions-v2"
          :headers {"Content-Type" "application/json"}
          :body "{\"description\":\"Iphone 10\",\"amount\":200}")))))

; find all transactions
(def transactions
  (-> (::http/service-fn @server)
      (test/response-for
        :get
        "/transactions")
      :body
      (cheshire/parse-string true)))
(pprint (count transactions))
(pprint transactions)

; delete all transactions
(doseq [trx transactions]
  (test/response-for
    (::http/service-fn @server)
    :delete
    (str "/transactions/" (:id trx))))

; check the total amount transactions
(reduce
  (fn [acc trx]
    (+ acc (:amount trx)))
  0 transactions)
