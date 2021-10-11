(ns service-clojure-race-condition.end-to-end.transactions-it
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.system :as system]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [cheshire.core :as cheshire]
            [clojure.pprint :as c.pp]))

(def component-result (component/start (system/test-environment)))
(def server (-> component-result :http-server :http-server :server))

(def all-transactions
  (-> (::http/service-fn @server)
      (test/response-for
        :get
        "/transactions")
      :body
      (cheshire/parse-string true)))
(c.pp/pprint all-transactions)

(reduce
  (fn [acc transaction]
    (+ acc (:amount transaction)))
  0 all-transactions)

(test/response-for
  (::http/service-fn @server)
  :post
  "/transactions"
  :headers {"Content-Type" "application/json"}
  :body "{\"description\":\"Iphone 10\",\"amount\":199.90}")
