(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.java.shell :refer [sh]]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file "target/java/lib/remote-nrepl.jar")

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :manifest {"Premain-Class" "remote_nrepl.agent"}})
  (sh "zip" "-r" "layer.zip" "java/lib" :dir "target"))
