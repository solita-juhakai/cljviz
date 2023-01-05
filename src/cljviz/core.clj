(ns cljviz.core 
  (:require [clj-kondo.core :as kondo]
            [clojure.string :as string]
            )
  (:gen-class))

(def ns-set #{})
;; => #'cljviz.core/ns-set


(defn run-lint-analysis "runs clj-kondo linter with analysis on FILE f" [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :output {:format :edn}}}))

(defn filter-var-def-keys "Filter certain fields from lint analysis var def map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m)
  )

(defn underscore-sp-chrs [s]
  (string/replace (string/replace s #"[ -<>/]" "_") #"__" "")
  )

(defn wonky-hash "Makes a string hash of input string, if initial chr is '-' replace with A " [i]
  (string/replace (str (.hashCode i)) #"^-" "A")
  )

(defn create-plantuml-object "Create plantuml object from a map :name key and :defined-by key"[m]
  (let [i (str \u0022 (m :name) " <<" (m :defined-by) ">>" \u0022)]
    (str "object " i " as " (underscore-sp-chrs i) "\n"))
  )


(defn -main
  "Attempt to turn clj-kondo analysis output to plantuml diagram. Input argumemnt clj-file or directory, output plantuml description."
  [& args]
  (if args
    ((println "@startuml")
     (apply println (map #(create-plantuml-object %) (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))))
     (println "@enduml"))
    (println "Must have at least one argument!")))
;;(println "Need an input clj-file or directory")))

(comment
  (-main "test")
  (-main)
  (println \u0022)
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))
  (merge {:a 1 :b 2} {:a 2 :c 3})
  (select-keys {:ns "test" :b 2 :c 3} [:ns])
  (map val (select-keys {:ns "test" :b 2 :c 3} [:ns]))
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (map #(apply println "object" (create-plantuml-object %) "as" "test") (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (create-plantuml-object {:name "tes-t" :t "foo" :defined-by "clojure.core/defn"})
  (underscore-sp-chrs "-te-st/fn <<clojure.core/defn>>")
  (.hashCode "t/fn <<clojure.core/defn>>")
  (wonky-hash (underscore-sp-chrs "t/fn <<clojure.core/defn>>")) 
  )
