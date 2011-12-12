(defproject clojure-python "0.1.0"
  :description "Improve seamlessness of Clojure Jython interop."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.python/jython-standalone "2.5.2"]]
  :dev-dependencies [[midje "1.3.0-RC4"]]
  :main clojure-python.core)