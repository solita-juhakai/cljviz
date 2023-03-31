(ns cljviz.util.dotsh
  (:require [clojure.java.shell :as shell]
            [clojure.string :as string]))

(defn escape-quot "escapes quotes" [s]
  (-> s
      (string/replace "\"" "\"")))

(defn create-svg 
  "creates svg output from input dot language description S,
   requiress graphviz dot command in path"
  [s]
  (shell/sh "dot" "-Tsvg" :in (escape-quot s)))

(comment
  (def test-input (str "digraph test {a[id=1];label=" \u0022 "NODE" \u0022 "}"))
  (print test-input)
  (escape-quot test-input)
  (create-svg "}digraph test {a[id=1];label=\"NODE\"}") 
  (create-svg (escape-quot test-input))
  )