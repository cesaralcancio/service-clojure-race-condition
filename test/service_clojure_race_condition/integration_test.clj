(ns service-clojure-race-condition.integration-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.components :as components]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]))

(def component-result (component/start (components/test-environment)))
(def test-request (-> component-result :http-server :http-server :test-request))
(def server (-> component-result :http-server :http-server :server))

(test-request :get "/hello?name=Cesar")
(test-request :post "/tasks?name=Run&status=pending")
(test-request :post "/tasks?name=Read&status=pending")
(test-request :post "/tasks?name=Study&status=done")

(clojure.edn/read-string (:body (test-request :get "/tasks")))

(let [tasks (clojure.edn/read-string (:body (test-request :get "/tasks")))
      vals (vals tasks)]
  (doseq [val vals]
    (clojure.pprint/pprint (test-request :delete (str "/tasks/" (:id val))))))

(clojure.edn/read-string (:body (test-request :get "/tasks")))
(test-request :post "/tasks?name=Running&status=pending")
(def task-id (-> (clojure.edn/read-string (:body (test-request :get "/tasks"))) keys first))
(test-request :patch (str "/tasks/" task-id "?name=FinishClojureStudy&status=done"))


(test/response-for (::http/service-fn @server)
                   :post
                   "/transactions"
                   :headers {"Content-Type" "application/json"}
                   :body "{\"description\":\"Iphone\",\"amount\":10}")
