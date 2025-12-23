# Testing Improvement Plan for cljviz

**Date Created**: 23 December 2025  
**Project**: cljviz - Clojure Visualization Tool  
**Focus**: Increasing test coverage for public interfaces

## Executive Summary

This document outlines a comprehensive testing improvement plan for the cljviz project. Current test coverage is minimal, with only 4 test files covering a small subset of public APIs. This plan emphasizes testing all public interfaces (public vars/functions) to ensure reliability and maintainability.

## Current Test Coverage Analysis

### Existing Tests

#### 1. `test/cljviz/check_test.clj`
- **Purpose**: Property-based testing using test.check
- **Coverage**: Tests `m-wonky-hash` uniqueness property
- **Issues**: 
  - Contains commented-out code
  - Duplicate `check-uniq-hash` definition (both as def and defspec)
  - Syntax error: `apply distinct? map` should be `(apply distinct? (map ...))`

#### 2. `test/cljviz/core_test.clj`
- **Purpose**: Core functionality tests
- **Coverage**: Only tests `m-wonky-hash` function
- **Issues**: 
  - Wrapped deftest with extra parentheses `((deftest ...))`
  - Does NOT test the main public interface `-main`
  - Missing tests for primary entry point

#### 3. `test/cljviz/dotwriter_test.clj`
- **Purpose**: Tests dot writer functionality
- **Coverage**: 
  - `create-dot-node` ✓
  - `create-ns-dot-node` ✓
  - `create-dot-links` ✓
- **Missing**: `create-dot-subgraph`, `write-edge`, `main-dot-writer`, `ns-dot-writer`
- **Issues**: Wrapped deftest with extra parentheses

#### 4. `test/cljviz/utils_test.clj`
- **Purpose**: Utility function tests
- **Coverage**: 
  - `filter-var-def-keys` ✓
  - `rand-color` with spec validation ✓
- **Missing**: Most utility functions (11 out of 13 functions untested)
- **Issues**: Wrapped deftests with extra parentheses

### Uncovered Source Files

The following source files have **NO tests at all**:
1. `src/cljviz/util/lint.clj`
2. `src/cljviz/util/http.clj`
3. `src/cljviz/util/alephws.clj`
4. `src/cljviz/util/dotsh.clj`
5. `src/cljviz/writer/plwriter.clj`
6. `src/cljviz/spec/spec.clj` (partially tested via utils_test)

---

## Public Interface Inventory

### Core Module (`src/cljviz/core.clj`)

**Public Functions:**
- `-main [& args]` - **CRITICAL** - Main entry point, no tests
  - Handles command-line arguments
  - Delegates to different output modes (pl, gv, ws, default http)
  - Should test all code paths and error cases

### Utility Functions (`src/cljviz/util/utils.clj`)

**Tested:**
1. ✓ `filter-var-def-keys [m]` - Tested
2. ✓ `m-wonky-hash` (memoized) - Tested
3. ✓ `rand-color []` - Tested with spec

**Untested:**
4. ✗ `wonky-hash [i]` - String hashing function
5. ✗ `multi-pred [coll m]` - Filter with multiple predicates
6. ✗ `filter-usage-var-defs [m m-d]` - **IMPORTANT** - Core filtering logic
7. ✗ `filter-usage-ns-defs [m n-d]` - **IMPORTANT** - Namespace filtering
8. ✗ `rand-hex []` - Random hex digit generation
9. ✗ `xml-escape [s]` - **CRITICAL** - XML escaping for security
10. ✗ `graph-escape [s]` - Graph character escaping
11. ✗ `br-align [s]` - Newline to BR tag conversion

### Lint Utilities (`src/cljviz/util/lint.clj`)

**All Untested:**
1. ✗ `run-lint-analysis [f]` - **CRITICAL** - Core analysis function
2. ✗ `run-ns-analysis [f]` - **CRITICAL** - Namespace analysis

### Dot Writer (`src/cljviz/writer/dotwriter.clj`)

**Tested:**
1. ✓ `create-dot-node [m]` - Tested
2. ✓ `create-ns-dot-node [m]` - Tested
3. ✓ `create-dot-links [m]` - Tested

