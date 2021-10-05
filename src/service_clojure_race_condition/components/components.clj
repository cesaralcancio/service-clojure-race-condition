(ns service-clojure-race-condition.components.components
  (:require [service-clojure-race-condition.components.http-server :as server]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.database :as database]
            [service-clojure-race-condition.components.routes_component :as routes])
  (:use [clojure.pprint]))

(defn test-environment []
  (component/system-map
    :database (database/new-database)
    :routes (routes/new-rotas)
    :http-server (component/using (server/new-http-server) [:database :routes])))
