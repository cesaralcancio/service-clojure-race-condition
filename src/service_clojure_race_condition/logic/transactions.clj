(ns service-clojure-race-condition.logic.transactions)

(defn exceeded-limit?
  [transactions]
  (let [total (reduce #(+ %1 (:amount %2)) 0 transactions)]
    (>= total 1000)))
