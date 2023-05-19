(ns cljviz.util.alephws
  (:require [aleph.http :as http]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [clojure-watch.core :as w]
            [manifold.deferred :as d]
            [manifold.stream :as s]
            [reitit.ring :as ring]
            [ring.middleware.params :as params]
            [ring.util.response :as r]))

(def non-websocket-request
  {:status 400
   :headers {"content-type" "application/text"}
   :body "Expected a websocket request."})


(defn start-ui "handler for loading ui code
(https://github.com/hpcc-systems/hpcc-js-wasm)" 
  [req]
  (let [body (str "<!DOCTYPE html>
  <html>
    <body>
    <div>
    <button id=\"nav-toggle-button\" type=\"button\" onclick=\"toggleVisibility()\">click me </button>
    </div>
    <div id=\"placeholder\">
    <script type=\"module\">
                   import { Graphviz } from \"https://cdn.jsdelivr.net/npm/@hpcc-js/wasm/dist/graphviz.js\";

          const graphviz = await Graphviz.load();
//const svg = graphviz.dot('digraph { a -> b }');
          const div = document.getElementById(\"placeholder\");
          fetch('http://localhost:3000/start')
                .then(response => response.text())
                .then(data => {
                 div.innerHTML = graphviz.layout(data, \"svg\", \"dot\");
          })
          .catch(error => {
             console.error(error);
          })
    </script>
    </div>

    <script type=\"module\">
    import { Graphviz } from \"https://cdn.jsdelivr.net/npm/@hpcc-js/wasm/dist/graphviz.js\";

    const graphviz = await Graphviz.load();
    const eventSource = new WebSocket('ws://localhost:3000/ws');
    let dot_string = '';  
    eventSource.onmessage = event => {
      dot_string = event.data;
      console.log(\"got dot-string: \" + dot_string.length)
      const svg = graphviz.dot(dot_string);
      const div = document.getElementById(\"placeholder\");
      div.innerHTML = graphviz.layout(dot_string, \"svg\", \"dot\");
     }
      </script>

   <script>
            function toggleVisibility () {
                var elems = document.getElementsByClassName(\"node\");
                console.log(elems);
                [].forEach.call(elems, function(a){
                    console.log(a.getElementsByTagName(\"polygon\"));
                    const polygon = a.getElementsByTagName(\"polygon\")[0];
//                    [].forEach.call(polygons, function(b){
//                        b.setAttribute(visibility,\"hidden\");
                        console.log(polygon.attributes);
                        polygon.setAttribute(\"visibility\", \"hidden\");
//                   });
                });
            }
          </script> 
    </body>
  </html>")]
    {:status 200
     :headers {"content-type" "text/html"}
     :body body}))
  

(comment
  (defn newtest [])
  (apply str (main-dot-writer "/home/juhakairamo/Projects/clojure/cljviz/src"))
  {:text (apply str "tic" + " tac")}
  )
  
(defn start-dot "" [req f]
  (let [dot (main-dot-writer f)]
    {:status 200
     :headers {"content-type" "text/plain"}
     :body dot})
    )
    
(defn wrap-start " " [handler f]
  (fn [req]
    (let [response (handler req f)]
      response)))

(def conns (atom []))

(defn ws-handler
  [req]
  (d/let-flow [conn (d/catch
                     (http/websocket-connection req)
                     (fn [_] nil))]
              (if-not conn
                non-websocket-request
                (d/let-flow [] (
                                (swap! conns conj conn)
;;                                (s/put! conn (main-dot-writer f))
                                (s/connect conn conn))
                               nil))))

(defn send-all "send msg to all ws channels" [msg]
  (doseq [conn @conns]
    (s/put! conn msg)))

(defn w-send-all "send-all wrapper" [_ _ d]
  (send-all (main-dot-writer d)))

(defn watch-src "watches changes in src dir D" [d]
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
                         ["/ws" ws-handler]])
                       (ring/create-default-handler))) {:port 3000}))

(comment
  (def ts (start-ws "/home/juhakairamo/Projects/clojure/cljviz/src"))
  (watch-src "/home/juhakairamo/Projects/clojure/cljviz/src")
  (.close ts)
  (def newas (start-ws "/home/juhakairamo/Projects/clojure/cljviz/src")))
  

;; Here we `put!` ten messages to the server, and read them back again
;; (let [conn @(http/websocket-client "ws://localhost:10000/echo")]

;;   (s/put-all! conn
;;               (->> 10 range (map str)))

;;   (->> conn
;;        (s/transform (take 10))
;;        s/stream->seq
;;        doall))    ;=> ("0" "1" "2" "3" "4" "5" "6" "7" "8" "9")

;; ;; Here we create two clients, and have them speak to each other
;; (let [conn1 @(http/websocket-client "ws://localhost:10000/chat")
;;       conn2 @(http/websocket-client "ws://localhost:10000/chat")]

;;   ;; sign our two users in
;;   (s/put-all! conn1 ["shoes and ships" "Alice"])
;;   (s/put-all! conn2 ["shoes and ships" "Bob"])

;;   (s/put! conn1 "hello")

;;   (println @(s/take! conn1))                                  ;=> "Alice: hello"
;;   (println @(s/take! conn2))                                  ;=> "Alice: hello"

;;   (s/put! conn2 "hi!")

;;   (println @(s/take! conn1))                                  ;=> "Bob: hi!"
;;   (println @(s/take! conn2)))                                 ;=> "Bob: hi!"

;;(.close s)
