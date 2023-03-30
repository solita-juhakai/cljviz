(ns cljviz.util.http
  (:require [cljviz.util.dotsh :refer [create-svg]]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "image/svg+xml" "Vary" "Accept-Encoding"}
;;   :body (:out create-svg (main-dot-writer f))})
   })

(defn mw-response-body [handler f]
  (fn [request]
    (let [response (handler request)]
      (assoc response :body f)
    )))

(defn start-http [f] 
  (let [my-body (:out (create-svg (main-dot-writer f)))]
    (run-jetty
     (-> handler
         (mw-response-body my-body))
     {:port 3000})))

(comment
  (:out (create-svg (main-dot-writer " test")))
  (start-http "../xtdb-inspector/src")
  )
