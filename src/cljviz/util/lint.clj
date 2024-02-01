(ns cljviz.util.lint
  (:require [clj-kondo.core :as kondo]))

(defn run-lint-analysis 
  "runs clj-kondo linter with analysis on file or dir F,
   see https://github.com/clj-kondo/clj-kondo/tree/master/analysis"
  [f]
  (kondo/run! {:lint (list f)
               :config {:analysis {:arglists true}
                        :output {:format :edn}
                        :var-definitions {:shallow true}}}))

(defn run-ns-analysis
  "runs clj-kondo linter with ns analysis on file or dir F,
   see https://github.com/clj-kondo/clj-kondo/tree/master/analysis"
  [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :var-usages false
                        :output {:format :edn} 
                        :var-definitions {:shallow true}}
               :skip-lint true}))

(comment
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (group-by :ns (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  ((juxt :namespace-definitions :namespace-usages) (:analysis (run-ns-analysis "/home/juhakairamo/Projects/clojure/cljviz/src"))))
