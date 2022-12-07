(ns remote-nrepl.server
  (:require [clojure.java.io :as io]))

(defn start-proxy [remote-nrepl-port client-port]
  (let [remote-ss (java.net.ServerSocket. remote-nrepl-port)
        client-ss (java.net.ServerSocket. client-port)
        server (atom {:remote-ss remote-ss
                      :client-ss client-ss
                      :running? true
                      :remote nil
                      :client nil})]
    (add-watch server :shutdown (fn [_ _ _ {:keys [remote-ss client-ss running?]}]
                                  (when-not running?
                                    (println "Closing sockets")
                                    (try
                                      (.close remote-ss)
                                      (catch Exception _))
                                    (try
                                      (.close client-ss)
                                      (catch Exception _)))))
    (add-watch server :forward (fn [_ _ _ {:keys [remote client]}]
                                 (when (and remote client)
                                   (println "Forwarding client to remote")
                                   (let [cin (.getInputStream client)
                                         cout (.getOutputStream client)
                                         rin (.getInputStream remote)
                                         rout (.getOutputStream remote)]
                                     (future
                                       (io/copy rin cout))
                                     (future
                                       (io/copy cin rout))))))
    (future
      (while (:running? @server)
        (let [remote (.accept remote-ss)]
          (println "Remote connected")
          (swap! server assoc :remote remote))))
    (future
      (while (:running? @server)
        (let [client (.accept client-ss)]
          (println "Client connected")
          (swap! server assoc :client client))))
    server))

(defn stop-server [server])
