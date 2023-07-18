(ns cljviz.spec.spec
  (:require [cljviz.util.utils :refer [rand-color]]
            [clojure.repl :refer [doc]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def :cljviz.spec.spec/starts-with-hash #(str/starts-with? % "#"))
(s/def :cljviz.spec.spec/rand-color-type (s/and
                                          string?
                                          #(= 7 (count %))
                                          :cljviz.spec.spec/starts-with-hash))

(comment
  (doc s/conform)
  (s/conform even? 10)
  (s/valid? even? 1)
  (rand-color)
  (s/conform :cljviz.spec.spec/rand-color-type (rand-color))
  (s/explain :cljviz.spec.spec/rand-color-type "1122121")
  )