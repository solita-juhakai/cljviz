(ns cljviz.core-test
  (:require [clojure.test :refer :all :as t]
            [cljviz.core :refer :all :as cl]))

((deftest wonky-hash-test
   (testing "Test wonky-hash"
     (is (= "A1057869520" (wonky-hash "read_input_clojure_core_defn"))))))

(comment
  (run-tests)
  )