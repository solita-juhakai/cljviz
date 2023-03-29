(ns cljviz.util.http
  (:require [cljviz.util.dotsh :refer [create-svg]]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "image/svg+xml" "Vary" "Accept-Encoding"}
;;   :body (:out create-svg (main-dot-writer f))})
   :body ""})

(defn mw-response-body [handler f]
  (fn [request] 
      (response (:out (create-svg (main-dot-writer f))))))

(defn start-http [f] (run-jetty 
                      (-> handler 
                          (mw-response-body f)) 
                      {:port 3000}))

(comment
  (:out (create-svg (main-dot-writer " test")))
  )
