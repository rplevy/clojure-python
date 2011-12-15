(ns clojure-python.t-core
  (:use midje.sweet)
  (:require [clojure-python.core :as base]))

(fact "append-paths adds the path to system path"
      (binding [base/*interp* (org.python.util.PythonInterpreter.)]
        (-> (#'base/append-paths ["test/clojure_python/"])
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


(defmacro with-test-interp [& body]
  `(base/with-interpreter
     {:libpaths ["test/clojure_python/"]}
     ~@body))

(fact "with-interpreter dynamically binds a new interpreter environment"
      (with-test-interp base/*interp*)
      =not=>
      (with-test-interp base/*interp*))

(fact "importing python modules works"
      (with-test-interp
        (base/py-import-lib example)
        (class example))
      =>
      org.python.core.PyStringMap)

(fact "importing python functions works"
      (with-test-interp
        (base/py-import-lib example)
        (base/import-fn example hello)
        (fn? hello))
      =>
      true)

(fact "calling python functions works"
      (with-test-interp
        (base/py-import-lib example)
        (base/import-fn example hello)
        (hello "world"))
      =>
      "hello, world how are you."

      (with-test-interp
        (base/py-import-lib example)
        ((base/py-fn example hello)
         "person"))
      =>
      "hello, person how are you.")
