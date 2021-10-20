(ns service-clojure-race-condition.logic.transactions)

(defn exceeded-limit?
  [transactions new-transaction]
  (let [all-transactions (conj transactions new-transaction)
        total (reduce #(+ %1 (:amount %2)) 0 all-transactions)]
    (>= total 1000)))
