(defproject clojure-python "0.4.1"
  :description "Improve seamlessness of Clojure Jython interop."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.python/jython-standalone "2.5.3"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}}
  :plugins [[lein-midje "2.0.0"]])
