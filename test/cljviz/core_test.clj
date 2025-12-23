(ns cljviz.core-test
  (:require [cljviz.core :refer [-main]]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]))

;; Tests for cljviz.core functionality

(deftest main-with-no-args-test
  (testing "Main function with no arguments prints error message"
    (is (= "Need an input clj-file or directory\n"
           (with-out-str (-main))))))

(deftest main-with-pl-output-test
  (testing "Main function with pl output calls main-pl-writer"
    (let [output (with-out-str (-main "test/fixtures/sample-project" "pl"))]
      (is (string? output))
      (is (str/includes? output "@startuml"))
      (is (str/includes? output "@enduml"))
      (is (str/includes? output "package")))))

(deftest main-with-gv-output-test
  (testing "Main function with gv output produces graphviz format"
    (let [output (with-out-str (-main "test/fixtures/sample-project" "gv"))]
      (is (string? output))
      (is (str/includes? output "digraph"))
      (is (str/includes? output "subgraph")))))

(deftest main-with-single-file-test
  (testing "Main function works with single clj file"
    (let [output (with-out-str (-main "test/fixtures/sample.clj" "gv"))]
      (is (string? output))
      (is (str/includes? output "digraph")))))

;; Note: Tests for ws and default (http) options require integration tests
;; with server lifecycle management (start/stop) and are not included here

(comment
  (clojure.test/run-tests)
  (clojure.test/run-all-tests #"cljviz.*-test"))
