(ns service-clojure-race-condition.logic.transactions)

(defn exceeded-limit?
  ([transactions]
   (exceeded-limit? transactions nil))
  ([transactions new-transaction]
   (let [all-transactions (if new-transaction
                            (conj transactions new-transaction)
                            transactions)
         total (reduce #(+ %1 (:amount %2)) 0 all-transactions)]
     (>= total 1000))))
