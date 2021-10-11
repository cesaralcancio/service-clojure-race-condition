(ns service-clojure-race-condition.components.http-server
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [io.pedestal.interceptor :as i]
            [com.stuartsierra.component :as component]))

(defonce server (atom nil))

(defn start-server! [service-map]
  (reset! server (http/start (http/create-server service-map))))

(defn test-request [verb url]
  (test/response-for (::http/service-fn @server) verb url))

(defn stop-server! []
  (http/stop @server))

(defn restart-server! [service-map]
  (stop-server!)
  (start-server! service-map))

(defn try-to-start! [service-map]
  (try (start-server! service-map) (catch Exception e (println "Error on start" (.getMessage e))))
  (try (restart-server! service-map) (catch Exception e (println "Error on restart" (.getMessage e)))))

(defrecord Server [database datomic routes]
  component/Lifecycle

  (start [this]
    (let [db-interceptor {:name  :db-interceptor
                          :enter (fn [context]
                                   (-> context
                                       (update :request assoc :store (:store database))
                                       (update :request assoc :datomic (:datomic datomic))))}
          service-map-base {::http/routes (:endpoints routes)
                            ::http/port   9999
                            ::http/type   :jetty
                            ::http/join?  false}
          service-map (-> service-map-base
                          (http/default-interceptors)
                          (update ::http/interceptors conj (i/interceptor db-interceptor)))]
      (try-to-start! service-map)
      (assoc this :http-server {:test-request test-request :server server})))
  (stop [this]
    (assoc this :test-request nil)))

(defn new-http-server []
  (map->Server {}))
