(defproject cljviz "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/test.check "1.1.1"]
                 [clj-kondo "2022.12.11-SNAPSHOT"]
                 [ring/ring "1.9.6"]
;;                 [http-kit "2.6.0"]
                 [aleph "0.6.1"]
                 [manifold "0.4.0"]
                 [metosin/reitit "0.6.0"]
                 [clojure-watch "0.1.14"]]
  :main ^:skip-aot cljviz.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[com.github.clj-kondo/lein-clj-kondo "0.2.4"]])
