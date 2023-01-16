(ns cljviz.core 
  (:require [clj-kondo.core :as kondo]
            [clojure.string :as string]
            [clojure.edn :as edn])
  (:gen-class))

(def ol "object links map as {:ns {:var-name \"object-link\"}}" (atom {}))

(defn deep-merge "from https://clojuredocs.org/clojure.core/merge, LICENSE?" [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(comment
  ((reset! ol {})
   (swap! ol deep-merge {(keyword (name "test-ns")) {(keyword "test-name") "link-value"}})
   (swap! ol deep-merge @ol {:test-ns {:test-name-2 "link-value"}})
   (swap! ol deep-merge @ol {:other-ns {:other-name "other-lv"}})
   (:test-name (:test-ns @ol)))
  )

(defn run-lint-analysis "runs clj-kondo linter with analysis on FILE f" [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :output {:format :edn}}}))

(comment
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (edn/read (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (group-by :ns (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (group-by :ns [{:ns 1 :a 2} {:ns 4 :c 45} {:ns 1 :b 2} {:ns 3 :a 2}]))

(defn filter-var-def-keys "Filter :ns :name :defined-by :doc fields from lint analysis :var-definitions map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m)
  ) 

(comment 
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  )

(defn wonky-hash "Makes a string hash of input string, if initial chr is '-' replace with A " [i]
  (string/replace (str (.hashCode i)) #"^-" "A")
  )

(comment
  (wonky-hash "read_input_clojure_core_defn_")
  )

(defn create-plantuml-object "Create plantuml object from a map M :name, :defined-by and :ns key, store object hash to map ol"[m]
  (let [i (str (m :name) " <<" (m :defined-by) ">>")
        pi (wonky-hash (str i "_" (m :ns)))]
    (swap! ol deep-merge @ol {(keyword (m :ns)) {(keyword (m :name)) pi}})
    (str "object " \u0022 i \u0022 " as " pi "\n"))
  )

(comment 
  (reset! ol {})
  (map #(apply println "object" (create-plantuml-object %) "as" "test") (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (create-plantuml-object {:name "tes-t" :t "foo" :defined-by "clojure.core/defn"})
  (@ol)
  )

(defn create-pl-ob-package "Create plantuml package section from vector V with first :ns and second map of vars" [v]
  (let [i (name (first v))]
    (str "package " i " {" "\n" (apply str (map #(create-plantuml-object %) (second v))) " }" "\n")))

(comment
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
  (@ol)
  )

(defn filter-from-vars "filters maps with key :from-var from :var-usages map in kondo map M" [m]
  (filter identity (map #(when (:from-var %) %) (m :var-usages))))

(comment
  (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))
  (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))
  )

(defn create-pl-links [m]
  (let [frnname (keyword (:from-var m))
        frnnamens (keyword (:from m))
        tn (keyword (:name m))
        tns (keyword (:to m))]
        (when (and tn tns frnname frnnamens)
          (let [fr (frnname (frnnamens @ol))
                to (tn (tns @ol))]
            (when (some? to) (str fr " --> " to "\n"))))))

(comment
  (create-pl-links '{:end-row 102,
                    :name-end-col 28,
                    :name-end-row 102,
                    :name-row 102,
                    :name ol,
                    :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj",
                    :from cljviz.core,
                    :col 26,
                    :name-col 26,
                    :from-var filter-from-vars,
                    :end-col 28,
                    :row 102,
                    :to cljviz.core})
  (map #(create-pl-links %) (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (reset! ol {})
  (some? nil)
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
  (@ol)
  (map #(create-pl-links %) (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))
  (create-pl-links '{:arity 1
                    :col 28
                    :end-col 42
                    :end-row 115
                    :filename
                    "/home/juhakairamo/Projects/clojure/aoc2022/src/aoc2022/aoc22_8.clj"
                    :fixed-arities #{1}
                    :from aoc2022.aoc22-8
                    :from-var part-1
                    :name parse-intsXXX
                    :name-col 29
                    :name-end-col 39
                    :name-end-row 115
                    :name-row 115
                    :row 115
                    :to aoc2022.aoc22-8})
  )

(defn -main
  "Attempt to turn clj-kondo analysis output to plantuml diagram. Input argument clj-file or directory, output plantuml description."
  [& args]
  (let [f (first args)
        ma (:analysis (run-lint-analysis f))]
        (if f 
          (do
           (println "@startuml")
           (apply println (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions ma)))))
           (apply println (filter identity (map #(create-pl-links %) (filter-from-vars ma))))
           (println "@enduml"))
          (println "Need an input clj-file or directory"))))

(comment
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src")
  (-main "/home/juhakairamo/Projects/clojure/aoc2022/src")
  (-main)
  (reset! ol {})
  (@ol)
  )