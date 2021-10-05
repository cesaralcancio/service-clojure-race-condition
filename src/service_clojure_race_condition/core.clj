(ns service-clojure-race-condition.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
