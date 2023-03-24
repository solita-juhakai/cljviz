(ns cljviz.writer.dotwriter 
  (:require [cljviz.util.lint :refer [run-lint-analysis]]
            [cljviz.util.utils :refer [filter-usage-var-defs
                                       filter-var-def-keys m-wonky-hash rand-color]]
            [clojure.string :as string]))

(defn create-dot-node "Create dot node from a map M :name, :defined-by and :ns key" [m]
  ;; object "ns-set <<clojure.core/def>>" as object_ns_set_clojure_core_def {
  ;; dummy is a very long text 1 
  ;; 3 4. with new lines and all
  ;; testi ng}
  (let [dn (str (m :name))
        ds (last (string/split (name (m :defined-by)) #"/"))
        node (str dn "<<" ds ">>")
        pi (m-wonky-hash (str (m :name) "_" (m :ns)))
        doc (m :doc)]
    (when (not (= "declare" ds))
      (str pi 
           "[\n"
           "id=" pi "\n"
           "label="\u0022 node \u0022"\n"
           (when doc (str "doc=" \u0022 doc \u0022 "\n"))
           "];\n"))))

(comment
  (map #(apply println (create-dot-node %)) (filter-var-def-keys   (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))) 
  (create-dot-node {:name "tes-t" :doc "foo" :defined-by "clojure.core/defn" :ns "cljvis.core"})
  (create-dot-node {:name "tes-t" :doc "foo" :defined-by "clojure.core/declare" :ns "cljvis.core"})
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
  


(defn create-dot-subgraph "Create dot subgraph from vector V with first :ns and second map of vars" [v]
  (let [i (name (first v))
        ri (string/replace i #"\." "_")
        m (second v)]
    (str "subgraph cluster_" ri " {" "\n" (apply str (into #{} (map #(create-dot-node %) m))) "\n label=" \u0022 i \u0022 "}" "\n")))

(comment
  (map #(create-dot-subgraph %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))))))


(defn create-dot-links "Provides dot relationship section links based on map M :from-var :from :name and :to values" [m]
  (let [frnname (m :from-var)
        frnnamens (m :from)
        tn (m :name)
        tns (m :to)]
    (when (and tn tns frnname frnnamens)
      (let [fr (m-wonky-hash (str frnname "_" frnnamens))
            to (m-wonky-hash (str tn "_" tns))]
        (when (some? to) (str fr "->" to ";"))))))

(comment
  (create-dot-links '{:end-row 102,
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
  (map #(create-dot-subgraph %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
  (create-dot-links '{:arity 1
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

(defn main-dot-writer "Main writer for graphviz output" [f]
  (let [ma (:analysis (run-lint-analysis f))
        m-d (:var-definitions ma)
        m-u (:var-usages ma)]
    (do
      (println "digraph" (last (string/split f #"/")) "{")
      (apply println (map #(create-dot-subgraph %) (group-by :ns (filter-var-def-keys m-d))))
      #_(apply println
               (map #(str (first (nth % 0)) "-[" (rand-color) ",thickness=" (nth % 1) "]->" (second (nth % 0)) ": " (nth % 1) "\n")
                    (frequencies
                     (filter identity (map #(create-dot-links %)
                                           (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))))
      (apply println (filter identity (map #(create-dot-links %) (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))
      (println "}"))))

