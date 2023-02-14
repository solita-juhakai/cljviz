(ns cljviz.core
  (:require [clj-kondo.core :as kondo]
            [clojure.string :as string])
  (:gen-class))

;;(def ol "object links map as {:ns {:var-name \"object-link\"}}" (atom {}))

;; (comment
;;   ((reset! ol {})
;;    (swap! ol deep-merge {(keyword (name "test-ns")) {(keyword "test-name") "link-value"}})
;;    (swap! ol deep-merge @ol {:test-ns {:test-name-2 "link-value"}})
;;    (swap! ol deep-merge @ol {:other-ns {:other-name "other-lv"}})
;;    (:test-name (:test-ns @ol))))
  
;;https://github.com/clj-kondo/clj-kondo/tree/master/analysis
(defn run-lint-analysis "runs clj-kondo linter with analysis on FILE f" [f]
  (kondo/run! {:lint (list f)
               :config {:analysis true
                        :output {:format :edn}}}))

(comment
  (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (group-by :ns (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (group-by :ns [{:ns 1 :a 2} {:ns 4 :c 45} {:ns 1 :b 2} {:ns 3 :a 2}]))

(defn filter-var-def-keys "Filter :ns :name :defined-by :doc fields from lint analysis :var-definitions map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m))
   

(comment 
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  

(defn wonky-hash "Makes a string hash of input string, if initial chr is '-' replace with A " [i]
  (string/replace (str (.hashCode i)) #"^-" "A"))

(def m-wonky-hash (memoize wonky-hash))
  

(comment
  (wonky-hash "read_input_clojure_core_defn_")
  (m-wonky-hash (wonky-hash "read_input_clojure_core_defn_"))
  (m-wonky-hash (wonky-hash "read_input_clojure_core_defn_"))
  )
  

(defn create-plantuml-object "Create plantuml object from a map M :name, :defined-by and :ns key, store object hash to map ol" [m]
  ;; object "ns-set <<clojure.core/def>>" as object_ns_set_clojure_core_def {
  ;; dummy is a very long text 1 
  ;; 3 4. with new lines and all
  ;; testi ng}
  (let [i (str (m :name) " <<" (m :defined-by) ">>")
        pi (wonky-hash (str (m :name) "_" (m :ns)))
        doc (m :doc)]
;;    (swap! ol deep-merge @ol {(keyword (m :ns)) {(keyword (m :name)) pi}})
    (str "object " \u0022 i \u0022 " as " pi (when doc (str " {\n" doc "\n}")) "\n")))
  

(comment
;;  (reset! ol {})
  (map #(apply println (create-plantuml-object %)) (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))
  (create-plantuml-object {:name "tes-t" :doc "foo" :defined-by "clojure.core/defn" :ns "cljvis.core"})
  ;;(@ol)
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
  (wonky-hash (str (t-m :name) "_" (t-m :ns)))
  )
  

(defn create-pl-ob-package "Create plantuml package section from vector V with first :ns and second map of vars" [v]
  (let [i (name (first v))
        m (second v)]
    (str "package " i " {" "\n" (apply str (into #{} (map #(create-plantuml-object %) m))) " }" "\n")))

(comment
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
 ;; (@ol)
  ,,,)
  

(defn filter-from-vars "filters maps with key :from-var from :var-usages map in kondo map M" [m]
  (filter identity (map #(when (:from-var %) %) (m :var-usages))))


(defn multi-pred [coll m]
  ;; select elements using reduce-kv & filter
  ;; https://clojuredocs.org/clojure.core/reduce-kv
  (reduce-kv
   (fn [erg k v] (filter #(= v (k %)) erg)) coll m))

(defn filter-usage-var-defs "filter maps from :var-usages that have (and (and :from :from-var) (and :ns :to) in :var-definitions m-d" [m m-d]
  (let [a (multi-pred m-d {:name (m :from-var) :ns (m :from)})
        b (multi-pred m-d {:name (m :name) :ns (m :to)})]
    (when (and (when (seq a) a) (when (seq b) b)) m)))

(comment
  (map #(:from-var %) (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))
  (def v-d (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (v-d) 
  (def v-u (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))) 
  (v-u)
  (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src")))
  (def m-pred '{:from-var create-pl-ob-package :from cljviz.core :to cljviz.core :name create-plantuml-object})
  (multi-pred v-u m-pred)
  (def test-m ' {:col 28
                 :end-col 38
                 :end-row 37
                 :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"
                 :fixed-arities #{1}
                 :from cljviz.core
                 :from-var m-wonky-hash-
                 :name wonky-hash
                 :name-col 28
                 :name-end-col 38
                 :name-end-row 37
                 :name-row 37
                 :row 37
                 :to cljviz.core})
  (test-m)
  (seq '())
  (filter-usage-var-defs test-m v-d)
  )

  
(defn rand-hex []
  ;;(format "%05d" (rand-int 256))
  ;;(format (Integer/toHexString (rand-int 256)))
  (string/replace (format "%2S" (Integer/toHexString (rand-int 256))) #"[ ]" "0")
  )

(defn rand-color []
  (str "#" (string/join (repeatedly 3 rand-hex))))

(comment
  (str "#" (string/join (repeatedly 3 rand-hex)))
  (string/replace (format "%2S" (Integer/toHexString (rand-int 10))) #"[ ]" "0")
  (rand-color)
  (format "%1S" "15a")
  )

(defn create-pl-links "Creates Plantuml arrow section from object names stored in atom map ol based on map M values" [m]
  (let [frnname (m :from-var)
        frnnamens (m :from)
        tn (m :name)
        tns (m :to)]
    (when (and tn tns frnname frnnamens)
      (let [fr (wonky-hash (str frnname "_" frnnamens))
            to (wonky-hash (str tn "_" tns))]
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
  (def l-m '{:arity 1
             :col 23
             :end-col 44
             :end-row 156
             :filename "./src/cljviz/core.clj"
             :fixed-arities #{1}
             :from cljviz.core
             :from-var -main
             :name run-lint-analysis
             :name-col 24
             :name-end-col 41
             :name-end-row 156
             :name-row 156
             :row 156
             :to cljviz.core})
  (:from-var l-m)
  (wonky-hash (str (l-m :from-var) "_" (l-m :from)))
  (wonky-hash (str (l-m :name) "_" (l-m :to)))
  (map #(create-pl-links %) (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  ;; (reset! ol {})
  ;; (empty? @ol)
  (some? nil)
  (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/aoc2022/src"))))))
;;  (@ol)
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
  "Cljviz turns clj-kondo analysis output to plantuml diagram. Input argument clj-file or directory, output plantuml description."
  [& args]
  (let [f (first args)
        ma (:analysis (run-lint-analysis f))
        m-d (:var-definitions ma)
        m-u (:var-usages ma)]
       (if f 
         (do
           (println "@startuml" f)
           (apply println (map #(create-pl-ob-package %) (group-by :ns (filter-var-def-keys m-d))))
           (apply println 
                  (map #(str (first (nth % 0)) "-[" (rand-color) ",thickness=" (nth % 1) "]->" (second (nth % 0)) ": " (nth % 1) "\n") (frequencies (filter identity (map #(create-pl-links %) (filter identity (map #(filter-usage-var-defs % m-d) m-u)))))))
           (println "@enduml"))
         (println "Need an input clj-file or directory"))))

(comment
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")
  (-main "/home/juhakairamo/Projects/clojure/cljviz/src")
  (-main "/home/juhakairamo/Projects/clojure/aoc2022/src")
  (-main "/home/juhakairamo/Projects/clojure/xtdb-inspector/src")
  (-main "/home/juhakairamo/Projects/clojure/tab/src")
  (-main)
  ;; (reset! ol {})
  ;; (@ol)
  (map #(str (nth % 0) ": " (nth % 1)) (frequencies (filter identity (map #(create-pl-links %) (filter-from-vars (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/xtdb-inspector/src")))))))
  (vals {"test" 2 "test2" 4})
  (def m-d-t (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (map #(filter-usage-var-defs % m-d-t) (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj" ))))
  )
  