(ns sample.core
  (:require [sample.util :as util]
            [clojure.string :as str]))

(defn process-data
  "Process some data using utility functions"
  [data]
  (-> data
      util/clean-data
      str/trim))

(defn -main
  "Sample main function"
  [& args]
  (println "Processing:" (first args))
  (when (first args)
    (process-data (first args))))
