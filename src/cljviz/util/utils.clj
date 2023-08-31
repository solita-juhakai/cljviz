(ns cljviz.util.utils 
  (:require [cljviz.util.lint :refer [run-lint-analysis run-ns-analysis]]
            [clojure.string :as string]))

(defn filter-var-def-keys 
  "Filter :ns :name :defined-by :doc fields
   from lint analysis :var-definitions map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m))

(comment
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (filter-var-def-keys (vector (first (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))))


(defn wonky-hash 
  "Makes a string hash of input string.
   If result's first chr is '-' replace with A "
  [i]
  (string/replace (str (hash i)) #"^-" "A"))

(def m-wonky-hash "memoize wonky-hash" (memoize wonky-hash))

(comment
  (wonky-hash "read_input_clojure_core_defn")
  (m-wonky-hash "read_input_clojure_core_defn"))

(defn multi-pred
  "filter items from COLL with M key value pairs" [coll m]
  ;; select elements using reduce-kv & filter
  ;; https://clojuredocs.org/clojure.core/reduce-kv
  (reduce-kv
   (fn [erg k v] (filter #(= v (k %)) erg)) coll m))

(defn filter-usage-var-defs 
  "filter maps from :var-usages that have both 
   (and :from :from-var) (and :to :ns) in :var-definitions m-d"
  [m m-d]
  (let [a (multi-pred m-d {:name (m :from-var) :ns (m :from)})
        b (multi-pred m-d {:name (m :name) :ns (m :to)})]
    (when (and (when (seq a) a) (when (seq b) b)) m)))

(comment
  (def v-d (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src"))))
  (v-d)
  (def v-u (:var-usages (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
  (v-u)
  (def m-pred '{:from-var create-pl-ob-package :from cljviz.core :to cljviz.core :name create-plantuml-object})
  (multi-pred v-u m-pred)
  (def test-m '{:col 28
                :end-col 38
                :end-row 37
                :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"
                :fixed-arities #{1}
                :from cljviz.util.utils
                :from-var m-wonky-hash
                :name wonky-hash
                :name-col 28
                :name-end-col 38
                :name-end-row 37
                :name-row 37
                :row 37
                :to cljviz.util.utils})
  (filter-usage-var-defs test-m v-d))

(defn filter-usage-ns-defs
  "filter maps M from :namespace-usages that have both
   :from :to keys in :namespace-definitions"
  [m n-d]
  (let [a (multi-pred n-d {:name (m :from)})
        b (multi-pred n-d {:name (m :to)})]
    (when (and (when (seq a) a) (when (seq b) b)) m)))

(comment
  (def n-d (:namespace-definitions (:analysis (run-ns-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/"))))
  (n-d)
  (def n-u (:namespace-usages (:analysis (run-ns-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/"))))
  (def test-ns '  {:alias string
                   :alias-col 33
                   :alias-end-col 39
                   :alias-end-row 3
                   :alias-row 3
                   :col 14
                   :filename
                   "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/util/utils.clj"
                   :from cljviz.util.utils
                   :name-col 14
                   :name-end-col 28
                   :name-end-row 3
                   :name-row 3
                   :row 3
                   :to clojure.string})
  (filter-usage-ns-defs test-ns n-d)
  )


(defn rand-hex 
  "Creates random 2 hex digits, pad with zero"
  []
  (string/replace (format "%2S" (Integer/toHexString (rand-int 256))) #"[ ]" "0"))

(defn rand-color
  "Creates random 6 digit hex color code"
  []
  (str "#" (string/join (repeatedly 3 rand-hex))))

(comment
  (str "#" (string/join (repeatedly 3 rand-hex)))
  (string/replace (format "%2S" (Integer/toHexString (rand-int 256))) #"[ ]" "0")
  (rand-color)
  (format "%1S" "15a"))

(defn xml-escape 
  "escape XML special characters from string S" 
  [s] 
  ;; TO-DO ugly
  (-> s
      (string/replace "&" "&amp;")
      (string/replace "<" "&lt;")
      (string/replace ">" "&gt;")
      (string/replace "\"" "&quot;")
      (string/replace "'" "&apos;")))

(comment
  (xml-escape "3& fwepgqew    eeee \"\"\"")
  (-> "rt&9 < > \" fewe ''|"
      (string/replace "&" "&amp;")
      (string/replace "<" "&lt;")
      (string/replace ">" "&gt;")
      (string/replace "\"" "&quot;")
      (string/replace "'" "&apos;"))
  )


(defn graph-escape 
  "escapes graph chars for string S"
  [s]
  (-> s
      (string/replace #"\." "_")
      (string/replace "-" "_")))

(comment
  (graph-escape "...--test--te")
  )

(defn br-align
  "replace newlines in string S
   with <BR ALIGN=LEFT></BR>"
  [s]
  (-> s
   (string/replace "\n" "<BR ALIGN=\"LEFT\"></BR>")))