**Untested:**
4. ✗ `create-dot-subgraph [v]` - Subgraph creation
5. ✗ `create-ns-dot-links [m]` - Namespace link creation
6. ✗ `write-edge [V]` - **IMPORTANT** - Edge rendering with frequency
7. ✗ `main-dot-writer [f]` - **CRITICAL** - Main output generator
8. ✗ `ns-dot-writer [f]` - **CRITICAL** - Namespace output generator

### PlantUML Writer (`src/cljviz/writer/plwriter.clj`)

**All Untested:**
1. ✗ `create-plantuml-object [m]` - Object creation
2. ✗ `create-pl-ob-package [v]` - Package creation
3. ✗ `create-pl-links [m]` - Link creation
4. ✗ `main-pl-writer [f]` - **CRITICAL** - Main PlantUML generator

### HTTP Server (`src/cljviz/util/http.clj`)

**All Untested:**
1. ✗ `handler [request]` - HTTP request handler
2. ✗ `mw-response-body [handler f]` - Middleware
3. ✗ `start-http [f]` - **IMPORTANT** - HTTP server startup

### WebSocket Server (`src/cljviz/util/alephws.clj`)

**All Untested:**
1. ✗ `start-ui [req]` - UI handler with hiccup
2. ✗ `start-dot [req f]` - Dot format handler
3. ✗ `start-ns-dot [req f]` - Namespace dot handler
4. ✗ `wrap-start [handler f]` - Handler wrapper
5. ✗ `ws-handler [req]` - **IMPORTANT** - WebSocket handler
6. ✗ `send-all [msg]` - Broadcast to connections
7. ✗ `w-send-all [_ _ d]` - Watch callback
8. ✗ `watch-src [d]` - **IMPORTANT** - File watching
9. ✗ `start-ws [f]` - **CRITICAL** - WebSocket server startup

### Dot Shell Utilities (`src/cljviz/util/dotsh.clj`)

**All Untested:**
1. ✗ `escape-quot [s]` - Quote escaping
2. ✗ `create-svg [s]` - **CRITICAL** - SVG generation via shell

### Spec Definitions (`src/cljviz/spec/spec.clj`)

**Partially Tested:**
1. ✓ `:cljviz.spec/rand-color-type` - Tested via utils_test
2. ✓ `:cljviz.spec/starts-with-hash` - Tested indirectly

---

## Priority Levels

### P0 - Critical (Must Have)
Functions that are entry points or core to application functionality:
- `-main` (core.clj)
- `run-lint-analysis` (lint.clj)
- `run-ns-analysis` (lint.clj)
- `main-dot-writer` (dotwriter.clj)
- `ns-dot-writer` (dotwriter.clj)
- `main-pl-writer` (plwriter.clj)
- `create-svg` (dotsh.clj)
- `start-ws` (alephws.clj)
- `xml-escape` (utils.clj) - Security concern

### P1 - High (Should Have)
Important functions that support core features:
- `filter-usage-var-defs` (utils.clj)
- `filter-usage-ns-defs` (utils.clj)
- `write-edge` (dotwriter.clj)
- `start-http` (http.clj)
- `ws-handler` (alephws.clj)
- `watch-src` (alephws.clj)

### P2 - Medium (Nice to Have)
Supporting functions with clear responsibilities:
- `create-dot-subgraph` (dotwriter.clj)
- `create-ns-dot-links` (dotwriter.clj)
- `create-plantuml-object` (plwriter.clj)
- `create-pl-ob-package` (plwriter.clj)
- `create-pl-links` (plwriter.clj)
- `multi-pred` (utils.clj)
- `graph-escape` (utils.clj)
- `br-align` (utils.clj)

### P3 - Low (Future)
Simple or already memoized/tested functions:
- `wonky-hash` (utils.clj) - Already tested via memoized version
- `rand-hex` (utils.clj) - Simple random generation
- `escape-quot` (dotsh.clj)
- HTTP/WS middleware and helper functions

---

## Recommended Test Structure

### New Test Files Needed

```
test/cljviz/
  ├── check_test.clj         (fix existing)
  ├── core_test.clj          (expand existing)
  ├── dotwriter_test.clj     (expand existing)
  ├── utils_test.clj         (expand existing)
  ├── lint_test.clj          (NEW)
  ├── plwriter_test.clj      (NEW)
  ├── dotsh_test.clj         (NEW)
  ├── http_test.clj          (NEW)
  └── alephws_test.clj       (NEW)
```

---

## Detailed Testing Recommendations

