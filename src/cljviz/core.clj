(ns cljviz.core 
  (:require [clj-kondo.core :as kondo])
  (:gen-class))

(defn run-lint-analysis "runs clj-kondo linter with analysis on FILENAME" [_file]
  (kondo/run! {:lint (list _file)
               :config {:analysis true
                        :output {:format :edn}}}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World! ")
  (println (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))
  (if args args nil))

(comment
  (-main "test") 
  (-main)
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (seq? '("/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))
  (list "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  )
