(ns hello.handler)

(gen-class
  :name "hello.handler"
  :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])

(defn -handleRequest [this in out ctx]
  (println "Hello!"))
