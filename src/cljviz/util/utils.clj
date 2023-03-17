(ns cljviz.util.utils 
  (:require [cljviz.util.lint :refer [run-lint-analysis]]
            [clojure.string :as string]))

(defn filter-var-def-keys "Filter :ns :name :defined-by :doc fields from lint analysis :var-definitions map M" [m]
  (map #(select-keys % [:ns :name :defined-by :doc]) m))

(comment
  (filter-var-def-keys (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj")))))


(defn wonky-hash "Makes a string hash of input string. If result's first chr is '-' replace with A " [i]
  (string/replace (str (.hashCode i)) #"^-" "A"))

(def m-wonky-hash "memoize wonky-hash" (memoize wonky-hash))

(comment
  (wonky-hash "read_input_clojure_core_defn")
  (m-wonky-hash "read_input_clojure_core_defn"))

(defn multi-pred "filter items from COLL with M key value pairs" [coll m]
  ;; select elements using reduce-kv & filter
  ;; https://clojuredocs.org/clojure.core/reduce-kv
  (reduce-kv
   (fn [erg k v] (filter #(= v (k %)) erg)) coll m))

(defn filter-usage-var-defs "filter maps from :var-usages that have both (and :from :from-var) (and :ns :to) in :var-definitions m-d" [m m-d]
  (let [a (multi-pred m-d {:name (m :from-var) :ns (m :from)})
        b (multi-pred m-d {:name (m :name) :ns (m :to)})]
    (when (and (when (seq a) a) (when (seq b) b)) m)))

(comment
  (def v-d (:var-definitions (:analysis (run-lint-analysis "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj"))))
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
                :from cljviz.core
                :from-var m-wonky-hash
                :name wonky-hash
                :name-col 28
                :name-end-col 38
                :name-end-row 37
                :name-row 37
                :row 37
                :to cljviz.core})
  (filter-usage-var-defs test-m v-d))

(defn rand-hex "Creates random 2 hex digits, pad with zero" []
  (string/replace (format "%2S" (Integer/toHexString (rand-int 256))) #"[ ]" "0"))

(defn rand-color "Creates random 6 digit hex color code" []
  (str "#" (string/join (repeatedly 3 rand-hex))))

(comment
  (str "#" (string/join (repeatedly 3 rand-hex)))
  (string/replace (format "%2S" (Integer/toHexString (rand-int 256))) #"[ ]" "0")
  (rand-color)
  (format "%1S" "15a"))