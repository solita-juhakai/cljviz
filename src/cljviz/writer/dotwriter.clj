(ns cljviz.writer.dotwriter "ns test doc-string" 
  (:require [cljviz.util.lint :refer [run-lint-analysis run-ns-analysis]]
            [cljviz.util.utils :refer [br-align filter-usage-ns-defs
                                       filter-usage-var-defs filter-var-def-keys
                                       graph-escape m-wonky-hash rand-color xml-escape]]
            [clojure.string :as string]))

(defn create-dot-node
  "Create dot node from a map M :name, :defined-by and :ns key"
  [m]
  ;; object "ns-set <<clojure.core/def>>" as object_ns_set_clojure_core_def {
  ;; dummy is a very long text 1 
  ;; 3 4. with new lines and all
  ;; testi ng}
  (let [dn (str (m :name))
        ds (last (string/split (name (m :defined-by)) #"/"))
        node (str dn " <<" ds ">>")
        pi (m-wonky-hash (str (m :name) "_" (m :ns)))
        doc (m :doc)]
    (when (not (= "declare" ds))
      (str pi
           "[\n"
           "id=" pi "\n"
;           "rankdir=" \u0022 "LR" \u0022 "\n"
           ;"label=" \u0022 "{" (xml-escape node) "|"(when doc (label-escape doc)) "|}" \u0022 "\n"
           "shape=plaintext\n"
           "label=<<TABLE BGCOLOR=" \u0022 "lightyellow"\u0022 " CELLSPACING="\u0022"0"\u0022"><TR><TD>" (xml-escape node) "</TD></TR><TR><TD>" (when doc (br-align (xml-escape doc))) "</TD></TR></TABLE>>\n"
;           "style=filled\n"
;           "fillcolor=lightyellow\n"
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

(defn create-ns-dot-node
  "Create dot node from namespace-definitions analysis map M"
  [m]
  (let [dn (str (m :name))
        pi (m-wonky-hash dn)
        doc (m :doc)]
    (str pi
         "[\n"
         "id=" pi "\n"
         "label=" \u0022 (xml-escape dn) (when doc (str "\ndoc: " (xml-escape doc) "\n")) \u0022
         "];\n")))

(comment
  (create-ns-dot-node '{:col 1
                        :doc "ns test doc-string"
                        :end-col 42
                        :end-row 6
                        :filename
                        "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/writer/dotwriter.clj"
                        :name cljviz.writer.dotwriter
                        :name-col 5
                        :name-end-col 28
                        :name-end-row 1
                        :name-row 1
                        :row 1})
  (create-ns-dot-node '  {:col 1
                          :end-col 50
                          :end-row 8
                          :filename
                          "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/util/alephws.clj"
                          :name cljviz.util.alephws
                          :name-col 5
                          :name-end-col 24
                          :name-end-row 1
                          :name-row 1
                          :row 1})
  )

(defn create-dot-subgraph
  "Create dot subgraph from vector V
   with first :ns and second map of vars"
  [v]
  (let [i (name (first v))
        ri (graph-escape i)
        m (second v)]
    (str "subgraph cluster_" ri " {" "\n" 
         (apply str (into #{} (map #(create-dot-node %) m)))
         "\n label=" \u0022 (xml-escape i) \u0022 "}" "\n")))

(comment
  (map #(create-dot-subgraph %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))))))


(defn create-dot-links 
  "Provides dot relationship section links
   based on map M :from-var :from :name and :to values"
  [m]
  (let [frnname (m :from-var)
        frnnamens (m :from)
        tn (m :name)
        tns (m :to)]
    (when (and tn tns frnname frnnamens)
      (let [fr-name (str frnname "_" frnnamens)
            to-name (str tn "_" tns)
            fr (m-wonky-hash fr-name)
            to (m-wonky-hash to-name)]
        (when (some? to-name) (vector (sorted-map :name fr-name :hash fr) (sorted-map :name to-name :hash to)))))))

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

(defn create-ns-dot-links
  "Provides dot relationship section links
   based on map M :from and :to values"  [m]
  (let [frnnamens (m :from)
        tons (m :to)]
    (when (and tons frnnamens)
      (let  [fr (m-wonky-hash (str frnnamens))
             to (m-wonky-hash (str tons))]
        (when (some? to) (vector (sorted-map :name frnnamens :hash fr) (sorted-map :name tons :hash to)))))))

(defn write-edge
  "vector input V,
   [[from-map {:name :hash} to-map{:name :hash}] freq]"
  [V]
  (let [[ft f] V
        [from-e to-e] ft]
    (str (:hash from-e) "->" (:hash to-e)
         " [penwidth=" f
         "; color=" \u0022 (rand-color) \u0022
         "; label=" f
         "; tooltip=" \u0022 (str (:name from-e) "->" (:name to-e)) \u0022 "] \n")))


(defn main-dot-writer 
  "Main writer for graphviz output" 
  [f]
  (let [ma (:analysis (run-lint-analysis f))
        m-d (:var-definitions ma)
        m-u (:var-usages ma)]
    (do
      (str "digraph " (graph-escape (last (string/split f #"/")))
           "{\nrankdir=" \u0022 "TB" \u0022 "\n"
           (apply str (map #(create-dot-subgraph %) (group-by :ns (filter-var-def-keys m-d))))
      ;    edge [penwidth=1; color="#40e0d0"] node1 -> node2
      ;    TO-DO use thread-macro (or rewrite)
           (apply str
                  (map #(write-edge %)
                       (frequencies
                        (filter identity (map #(create-dot-links %)
                                              (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))))
           (str "}\n")))))

(defn ns-dot-writer 
  "Writer for namespace graphviz output"
  [f]
  (let [n-s (:analysis (run-ns-analysis f))
        n-d (:namespace-definitions n-s)
        n-u (:namespace-usages n-s)]
    (do
      (str "digraph " (graph-escape (last (string/split f #"/")))
           "{\nrankdir=" \u0022 "TB" \u0022 "\n"
           (apply str (map #(create-ns-dot-node %) n-d))
           (apply str 
                  (map #(write-edge %)
                       (frequencies
                        (filter identity (map #(create-ns-dot-links %)
                                              (filter identity (map #(filter-usage-ns-defs % n-d) n-u)))))))
           "}\n"))))

(comment
  (println (ns-dot-writer "/home/juhakairamo/Projects/clojure/cljviz/src/"))
  (println (main-dot-writer "/home/juhakairamo/Projects/clojure/cljviz/src/"))
  (def test-nd (:namespace-definitions (:analysis (run-ns-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/"))))
  (test-nd)
  (def test-nu (:namespace-usages  (:analysis (run-ns-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/"))))
  (test-nu)
  (map #(filter-usage-ns-defs % test-nd) test-nu)
  (map #(create-dot-links %) (filter identity (map #(filter-usage-ns-defs % test-nd) test-nu)))
  )