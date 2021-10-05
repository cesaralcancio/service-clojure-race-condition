(ns service-clojure-race-condition.components.routes_component
  (:require [com.stuartsierra.component :as component]
            [service-clojure-race-condition.routes :as routes]))

(defrecord Rotas []
  component/Lifecycle

  (start [this]
    (assoc this :endpoints routes/routes))

  (stop [this]
    (assoc this :endpoints nil)))

(defn new-rotas []
  (->Rotas))
