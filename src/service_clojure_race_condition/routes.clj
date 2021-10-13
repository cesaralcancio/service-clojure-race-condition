(ns service-clojure-race-condition.routes
  (:require [io.pedestal.http.route :as route]
            [service-clojure-race-condition.controllers.transactions :as c.transactions]
            [io.pedestal.http.body-params :as body-params])
  (:import (java.util UUID)))

(defn hello-world [request]
  {:status 200 :body (str "Welcome!!! " (get-in request [:query-params :name] "Everybody!"))})

(defn tasks [request]
  {:status 200 :body @(:store request)})

(defn create-task-helper [uuid name status]
  {:id uuid :name name :status status})

(defn create-task [request]
  (let [uuid (UUID/randomUUID)
        name (get-in request [:query-params :name])
        status (get-in request [:query-params :status])
        task (create-task-helper uuid name status)
        store (:store request)]
    (swap! store assoc uuid task)
    {:status 200 :body {:message "Task registered"
                        :task    task}}))

(defn remove-task [request]
  (let [store (:store request)
        task-id (get-in request [:path-params :id])
        task-id-uuid (UUID/fromString task-id)]
    (swap! store dissoc task-id-uuid)
    {:status 200 :body {:message "Task deleted"}}))

(defn update-task [request]
  (let [task-id (get-in request [:path-params :id])
        task-id-uuid (UUID/fromString task-id)
        name (get-in request [:query-params :name])
        status (get-in request [:query-params :status])
        task (create-task-helper task-id-uuid name status)
        store (:store request)]
    (swap! store assoc task-id-uuid task)
    {:status 200 :body {:message "Task updated!"
                        :task    task}}))

(defn get-transactions [request]
  (let [datomic (:datomic request)]
    (c.transactions/find-all! datomic)))

(defn post-transactions [request]
  (let [transaction (:json-params request)
        datomic (:datomic request)]
    (c.transactions/process! datomic transaction)))

(defn delete-transactions [request]
  (let [id (get-in request [:path-params :id])
        datomic (:datomic request)]
    (c.transactions/delete! datomic id)))

(def routes
  (route/expand-routes
    #{["/hello" :get hello-world :route-name :hello-world]
      ["/tasks" :post create-task :route-name :create-task]
      ["/tasks" :get tasks :route-name :tasks]
      ["/tasks/:id" :delete remove-task :route-name :remove-task]
      ["/tasks/:id" :patch update-task :route-name :update-task]
      ["/transactions" :get get-transactions :route-name :get-transactions]
      ["/transactions" :post [(body-params/body-params) post-transactions] :route-name :post-transactions]
      ["/transactions/:id" :delete delete-transactions :route-name :delete-transactions]}))
