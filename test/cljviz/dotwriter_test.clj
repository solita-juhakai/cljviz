(ns cljviz.dotwriter-test 
  (:require [cljviz.writer.dotwriter :refer [create-dot-links create-dot-node
                                             create-ns-dot-node]]
            [clojure.test :refer [deftest is run-tests testing]]))

((deftest create-dot-node-test
   (testing "Testing create-dot-node from dotwriter.clj"
             (is 
              (=
               "A819714924[\nid=A819714924\nshape=plaintext\ntooltip=\"tes-t\"\nlabel=<<TABLE BGCOLOR=\"lightyellow\" CELLSPACING=\"0\"><TR><TD>tes-t &lt;&lt;defn&gt;&gt;</TD></TR><TR><TD>foo</TD></TR></TABLE>>\n];\n"
               (create-dot-node '{:name "tes-t"
                                  :doc "foo"
                                  :defined-by "clojure.core/defn"
                                  :ns "cljvis.core"}))))))

((deftest create-ns-dot-node-test
      (testing "Testing create-ns-dot-node from dotwriter.clj"
        (is 
         (=
          "453867576[\nid=453867576\ntooltip=\"cljviz.writer.dotwriter\"\nlabel=\"cljviz.writer.dotwriter\ndoc: ns test doc-string\n\"];\n"
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
                                :row 1}))))))

((deftest create-dot-links-test
   (testing "Testing create-dot-links from dotwriter.clj"
     (is
      (=
       '[{:hash "A180623784", :name "filter-from-vars", :ns "cljviz.core"} {:hash "A765940430", :name "tes-t", :ns "cljviz.core"}]
       (create-dot-links '{:end-row 102,
                           :name-end-col 28,
                           :name-end-row 102,
                           :name-row 102,
                           :name "tes-t",
                           :filename "/home/juhakairamo/Projects/clojure/cljviz/src/cljviz/core.clj",
                           :from "cljviz.core",
                           :col 26,
                           :name-col 26,
                           :from-var "filter-from-vars",
                           :end-col 28,
                           :row 102,
                           :to "cljviz.core"}))))))

 (comment
   (run-tests)
          (vector
    {:hash "A765940430", :name "tes-t", :ns "cljviz.core"} {:hash "A180623784", :name "filter-from-vars", :ns "cljviz.core"})
   )