### DTR-1. Core Module Tests (`test/cljviz/core_test.clj`) ✅ COMPLETED

**Fix existing issues:** ✅
- Remove extra parentheses around `deftest`
- Fix `m-wonky-hash` test (move to utils_test where it belongs)

**Add new tests:** ✅

```clojure
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

;; Note: Tests for ws and default (http) options are NOT included
;; They require integration tests with server lifecycle management (start/stop),
;; timeout handling, and proper cleanup. These should be added in Phase 4
;; as integration tests (see DTR-8 for WebSocket testing approach).
```

**Test Data Strategy:** ✅
- Create fixture directory in `test/fixtures/sample-project/` with minimal Clojure files
- Use these fixtures for integration-style tests

### DTR-2. Lint Utilities Tests (`test/cljviz/lint_test.clj` - NEW) ✅ COMPLETED

**Critical tests:** ✅

```clojure
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
```

**Note on error handling tests**: Tests for invalid paths are intentionally omitted as clj-kondo handles errors internally and returns results structures. The focus is on validating the structure and content of successful analysis results.

### DTR-3. Utils Tests Expansion (`test/cljviz/utils_test.clj`)

**Fix existing:**
- Remove extra parentheses around deftests

**Add critical tests:**

```clojure
(deftest xml-escape-test
  (testing "XML escape basic characters"
    (is (= "&amp;" (xml-escape "&")))
    (is (= "&lt;" (xml-escape "<")))
    (is (= "&gt;" (xml-escape ">")))
    (is (= "&quot;" (xml-escape "\"")))
    (is (= "&apos;" (xml-escape "'"))))
  
  (testing "XML escape combined"
    (is (= "&lt;div&gt;&quot;test&amp;data&quot;&lt;/div&gt;" 
           (xml-escape "<div>\"test&data\"</div>"))))
  
  (testing "XML escape with empty string"
    (is (= "" (xml-escape ""))))
  
  (testing "XML escape order matters (& first)"
    ;; Ensure & is escaped first to avoid double-escaping
    (is (= "&amp;lt;" (xml-escape "&lt;")))))

(deftest multi-pred-test
  (testing "Filter with single predicate"
    (is (= [{:a 1 :b 2}] 
           (multi-pred [{:a 1 :b 2} {:a 2 :b 3}] {:a 1}))))
  
  (testing "Filter with multiple predicates"
    (is (= [{:a 1 :b 2}] 
           (multi-pred [{:a 1 :b 2} {:a 1 :b 3}] {:a 1 :b 2}))))
  
  (testing "Filter with no matches"
    (is (empty? (multi-pred [{:a 1}] {:a 2})))))

(deftest filter-usage-var-defs-test
  (testing "Filter with matching definitions"
    ;; Use fixture data from actual clj-kondo output
    )
  
  (testing "Filter with no matches"
    (is (nil? (filter-usage-var-defs 
                {:from "ns1" :from-var "fn1" :to "ns2" :name "fn2"}
                [])))))

(deftest filter-usage-ns-defs-test
  (testing "Filter namespace usages with matching definitions"
    ;; Similar to var defs test
    ))

(deftest graph-escape-test
  (testing "Escape dots and dashes"
    (is (= "test_file_clj" (graph-escape "test-file.clj")))
    (is (= "___test___" (graph-escape "...test---")))))

(deftest br-align-test
  (testing "Replace newlines with BR tags"
    (is (= "line1<BR ALIGN=\"LEFT\"></BR>line2" 
           (br-align "line1\nline2")))
    (is (= "" (br-align "")))
    (is (= "no-newlines" (br-align "no-newlines")))))

(deftest wonky-hash-test
  (testing "Hash produces consistent output"
    (is (= (wonky-hash "test") (wonky-hash "test"))))
  
  (testing "Hash replaces leading dash with A"
    (let [h (wonky-hash "test")]
      (is (not (str/starts-with? h "-")))))
  
  (testing "Different inputs produce different hashes"
    (is (not= (wonky-hash "test1") (wonky-hash "test2")))))
```

### DTR-4. Dot Writer Tests Expansion (`test/cljviz/dotwriter_test.clj`)

**Fix existing:**
- Remove extra parentheses

**Add tests:**

