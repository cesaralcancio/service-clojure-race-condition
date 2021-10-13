(ns service-clojure-race-condition.components.system
  (:require [service-clojure-race-condition.components.http-server :as server]
            [com.stuartsierra.component :as component]
            [service-clojure-race-condition.components.database :as database]
            [service-clojure-race-condition.components.routes_component :as routes]
            [service-clojure-race-condition.components.datomic :as datomic]
            [service-clojure-race-condition.components.datomic-pro :as datomic-pro])
  (:use [clojure.pprint]))

(defn local-environment []
  (component/system-map
    :database (database/new-database)
    :datomic (datomic/new-datomic)
    :routes (routes/new-rotas)
    :http-server (component/using (server/new-http-server) [:database :datomic :routes])))

(defn test-environment []
  (component/system-map
    :database (database/new-database)
    :datomic (datomic-pro/new-datomic)
    :routes (routes/new-rotas)
    :http-server (component/using (server/new-http-server) [:database :datomic :routes])))
