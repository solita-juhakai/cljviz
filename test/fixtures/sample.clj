(ns fixtures.sample
  (:require [clojure.string :as str]))

(defn helper-fn
  "A helper function"
  [x]
  (str/upper-case x))

(defn main-fn
  "Main function that uses helper"
  [input]
  (helper-fn input))