```clojure
(deftest create-dot-subgraph-test
  (testing "Create subgraph with single namespace"
    (let [input ['ns.test [{:name "fn1" :defined-by "clojure.core/defn" 
                           :ns "ns.test" :doc "test"}]]
          result (create-dot-subgraph input)]
      (is (str/includes? result "subgraph cluster_"))
      (is (str/includes? result "ns.test"))
      (is (str/includes? result "fn1"))))
  
  (testing "Subgraph escapes namespace name"
    ;; Test with dots and dashes in namespace
    ))

(deftest create-ns-dot-links-test
  (testing "Create namespace links"
    (let [result (create-ns-dot-links {:from 'ns1 :to 'ns2})]
      (is (vector? result))
      (is (= 2 (count result)))
      (is (contains? (first result) :hash))
      (is (contains? (second result) :hash)))))

(deftest write-edge-test
  (testing "Write edge with frequency"
    (let [input [[{:name "fn1" :ns "ns1" :hash "123"} 
                  {:name "fn2" :ns "ns2" :hash "456"}] 3]
          result (write-edge input)]
      (is (str/includes? result "123->456"))
      (is (str/includes? result "penwidth=3"))
      (is (str/includes? result "label=3"))))
  
  (testing "Edge includes tooltip"
    (let [input [[{:name "fn1" :ns "ns1" :hash "123"} 
                  {:name "fn2" :ns "ns2" :hash "456"}] 1]
          result (write-edge input)]
      (is (str/includes? result "edgetooltip=")))))

(deftest main-dot-writer-integration-test
  (testing "Generate dot output for fixture project"
    (let [result (main-dot-writer "test/fixtures/sample-project")]
      (is (string? result))
      (is (str/starts-with? result "digraph"))
      (is (str/includes? result "subgraph"))
      (is (str/ends-with? (str/trim result) "}"))))
  
  (testing "Dot output is valid graphviz syntax"
    ;; Can be validated by attempting to parse with dot command
    ;; or checking for balanced braces
    ))

(deftest ns-dot-writer-integration-test
  (testing "Generate namespace view"
    (let [result (ns-dot-writer "test/fixtures/sample-project")]
      (is (string? result))
      (is (str/starts-with? result "digraph")))))
```

### DTR-5. PlantUML Writer Tests (`test/cljviz/plwriter_test.clj` - NEW)

```clojure
(ns cljviz.plwriter-test
  (:require [cljviz.writer.plwriter :refer [create-plantuml-object 
                                             create-pl-ob-package
                                             create-pl-links
                                             main-pl-writer]]
            [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]))

(deftest create-plantuml-object-test
  (testing "Create plantuml object from var definition"
    (let [result (create-plantuml-object 
                   {:name "test-fn" :defined-by "clojure.core/defn" 
                    :ns "test.ns" :doc "A test function"})]
      (is (str/includes? result "object"))
      (is (str/includes? result "test-fn"))
      (is (str/includes? result "<<defn>>"))
      (is (str/includes? result "A test function"))))
  
  (testing "Plantuml object without doc"
    (let [result (create-plantuml-object 
                   {:name "test-fn" :defined-by "clojure.core/defn" 
                    :ns "test.ns"})]
      (is (not (str/includes? result "{")))))
  
  (testing "Skip declare definitions"
    (let [result (create-plantuml-object 
                   {:name "test-fn" :defined-by "clojure.core/declare" 
                    :ns "test.ns"})]
      (is (nil? result)))))

(deftest create-pl-ob-package-test
  (testing "Create package with vars"
    (let [input ['test.ns [{:name "fn1" :defined-by "clojure.core/defn" 
                           :ns "test.ns"}
                          {:name "fn2" :defined-by "clojure.core/defn" 
                           :ns "test.ns"}]]
          result (create-pl-ob-package input)]
      (is (str/includes? result "package test.ns"))
      (is (str/includes? result "fn1"))
      (is (str/includes? result "fn2")))))

(deftest create-pl-links-test
  (testing "Create plantuml link"
    (let [result (create-pl-links {:from-var "fn1" :from "ns1" 
                                    :name "fn2" :to "ns2"})]
      (is (seq? result))
      (is (= 2 (count result)))))
  
  (testing "Return nil for incomplete data"
    (is (nil? (create-pl-links {:from-var "fn1"})))))

(deftest main-pl-writer-integration-test
  (testing "Generate plantuml output"
    (let [output (with-out-str (main-pl-writer "test/fixtures/sample-project"))]
      (is (str/includes? output "@startuml"))
      (is (str/includes? output "@enduml"))
      (is (str/includes? output "package")))))
```

