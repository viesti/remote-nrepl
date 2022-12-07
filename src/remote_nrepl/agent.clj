(ns remote-nrepl.agent
  (:require [remote-nrepl.client :as client]))

(gen-class
  :name remote_nrepl.agent
  :methods [^{:static true} [premain [String java.lang.instrument.Instrumentation] void]])

(defn -premain [_args _instrumentation]
  (let [proxy-host (System/getenv "NREPL_PROXY_HOST")
        proxy-port (when-let [proxy-port (System/getenv "NREPL_PROXY_PORT")]
                     (Long/parseLong proxy-port))]
    (when (and proxy-host proxy-port)
      (println "Connecting to remote nrepl proxy " proxy-host proxy-port)
      (client/start proxy-host proxy-port)))
  (println "hello from agent"))
