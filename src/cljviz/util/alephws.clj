(ns cljviz.util.alephws
  (:require [aleph.http :as http]
            [cljviz.writer.dotwriter :refer [main-dot-writer ns-dot-writer]]
            [clojure-watch.core :as w]
            [manifold.deferred :as d]
            [manifold.stream :as s]
            [reitit.ring :as ring]
            [ring.middleware.params :as params]
            [hiccup.core :as h]))

(def non-websocket-request
  {:status 400
   :headers {"content-type" "application/text"}
   :body "Expected a websocket request."})


(defn start-ui "handler for loading ui code to draw svg,
using (https://github.com/hpcc-systems/hpcc-js-wasm)" 
  [req]
  (let [body (h/html
               [:body
                [:details
                 [:summary
                  [:div#nsplace]]
                 [:div#placeholder]
                 ]
                [:script {:type :module}
                 "import { Graphviz } from \"https://cdn.jsdelivr.net/npm/@hpcc-js/wasm/dist/graphviz.js\";
      const graphviz = await Graphviz.load();
      //const svg = graphviz.dot('digraph { a -> b }');
      var div = document.getElementById(\"placeholder\");
        fetch('/start')
         .then(response => response.text())
         .then(data => {
           div.innerHTML = graphviz.layout(data, \"svg\", \"dot\");
         })
        .catch(error => {
           console.error(error);
        })
                   

      var nsdiv = document.getElementById(\"nsplace\");
        fetch('/ns')
         .then(response => response.text())
         .then(data => {
           nsdiv.innerHTML = graphviz.layout(data, \"svg\", \"dot\");
         })
        .catch(error => {
           console.error(error);
        })
      
      const eventSource = new WebSocket('ws://localhost:3000/ws');
      let dot_string = '';  
      eventSource.onmessage = event => {
        dot_string = event.data;
        console.log(\"got dot-string: \" + dot_string.length)
        const div = document.getElementById(\"placeholder\");
        div.innerHTML = graphviz.layout(dot_string, \"svg\", \"dot\");
      };"]])]
    {:status 200
     :headers {"content-type" "text/html"}
     :body body}))
  

(defn start-dot "creates start-up handler which
returns dot format presentation of
input clj source folder F"
  [req f]
  (let [dot (main-dot-writer f)]
    {:status 200
     :headers {"content-type" "text/plain"}
     :body dot})
    )

(defn start-ns-dot "creates start-up handler which
returns dot format presentation (ns-view) of
input clj source folder F"
  [req f]
  (let [dot (ns-dot-writer f)]
    {:status 200
     :headers {"content-type" "text/plain"}
     :body dot}))

(defn wrap-start "wraps ring handler HANDLER 
to be able to pass more arguments F"
  [handler f]
  (fn [req]
    (let [response (handler req f)]
      response)))

(def conns (atom #{}))

(defn ws-handler 
  "websocket handler, storing connections to atom conns"
  [req]
  (d/let-flow [conn (d/catch
                     (http/websocket-connection req)
                     (fn [_] nil))]
              (if-not conn
                non-websocket-request
                ((swap! conns conj conn)
                 (s/connect conn conn)))
              nil))

(defn send-all
  "send msg  MSG to all ws channels
based on atom conns"
  [msg]
  (doseq [conn @conns]
    (s/put! conn msg)))

(defn w-send-all
  "wrapper for send-all to send
dot updates to connections in conns"
  [_ _ d]
  (send-all (main-dot-writer d)))

(defn watch-src
  "watches changes in src dir D
and sends dot updates to channels"
  [d]
  (w/start-watch [{:path d
                   :event-types [:create :modify :delete]
                   :bootstrap (fn [d] (send-all (main-dot-writer d)))
                   :callback (fn [ev f] (w-send-all ev f d))
                   :options {:recursive true}}]))

;; (def handler
;;   (params/wrap-params
;;    (ring/ring-handler
;;     (ring/router
;;      [["/start" start-page]
;;       ["/ws" ws-handler]])
;;     (ring/create-default-handler))))

(defn start-ws
  "start websocket server" 
  [f]
  (http/start-server (params/wrap-params
                      (ring/ring-handler
                       (ring/router
                        [["/ui" start-ui]
                         ["/start" (wrap-start start-dot f)]
                         ["/ws" ws-handler]
                         ["/ns" (wrap-start start-ns-dot f)]])
                       (ring/create-default-handler))) {:port 3000}))

(comment
  (def ts (start-ws "/home/juhakairamo/Projects/clojure/cljviz/src"))
  (watch-src "/home/juhakairamo/Projects/clojure/cljviz/src")
  (.close ts)
  (send-all (main-dot-writer "/home/juhakairamo/Projects/clojure/cljviz/src"))
  )