### DTR-6. Dot Shell Tests (`test/cljviz/dotsh_test.clj` - NEW)

```clojure
(ns cljviz.dotsh-test
  (:require [cljviz.util.dotsh :refer [escape-quot create-svg]]
            [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]))

(deftest escape-quot-test
  (testing "Escape quotes in string"
    ;; Note: Current implementation seems incorrect (replaces " with ")
    ;; Should escape properly for shell
    (is (string? (escape-quot "test \"quoted\" string")))))

(deftest create-svg-test
  (testing "Create SVG from valid dot input"
    (let [dot-input "digraph test { a -> b; }"
          result (create-svg dot-input)]
      (is (map? result))
      (is (contains? result :exit))
      (is (contains? result :out))
      (is (= 0 (:exit result)) "dot command should succeed")
      (is (str/includes? (:out result) "<svg") "should produce SVG")))
  
  (testing "Create SVG handles invalid input gracefully"
    (let [result (create-svg "invalid dot syntax")]
      (is (map? result))
      (is (not= 0 (:exit result)) "should fail with non-zero exit")))
  
  (testing "Create SVG requires dot command in PATH"
    ;; This is an environmental test
    ;; Could mock or skip if dot not available
    ))
```

### DTR-7. HTTP Server Tests (`test/cljviz/http_test.clj` - NEW)

```clojure
(ns cljviz.http-test
  (:require [cljviz.util.http :refer [handler mw-response-body start-http]]
            [clojure.test :refer [deftest is testing]]))

(deftest handler-test
  (testing "Basic handler returns 200"
    (let [response (handler {})]
      (is (= 200 (:status response)))
      (is (= "image/svg+xml" (get-in response [:headers "Content-Type"])))))
  
  (testing "Handler includes CORS header"
    (let [response (handler {})]
      (is (= "*" (get-in response [:headers "Access-Control-Allow-Origin"]))))))

(deftest mw-response-body-test
  (testing "Middleware adds body to response"
    (let [test-handler (fn [req] {:status 200})
          wrapped (mw-response-body test-handler "test body")
          response (wrapped {})]
      (is (= "test body" (:body response))))))

;; Note: start-http integration test would require:
;; - Starting server in separate thread
;; - Making HTTP request
;; - Stopping server
;; - Should be in integration test suite
```

### DTR-8. WebSocket Server Tests (`test/cljviz/alephws_test.clj` - NEW)

```clojure
(ns cljviz.alephws-test
  (:require [cljviz.util.alephws :refer [start-ui start-dot start-ns-dot 
                                          wrap-start send-all w-send-all]]
            [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]))

(deftest start-ui-test
  (testing "UI handler returns HTML"
    (let [response (start-ui {})]
      (is (= 200 (:status response)))
      (is (= "text/html" (get-in response [:headers "content-type"])))
      (is (str/includes? (:body response) "<script"))
      (is (str/includes? (:body response) "Graphviz")))))

(deftest start-dot-test
  (testing "Dot handler returns graphviz"
    (let [response (start-dot {} "test/fixtures/sample-project")]
      (is (= 200 (:status response)))
      (is (= "text/plain" (get-in response [:headers "content-type"])))
      (is (str/starts-with? (:body response) "digraph")))))

(deftest start-ns-dot-test
  (testing "Namespace dot handler returns graphviz"
    (let [response (start-ns-dot {} "test/fixtures/sample-project")]
      (is (= 200 (:status response)))
      (is (str/starts-with? (:body response) "digraph")))))

(deftest wrap-start-test
  (testing "Wrapper passes extra arguments"
    (let [test-handler (fn [req f] {:status 200 :file f})
          wrapped (wrap-start test-handler "test-file")
          response (wrapped {})]
      (is (= "test-file" (:file response))))))

;; WebSocket tests (ws-handler, send-all, watch-src, start-ws) would require:
;; - Mock WebSocket connections
;; - Test with aleph/manifold test utilities
;; - Integration test approach with actual WebSocket client
;; - File system mocking for watch-src
```

---

## Test Fixtures Strategy

### Create Standard Test Fixtures

**Location**: `test/fixtures/`

