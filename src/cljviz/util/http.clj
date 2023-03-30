(ns cljviz.util.http
  (:require [cljviz.util.dotsh :refer [create-svg]]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "image/svg+xml" "Vary" "Accept-Encoding"}
;;   :body (:out create-svg (main-dot-writer f))})
   })

(defn mw-response-body [handler f]
  (fn [request]
    (let [response (handler request)]
;          my-body (:out (create-svg (main-dot-writer f)))]
      ;(println "my-body: " my-body)
      (assoc response :body f)
    
;      {:headers {"Content-Type" "image/svg+xml" "Vary" "Accept-Encoding"}
        ;:body (str (:out (create-svg (main-dot-writer f))))
        ;:body (:out (create-svg (main-dot-writer "test"))) 
       ;:body my-body
    ;   }
    )))

(defn start-http [f] 
  (let [my-body (:out (create-svg (main-dot-writer f)))]
    (println "f: " f)
    (run-jetty
     (-> handler
                          ;(mw-response-body (str (:out (create-svg (main-dot-writer f))))))
         (mw-response-body my-body))
     {:port 3000})))

(comment
  (:out (create-svg (main-dot-writer " test")))
  (start-http "./src")
  )
