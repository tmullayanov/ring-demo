(ns ring-demo.lib
  (:gen-class))

(defmacro safe [expr & {:keys [exp or] :or {exp Exception or nil}}]
  `(try
     ~expr
     (catch ~exp e# ~or)))
