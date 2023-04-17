(ns cljviz.util.lint
  (:require [clj-kondo.core :as kondo]))

(defn run-lint-analysis 
  "runs clj-kondo linter with analysis on file or dir F,
   see https://github.com/clj-kondo/clj-kondo/tree/master/analysis"
  [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :output {:format :edn}}}))

(comment
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (group-by :ns (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
