(ns cljviz.writer.plwriter 
  (:require [cljviz.util.lint :refer [run-lint-analysis]]
            [cljviz.util.utils :refer [filter-usage-var-defs
                                       filter-var-def-keys m-wonky-hash rand-color]]
            [clojure.string :as string]))

(defn create-plantuml-object "Create plantuml object from a map M :name, :defined-by and :ns key" [m]
  ;; object "ns-set <<clojure.core/def>>" as object_ns_set_clojure_core_def {
  ;; dummy is a very long text 1 
  ;; 3 4. with new lines and all
  ;; testi ng}
  (let [dn (str (m :name))
        ds (last (string/split (name (m :defined-by)) #"/"))
;        i (str (m :name) " <<" (last (string/split (name (m :defined-by)) #"/")) ">>")
        pi (m-wonky-hash (str (m :name) "_" (m :ns)))
        doc (m :doc)]
    (when (not (= "declare" ds))
      (str "object " \u0022 dn " <<" ds ">>" \u0022 " as " pi (when doc (str " {\n" doc "\n}")) "\n"))))

(comment
  (map #(apply println (create-plantuml-object %)) (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (create-plantuml-object {:name "tes-t" :doc "foo" :defined-by "clojure.core/defn" :ns "cljvis.core"})
  (create-plantuml-object {:name "tes-t" :doc "foo" :defined-by "clojure.core/declare" :ns "cljvis.core"})
  (def t-m '{:col 1
             :defined-by clojure.core/defn
             :doc "runs clj-kondo linter with analysis on FILE f"
             :end-col 51
             :end-row 19
             :filename "./src/cljviz/core.clj"
             :fixed-arities #{1}
             :name run-lint-analysis
             :name-col 7
             :name-end-col 24
             :name-end-row 16
             :name-row 16
             :ns cljviz.core
             :row 16})
  (m-wonky-hash (str (t-m :name) "_" (t-m :ns)))
  (compare "declare" "declare"))


(defn create-pl-ob-package "Create plantuml package section from vector V with first :ns and second map of vars" [v]
  (let [i (name (first v))
        m (second v)]
    (str "package " i " {" "\n" (apply str (into #{} (map #(create-plantuml-object %) m))) " }" "\n")))

(comment
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))))))


(defn create-pl-links "Provides Plantuml relationship section links based on map M :from-var :from :name and :to values" [m]
  (let [frnname (m :from-var)
        frnnamens (m :from)
        tn (m :name)
        tns (m :to)]
    (when (and tn tns frnname frnnamens)
      (let [fr (m-wonky-hash (str frnname "_" frnnamens))
            to (m-wonky-hash (str tn "_" tns))]
        (when (some? to) (list fr to))))))

(comment
  (create-pl-links '{:end-row 102,
                     :name-end-col 28,
                     :name-end-row 102,
                     :name-row 102,
                     :name tes-t,
                     :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj",
                     :from cljviz.core,
                     :col 26,
                     :name-col 26,
                     :from-var filter-from-vars,
                     :end-col 28,
                     :row 102,
                     :to cljviz.core})
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
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
                     :to aoc2022.aoc22-8}))

(defn main-pl-writer "Main writer for plantuml output" [f]
  (let [ma (:analysis (run-lint-analysis f))
        m-d (:var-definitions ma)
        m-u (:var-usages ma)]
    (do
      (println "@startuml" (last (string/split f #"/")))
      (apply println (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys m-d))))
      (apply println
             (map #(str (first (nth % 0)) "-[" (rand-color) ",thickness=" (nth % 1) "]->" (second (nth % 0)) ": " (nth % 1) "\n")
                  (frequencies
                   (filter identity (map #(create-pl-links %)
                                         (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))))
      (println "@enduml"))))