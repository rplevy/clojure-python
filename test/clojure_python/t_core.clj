(ns clojure-python.t-core
  (:use midje.sweet)
  (:require [clojure-python.core :as base]))

(fact "append-paths adds the path to system path"
      (binding [base/*interp* (org.python.util.PythonInterpreter.)]
        (-> (#'base/append-paths! ["test/clojure_python/"])
            .getLocals
            (.__getitem__ "sys")
            .path
            set
            (get "test/clojure_python/")))
      =>
      "test/clojure_python/")

(fact "init sets *interp* root binding (but only once)"
      (do
        (base/init {:libpaths ["test/clojure_python/"]})
        (class base/*interp*))
      =>
      org.python.util.PythonInterpreter

      (do
        (base/init {:libpaths ["test/clojure_python/"]})
        (class base/*interp*))
      =>
      (do
        (base/init {:libpaths ["test/clojure_python/"]})
        (class base/*interp*)))

(fact "with-interpreter dynamically binds a new interpreter environment"
      (base/with-interpreter
        {:libpaths ["test/clojure_python/"]}
        base/*interp*)
      =not=>
      (base/with-interpreter
        {:libpaths ["test/clojure_python/"]}
        base/*interp*))
