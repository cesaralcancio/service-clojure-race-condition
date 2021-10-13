(ns service-clojure-race-condition.end-to-end.tasks-it
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.system :as system]))

(def component-result (component/start (system/local-environment)))
(def test-request (-> component-result :http-server :http-server :test-request))

; create tasks
(test-request :get "/hello?name=Cesar")
(test-request :post "/tasks?name=Run&status=pending")
(test-request :post "/tasks?name=Read&status=pending")
(test-request :post "/tasks?name=Study&status=done")

(clojure.edn/read-string (:body (test-request :get "/tasks")))

; delete all tasks
(let [tasks (clojure.edn/read-string (:body (test-request :get "/tasks")))
      vals (vals tasks)]
  (doseq [val vals]
    (clojure.pprint/pprint (test-request :delete (str "/tasks/" (:id val))))))

; create & update task
(clojure.edn/read-string (:body (test-request :get "/tasks")))
(test-request :post "/tasks?name=Running&status=pending")
(def task-id (-> (clojure.edn/read-string (:body (test-request :get "/tasks"))) keys first))
(test-request :patch (str "/tasks/" task-id "?name=FinishClojureStudy&status=done"))
