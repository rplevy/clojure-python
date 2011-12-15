(ns clojure-python.core
  (:require (clojure [string :as s]))
  (:import (org.python.util PythonInterpreter)
           (org.python.core.*)))

(declare ^:dynamic *interp*)

(defn append-paths
  "appends a vector of paths to the python system path"
  [libpaths]
  (.exec *interp* "import sys")
  (doseq [p libpaths]
    (.exec *interp* (str "sys.path.append('" p "')")))
  *interp*)

(defn init
  "Establish a global python interpreter.
   The init function is only usefully called once.
   Alternatively, only use with-interpreter."
  [{:keys [libpaths] :as options}]
  (defonce ^:dynamic
    ^{:doc "root binding serves as global python interpreter"}
    *interp*
    (org.python.util.PythonInterpreter.))
  (append-paths libpaths))

(defmacro with-interpreter
  "Dynamically bind a new python interpreter for the calling context."
  [{:keys [libpaths] :as options} & body]
  `(binding [*interp* (org.python.util.PythonInterpreter.)]
     (append-paths ~libpaths)
     ~@body))

(defmacro py-import-lib
  "import lib
  defaults to use same name it has in python
  if it something like foo.bar, the name is bar."
  [lib & libs]
  (let [lib-sym (or (last libs) lib)
        lib-strs (map name (cons lib libs))
        py-name (s/join "." lib-strs)]
    `(do (.exec *interp* (str "import " ~py-name))
         (def ~lib-sym
              (-> *interp*
                  .getLocals
                  (.__getitem__ ~(first lib-strs))
                  ~@(map (fn [lib#] `(.__getattr__ ~lib#))
                         (next lib-strs))
                  .__dict__)))))

(defmacro py-import-obj
  "import objects from lib"
  [lib obj & objs]
  (cons 'do
        (map
         (fn [o#]
           `(def ~o# (.__finditem__ ~lib ~(name o#)))))
        (cons obj objs)))

(defmacro py-fn 
  "create a native clojure function applying the python 
  wrapper calls on a python function at the top level of the library
  use this where lambda is preferred over named function"
  [lib fun]
  `(let [f# (.__finditem__
             ~lib
             ~(name fun))]
     (fn [& args#]
       (call f# args#))))

(defmacro import-fn 
  "this is like import but it defines the imported item 
  as a native function that applies the python wrapper calls"
  [lib fun & funs]
  (cons 'do
        (map
         (fn [fun]
           `(def ~fun (py-fn ~lib ~fun)))
         (cons fun funs))))

(defmacro __
  "access attribute of class or attribute of attribute of (and so on) class"
  ([class attr]
     `(.__findattr__ ~class ~(name attr)))
  ([class attr & attrs]
     `(__ (__ ~class ~attr) ~@attrs)))

(defmacro _> 
  "call attribute as a method
  basic usage: (_> [class attrs ...] args ...)
  usage with keyword args: (_> [class attrs ...] args ... :key arg :key arg)
  keyword args must come after any non-keyword args"
  ([[class & attrs] & args]
     (let [keywords (map name (filter keyword? args))
           non-keywords (filter (fn [a] (not (keyword? a))) args)]
       `(call (__ ~class ~@attrs) [~@non-keywords] ~@keywords))))

(defn dir 
  "it's slightly nicer to call the dir method in this way"
  [x] (seq (.__dir__ x)))

(defn pyobj-nth
  "nth item in a 'PyObjectDerived'"
  [o i] (.__getitem__ o i))

(defn pyobj-range
  "access 'PyObjectDerived' items as non-lazy range"
  [o start end] (for [i (range start end)] (pyobj-nth o i)))

(defn pyobj-iterate
  "access 'PyObjectDerived' items as Lazy Seq"
  [pyobj] (lazy-seq (.__iter__ pyobj)))

(defn java2py
  "to wrap java objects for input as jython, and unwrap Jython output as java"
  [args]
  (into-array 
   org.python.core.PyObject 
   (map #(. org.python.core.Py java2py %) args)))

(defn call 
  "The first len(args)-len(keywords) members of args[] 
  are plain arguments. The last len(keywords) arguments
  are the values of the keyword arguments."
  [fun args & key-args]
  (.__tojava__
   (if key-args
     (.__call__ fun (java2py args) (into-array java.lang.String key-args))
     (.__call__ fun (java2py args)))
    Object))
