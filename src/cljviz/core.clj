(ns cljviz.core 
  (:require [clj-kondo.core :as kondo]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [clojure.edn :as edn]
            )
  (:gen-class))

(def ns-set #{})
;; => #'cljviz.core/ns-set


(defn run-lint-analysis "runs clj-kondo linter with analysis on FILE f" [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :output {:format :edn}}}))

(defn filter-var-def-keys "Filter :ns :name :defined-by :doc fields from lint analysis :var-definitions map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m)
  ) 

(defn get-val-as-str-from-edn "get (symbol) value of key K as string from edn map M" [m k]
  (name ((edn/read-string m) k)))

(defn underscore-sp-chrs [s]
  (string/replace (string/replace (string/replace s #"([-]*)([\p{Alnum}]+)([ -<>/\"\s])" "$2_") #"__" "") #"[ <>]" "")
  )

(comment
  (underscore-sp-chrs "te-st/fn- \"<<clojure.core/defn>>")
  (underscore-sp-chrs "2-find-2- -marker-3")
  )

(defn wonky-hash "Makes a string hash of input string, if initial chr is '-' replace with A " [i]
  (string/replace (str (.hashCode i)) #"^-" "A")
  )

(comment
  (wonky-hash "read_input_clojure_core_defn_")
  )

(defn create-plantuml-object "Create plantuml object from a map :name key and :defined-by key"[m]
  (let [i (str (m :name) " <<" (m :defined-by) ">>")
        pi (wonky-hash (str (underscore-sp-chrs i) "_" (m :ns)))]
    (str "object " \u0022 i \u0022 " as " pi "\n"))
  )

(defn create-pl-ob-package "Create plantuml package section from vector V with first :ns and second map of vars" [v]
  (let [i (name (first v))]
    (str "package " i " {" "\n" (apply str (map #(create-plantuml-object %) (second v))) " }" "\n")))

(defn -main
  "Attempt to turn clj-kondo analysis output to plantuml diagram. Input argument clj-file or directory, output plantuml description."
  [& args]
  (let [f (first args)
        ma (:analysis (run-lint-analysis f))]
        (if f 
          ((println "@startuml")
;;           (apply println (map #(create-plantuml-object %) (filter-var-def-keys (:var-definitions ma))))
           (apply println (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions ma)))))
           (println "@enduml"))
          (println "Need an input clj-file or directory"))))

(comment
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src")
  (-main "/home/juhakairamo/Projects/clojure/aoc2022/src")
  (-main)
  (apply str "package " "ns-name" " {")
  (println \u0022)
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (name ((edn/read-string "{:ns cljviz.core,
                           :name create-plantuml-object,
                           :defined-by clojure.core/defn}") :ns))
  (get-val-as-str-from-edn "{:ns cljviz.core, :name create-plantuml-object,:defined-by clojure.core/defn}" :name) (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))
  (edn/read (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (group-by :ns (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (group-by :ns [{:ns 1 :a 2} {:ns 4 :c 45} {:ns 1 :b 2} {:ns 3 :a 2}])
  (keys {:a 1 :b 2})
  (select-keys {:ns "tvrees" :b 2 :c 3} [:ns])
  (map val (select-keys {:ns "test" :b 2 :c 3} [:ns]))
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (walk/keywordize-keys (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (map #(assoc % :ns (keyword (val :ns))) (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  ({:ns "cljviz.core", :name "create-plantuml-object", :defined-by "clojure.core/defn", :doc "Create plantuml object from a map :name key and :defined-by key"} :ns)
  (walk/keywordize-keys (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
  (create-pl-ob-package (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))))
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
  (walk/keywordize-keys (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/core.clj"))))))
  (map #(apply println "object" (create-plantuml-object %) "as" "test") (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (create-plantuml-object {:name "tes-t" :t "foo" :defined-by "clojure.core/defn"})
  (.hashCode "t/fn <<clojure.core/defn>>")
  (wonky-hash (underscore-sp-chrs "t/fn <<clojure.core/defn>>"))
  (vals {:aoc2022.aoc22_2 '[{:ns aoc2022.aoc22_2, :name "process-file-2", :defined-by clojure.core/defn} {:ns aoc2022.aoc22_2, :name "score-map", :defined-by clojure.core/def} {:ns aoc2022.aoc22_2, :name "score-map-2", :defined-by clojure.core/def}]})
  (walk/keywordize-keys {aoc2022.aoc22_2 '[{:1 :2} {:3 :2}] :2 '[{:3 :2} {:4 :2}]})
  ;;(clojure.set/rename-keys {"aoc2022.aoc22_2" '[{:1 :2} {:3 :2}] :2 '[{:3 :2} {:4 :2}]} {"aoc2022.aoc22_2" :aoc2022.aoc22_2})
  (keyword "ns")
  )
