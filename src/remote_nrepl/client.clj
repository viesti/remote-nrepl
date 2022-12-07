(ns remote-nrepl.client
  (:require [clojure.java.io :as io]
            [nrepl.server :as nrepl]))

(def nrepl-port 4444)

(def server-volatile (volatile! nil))

(defn start [proxy-host proxy-port]
  (locking server-volatile
    (let [server @server-volatile]
      (when-not server
        (let [nrepl-server (nrepl/start-server #_#_#_#_:bind "127.0.0.1" :port nrepl-port
                                               :socket "/tmp/nrepl.sock")
              _ (do
                  (Thread/sleep 5000)
                  (println "nrepl server started"))
              client (future
                       (let [#_#_local (java.net.Socket. "localhost" nrepl-port)
                             local (doto (java.net.Socket.)
                                     (.connect (java.net.UnixDomainSocketAddress/of "/tmp/nrepl.sock")))
                             remote (java.net.Socket. proxy-host proxy-port)
                             lin (.getInputStream local)
                             lout (.getOutputStream local)
                             rin (.getInputStream remote)
                             rout (.getOutputStream remote)

                             #_#_remote-debug (java.net.Socket. proxy-host (inc proxy-port))
                             #_#_rd-in (.getInputStream remote-debug)]
                         (future
                           (io/copy rin lout)
                           (println "rin lout done"))
                         (future
                           (io/copy lin rout)
                           (println "lin rout done"))
                         #_(future
                           (with-open [rdr (io/reader rd-in)]
                             (loop [line (.readLine rdr)]
                               (when-not (.startsWith line "exit")
                                 (println line)
                                 (recur (.readLine rdr)))))
                           (println "debug done"))))]
          (vreset! server-volatile {:nrepl-server nrepl-server
                                    :client client}))))))
