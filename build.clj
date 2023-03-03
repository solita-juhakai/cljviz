(ns build
  (:require [clojure.tools.build.api :as b]))

(def app "cljviz")
(def version "0.1.0-SNAPSHOT")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" app version))
