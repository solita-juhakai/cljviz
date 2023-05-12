(ns cljviz.util.alephws
  (:require [aleph.http :as http]
            [cljviz.writer.dotwriter :refer [main-dot-writer]]
            [clojure-watch.core :as w]
            [manifold.deferred :as d]
            [manifold.stream :as s]
            [reitit.ring :as ring]
            [ring.middleware.params :as params]))

(def non-websocket-request
  {:status 400
   :headers {"content-type" "application/text"}
   :body "Expected a websocket request."})


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
                                (s/connect conn conn)
                                )nil))))

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

(def handler
  (params/wrap-params
   (ring/ring-handler
    (ring/router
     [["/ws" ws-handler]])
    (ring/create-default-handler))))

(defn start-ws "start websocket server" []
  (http/start-server handler {:port 3000}))

(comment
  (def ts (start-ws))
  (watch-src "/home/juhakairamo/Projects/clojure/cljviz/src")
  (.close ts)
  (def newt-tasausat (start-ws))
  )

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