**Structure**:
```
test/fixtures/
  ├── sample.clj              (single file with basic functions)
  ├── sample-project/
  │   └── src/
  │       └── sample/
  │           ├── core.clj    (with -main and requires)
  │           └── util.clj    (with helper functions)
  └── expected-outputs/
      ├── sample.gv           (expected graphviz output)
      ├── sample.plantuml     (expected plantuml output)
      └── sample.svg          (expected svg output)
```

**Sample Fixture Content** (`test/fixtures/sample.clj`):
```clojure
(ns sample.core
  (:require [clojure.string :as str]))

(defn helper-fn
  "A helper function"
  [x]
  (str/upper-case x))

(defn main-fn
  "Main function that uses helper"
  [input]
  (helper-fn input))
```

This provides predictable output for testing linting, analysis, and diagram generation.

---

## Testing Best Practices for This Project

### 1. Use Fixtures for File-Based Tests
- Create minimal Clojure files in `test/fixtures/`
- Use these for testing lint analysis and output generation
- Keep fixtures small and focused

### 2. Separate Unit and Integration Tests
- **Unit tests**: Individual function behavior (most tests)
- **Integration tests**: End-to-end flows (-main, server startup)
- Consider separate test namespaces or use metadata: `^:integration`

### 3. Mock External Dependencies
- **File system**: Use fixtures instead of real projects
- **Shell commands**: Consider mocking `clojure.java.shell/sh` for dotsh tests
- **HTTP/WebSocket**: Use test clients or mocking libraries

### 4. Test Error Cases
- Invalid input paths
- Malformed Clojure code
- Missing dependencies (dot command not in PATH)
- Empty input
- Large input (performance/memory)

### 5. Use Spec-based Testing Where Applicable
- Already started with `rand-color` spec test
- Consider spec tests for:
  - clj-kondo analysis output structure
  - Generated dot/plantuml syntax
  - HTTP request/response shapes

### 6. Property-Based Testing
- Current `check_test.clj` has the right idea
- Fix and expand for:
  - Hash uniqueness (already attempted)
  - XML escaping (should never produce invalid XML)
  - Graph escaping (valid graphviz identifiers)
  - Idempotency of escape functions

### 7. Test Data Validation
- Validate generated dot/plantuml is syntactically correct
- Can use `dot -Tsvg` in test to validate graphviz output
- Helps catch regressions in output format

---

## Implementation Roadmap

### Phase 1: Critical Coverage (1-2 weeks)
**Goal**: Test all P0 (Critical) functions

1. Fix syntax errors in existing tests (remove extra parentheses)
2. Create test fixtures (sample.clj, sample-project/)
3. Implement `test/cljviz/lint_test.clj`
   - `run-lint-analysis` tests
   - `run-ns-analysis` tests
4. Expand `test/cljviz/core_test.clj`
   - `-main` function with all code paths
5. Implement `test/cljviz/dotsh_test.clj`
   - `create-svg` with valid/invalid input
6. Expand utils tests for `xml-escape` (security-critical)

### Phase 2: High Priority Coverage (1-2 weeks)
**Goal**: Test all P1 (High) functions

1. Implement `test/cljviz/plwriter_test.clj`
   - All PlantUML generation functions
   - Integration test for `main-pl-writer`
2. Expand `test/cljviz/utils_test.clj`
   - `filter-usage-var-defs`
   - `filter-usage-ns-defs`
   - `multi-pred`
3. Expand `test/cljviz/dotwriter_test.clj`
   - `write-edge`
   - `main-dot-writer` integration
   - `ns-dot-writer` integration
4. Implement `test/cljviz/http_test.clj`
   - Handler and middleware tests
   - Consider integration test for server

### Phase 3: Medium Priority Coverage (1 week)
**Goal**: Test all P2 (Medium) functions

1. Complete `dotwriter_test.clj`
   - `create-dot-subgraph`
   - `create-ns-dot-links`
2. Complete `plwriter_test.clj`
   - `create-pl-ob-package`
   - `create-pl-links`
3. Complete `utils_test.clj`
   - `graph-escape`
   - `br-align`

### Phase 4: Integration & Refinement (1 week)
**Goal**: WebSocket tests and comprehensive integration testing

1. Implement `test/cljviz/alephws_test.clj`
   - Unit tests for handlers
   - Consider integration tests for WebSocket (optional)
2. Add end-to-end integration tests
   - Full workflow from input to output
