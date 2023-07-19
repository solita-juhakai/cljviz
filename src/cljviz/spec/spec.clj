(ns cljviz.spec.spec
  (:require [clojure.repl :refer [doc]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def :cljviz.spec/starts-with-hash #(str/starts-with? % "#"))
(s/def :cljviz.spec/rand-color-type (s/and
                                          string?
                                          #(= 7 (count %))
                                          :cljviz.spec/starts-with-hash))

(comment
;;  (s/conform even? 10)
;;  (s/explain :cljviz.spec/rand-color-type "#122121")
;;  (s/valid? :cljviz.spec/rand-color-type "#122121")
;;  (doc :cljviz.spec/rand-color-type)
  )