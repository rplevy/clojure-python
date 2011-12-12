clojure-python is a library for Jython interop in Clojure.

Overview
========

Python libraries can be used in Clojure by instantiating 
a Jython interpreter via Clojure's excellent Java interop.
However, the way in which Python code is wrapped by Jython 
makes interop with Python clumsy and verbose. This library 
aims to make Jython interop in Clojure nearly as seamless 
as basic java interop.

Usage and Installation
======================

To include as a dependency:
--------------------------

Copy the config section found at http://clojars.org/clojure-python into your dependencies in your project's project.clj.

License
=======

Copyright (C) 2010-2012 Robert P. Levy

Distributed under LGPL.
