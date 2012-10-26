(defproject clojure-python "0.3.0"
  :description "Improve seamlessness of Clojure Jython interop."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.python/jython-standalone "2.5.2"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}}
  :plugins [[lein-midje "2.0.0"]])
