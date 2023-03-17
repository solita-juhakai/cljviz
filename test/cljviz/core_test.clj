(ns cljviz.core-test
  (:refer-clojure :exclude [m-wonky-hash])
  (:require [cljviz.core :refer :all :as cl]
            [cljviz.util.utils :refer [m-wonky-hash]]
            [clojure.test :refer :all :as t]))

((deftest wonky-hash-test
   (testing "Test wonky-hash"
     (is (= "A1057869520" (m-wonky-hash "read_input_clojure_core_defn"))))))

(comment
  (run-tests)
  )