3. Add property-based tests
   - Fix and expand `check_test.clj`
4. Performance/load tests (optional)
   - Test with large projects
   - Memory usage validation

---

## Metrics and Success Criteria

### Coverage Targets
- **Phase 1**: >60% code coverage (critical paths)
- **Phase 2**: >75% code coverage (+ high priority)
- **Phase 3**: >85% code coverage (+ medium priority)
- **Phase 4**: >90% code coverage (full public API)

### Quality Targets
- All public functions have at least one test
- All tests pass consistently
- No syntax errors in test code
- Integration tests cover all command-line options
- Error cases are tested for critical functions

### Documentation
- Each test file includes descriptive docstrings
- Complex test scenarios are commented
- Test fixtures are documented with their purpose

---

## Risk Analysis & Mitigation

### Risk 1: External Dependencies
**Issue**: Tests depend on `dot` command, file system, network  
**Mitigation**: 
- Document required tools in test README
- Skip tests gracefully if tools unavailable (`:skip-meta`)
- Mock external calls where possible

### Risk 2: Async/Concurrent Code (WebSocket)
**Issue**: WebSocket and file watching are inherently async  
**Mitigation**:
- Use test utilities from aleph/manifold
- Consider marking as integration tests
- Use timeouts and proper cleanup

### Risk 3: Test Maintenance
**Issue**: Integration tests can be fragile  
**Mitigation**:
- Keep fixtures simple and minimal
- Document expected behavior clearly
- Regular test execution in CI

### Risk 4: Time Investment
**Issue**: Comprehensive testing takes significant time  
**Mitigation**:
- Prioritize by phases (P0 → P1 → P2)
- Focus on public interfaces first
- Defer complex integration tests if needed

---

## Tools and Libraries Recommendation

### Testing Libraries
- **Current**: `clojure.test` (built-in) ✓
- **Current**: `test.check` for property-based testing ✓
- **Consider**: `matcher-combinators` for deep assertions
- **Consider**: `kaocha` for better test runner

### Mocking
- **Consider**: `mock-clj` or `with-redefs` for mocking
- **Consider**: `clj-http-fake` if making HTTP requests in tests

### Coverage
- **Recommended**: `cloverage` for code coverage metrics
- Integration with CI/CD for automatic reports

### CI/CD
- **Recommended**: GitHub Actions for automated test runs
- Run tests on multiple JDK versions
- Generate and publish coverage reports

---

## Maintenance Plan

### Ongoing Practices
1. **Test-Driven Development (TDD)**: Write tests before new features
2. **Review Coverage**: Regular coverage reports (monthly)
3. **Refactor Tests**: Keep tests clean and maintainable
4. **Update Fixtures**: As code evolves, update test fixtures
5. **Document Changes**: Update this plan as priorities shift

### When to Update Tests
- Before adding new public functions
- When fixing bugs (add regression test first)
- When refactoring (tests should still pass)
- When external APIs change (clj-kondo, aleph, etc.)

---

## Conclusion

This testing improvement plan provides a comprehensive roadmap to increase test coverage for the cljviz project, with emphasis on public interfaces. By following the phased approach and priorities, the project can achieve >90% coverage of critical functionality within 4-6 weeks of focused effort.

The key success factors are:
1. **Fixing existing test issues** (syntax errors, structure)
2. **Creating reusable test fixtures**
3. **Prioritizing critical paths** (P0 → P1 → P2 → P3)
4. **Balancing unit and integration tests**
5. **Maintaining tests as code evolves**

By implementing this plan, cljviz will have a robust test suite that ensures reliability, facilitates refactoring, and provides confidence in future development.

---

## Appendix: Quick Reference

### Test Syntax Issues to Fix
- Remove extra parentheses: `((deftest ...))` → `(deftest ...)`
- Fix `apply distinct? map` → `(apply distinct? (map ...))`
- Move `m-wonky-hash` test to utils_test.clj

### Command to Run Tests
```bash
# Run all tests
clojure -X:test

# Run specific namespace
clojure -M:test -n cljviz.core-test

# Run with coverage (after adding cloverage)
clojure -M:test:coverage
```

### Coverage Commands (after setup)
```bash
# Install cloverage (add to deps.edn)
# Run with coverage
clojure -M:coverage

# Generate HTML report
clojure -M:coverage --html
```
