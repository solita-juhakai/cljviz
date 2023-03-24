(ns cljviz.core
  (:require [cljviz.util.lint :refer [run-lint-analysis]]
            [cljviz.util.utils :refer [filter-usage-var-defs]]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [cljviz.writer.plwriter :refer [main-pl-writer]]
            [clojure.string :as string])
  (:gen-class))

(defn -main
  "Cljviz turns clj-kondo analysis output to plantuml diagram. First input argument clj-file or directory, second output type (pl or gv), output is string presentation of chosen output type."
  [& args]
  (let [f (first args)
        o (second args)]
        ;; ma (:analysis (run-lint-analysis f))
        ;; m-d (:var-definitions ma)
        ;; m-u (:var-usages ma)]
    (if f
      (cond
        (= o "pl") (main-pl-writer f)
        (= o "gv") (main-dot-writer f)
        :else (println "Need proper output type"))
      (println "Need an input clj-file or directory"))))

(comment
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj" "pl")
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src" "gv")
  (-main "/home/juhakairamo/Projects/clojure/aoc2022/src")
  (-main "/home/juhakairamo/Projects/clojure/xtdb-inspector/src")
  (-main "/home/juhakairamo/Projects/clojure/tab/src")
  (-main "./src")
  (last (string/split "c" #"/"))
  (def m-d-t (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (map #(filter-usage-var-defs % m-d-t) (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
