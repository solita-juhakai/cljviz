(ns cljviz.lint-test
  (:require [cljviz.util.lint :refer [run-lint-analysis run-ns-analysis]]
            [clojure.test :refer [deftest is testing]]))

(deftest run-lint-analysis-test
  (testing "Lint analysis on valid Clojure file"
    (let [result (run-lint-analysis "test/fixtures/sample.clj")]
      (is (map? result))
      (is (contains? result :analysis))
      (is (contains? (:analysis result) :var-definitions))
      (is (contains? (:analysis result) :var-usages))))
  
  (testing "Lint analysis on directory"
    (let [result (run-lint-analysis "test/fixtures/sample-project")]
      (is (map? result))
      (is (seq (:var-definitions (:analysis result))))))
  
  (testing "Lint analysis returns findings from clj-kondo"
    (let [result (run-lint-analysis "test/fixtures/sample.clj")
          var-defs (:var-definitions (:analysis result))]
      (is (vector? var-defs))
      (is (pos? (count var-defs)))
      ;; Check that var definitions have expected keys
      (is (every? #(contains? % :name) var-defs))
      (is (every? #(contains? % :ns) var-defs)))))

(deftest run-ns-analysis-test
  (testing "Namespace analysis on valid file"
    (let [result (run-ns-analysis "test/fixtures/sample.clj")]
      (is (map? result))
      (is (contains? (:analysis result) :namespace-definitions))
      (is (contains? (:analysis result) :namespace-usages))))
  
  (testing "Namespace analysis on directory"
    (let [result (run-ns-analysis "test/fixtures/sample-project")
          ns-defs (:namespace-definitions (:analysis result))]
      (is (vector? ns-defs))
      (is (pos? (count ns-defs)))
      ;; Should find both sample.core and sample.util namespaces
      (is (>= (count ns-defs) 2)))))

(comment
  (clojure.test/run-tests))
