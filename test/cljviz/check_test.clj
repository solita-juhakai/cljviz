(ns cljviz.check-test
  (:require [cljviz.util.utils :refer [m-wonky-hash]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

;; (def sort-idempotent-prop
;;   (prop/for-all [v (gen/vector gen/small-integer)]
;;     (= (sort v) (sort (sort v)))))

;; (tc/quick-check 100 sort-idempotent-prop)

(def check-uniq-hash
  (prop/for-all [v (gen/set gen/string-alphanumeric)]
    (apply distinct? map #(m-wonky-hash %) v)))

(tc/quick-check 1000 check-uniq-hash)

(comment
  (gen/sample gen/string-alphanumeric)
  (gen/generate gen/string-alphanumeric)
  (gen/sample (gen/set gen/string-alphanumeric))
  (apply distinct? (map #(m-wonky-hash %) ["dd" "ddddddddddddd" "w" ""]))
  (apply distinct? (map #(hash %) ["dd" "ddddddddddddd" "w" "dd"]))
)
