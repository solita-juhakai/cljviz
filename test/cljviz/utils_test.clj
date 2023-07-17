(ns cljviz.utils-test 
  (:require [cljviz.util.utils :refer [filter-var-def-keys]]
            [clojure.test :refer [deftest is run-all-tests run-tests testing]]))

((deftest filter-var-def-keys-test
      (testing "Testing filter-var-def-keys from utils.clj"
        (is
         (= 
          (conj () {:ns "cljviz.core", :name "-main", :defined-by "clojure.core/defn", :doc "Cljviz turns clj-kondo analysis output to visul diagram.\n   First input argument clj-file or directory,\n   second optional output type (pl, gv or ws),\n   output for pl and gv is string presentation of chosen output type."})
           (filter-var-def-keys
            [{:end-row 29, :name-end-col 12, :name-end-row 11, :name-row 11, :ns "cljviz.core", :name "-main", :defined-by "clojure.core/defn", :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj", :col 1, :name-col 7, :end-col 57, :varargs-min-arity 0, :doc "Cljviz turns clj-kondo analysis output to visul diagram.\n   First input argument clj-file or directory,\n   second optional output type (pl, gv or ws),\n   output for pl and gv is string presentation of chosen output type.", :row 11}])) "name, defined-by and ns are escaped with double quotes")"")))

(comment
  (run-tests)
)