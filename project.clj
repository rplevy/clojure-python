(defproject clojure-python "0.4.2"
  :description "Improve seamlessness of Clojure Jython interop."
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.python/jython-standalone "2.7.0"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}}
  :plugins [[lein-midje "2.0.0"]])
