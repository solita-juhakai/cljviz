(ns sample.util
  (:require [clojure.string :as str]))

(defn clean-data
  "Clean input data"
  [data]
  (str/lower-case (str data)))

(defn validate-data
  "Validate input data"
  [data]
  (and (string? data)
       (not (str/blank? data))))
