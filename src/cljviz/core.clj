(ns cljviz.core
  (:require [cljviz.util.lint :refer [run-lint-analysis]]
            [cljviz.util.utils :refer [filter-usage-var-defs
                                       filter-var-def-keys rand-color]]
            [cljviz.writer.dotwriter :refer [create-pl-links
                                            create-pl-ob-package]]
            [clojure.string :as string])
  (:gen-class))

(defn -main
  "Cljviz turns clj-kondo analysis output to plantuml diagram. Input argument clj-file or directory, output plantuml description."
  [& args]
  (let [f (first args)
        ma (:analysis (run-lint-analysis f))
        m-d (:var-definitions ma)
        m-u (:var-usages ma)]
    (if f
      (do
        (println "@startuml" (last (string/split f #"/")))
        (apply println (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys m-d))))
        (apply println
               (map #(str (first (nth % 0)) "-[" (rand-color) ",thickness=" (nth % 1) "]->" (second (nth % 0)) ": " (nth % 1) "\n")
                    (frequencies
                     (filter identity (map #(create-pl-links %)
                                           (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))))
        (println "@enduml"))
      (println "Need an input clj-file or directory"))))

(comment
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src")
  (-main "/home/juhakairamo/Projects/clojure/aoc2022/src")
  (-main "/home/juhakairamo/Projects/clojure/xtdb-inspector/src")
  (-main "/home/juhakairamo/Projects/clojure/tab/src")
  (-main "./src")
  (last (string/split "c" #"/"))
  (def m-d-t (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (map #(filter-usage-var-defs % m-d-t) (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
