(ns checked-route.route
  (:require [checked-route.core :refer (checked)]
            [compojure.core :as compojure]))

;; ## Checked Route Generation
;; The following are the checked-route equivalents of the route macros
;; defined by compojure.core.
(defmacro GET
  "Generate a checked GET route."
  [path args & body]
  `(checked compojure/GET ~path ~args ~@body))

(defmacro POST
  "Generate a checked POST route."
  [path args & body]
  `(checked compojure/POST ~path ~args ~@body))

(defmacro PUT
  "Generate a checked PUT route."
  [path args & body]
  `(checked compojure/POST ~path ~args ~@body))

(defmacro DELETE
  "Generate a checked DELETE route."
  [path args & body]
  `(checked compojure/DELETE ~path ~args ~@body))

(defmacro HEAD
  "Generate a checked HEAD route."
  [path args & body]
  `(checked compojure/HEAD ~path ~args ~@body))

(defmacro OPTIONS
  "Generate a checked OPTIONS route."
  [path args & body]
  `(checked compojure/OPTIONS ~path ~args ~@body))

(defmacro PATCH
  "Generate a checked PATCH route."
  [path args & body]
  `(checked compojure/PATCH ~path ~args ~@body))

(defmacro ANY
  "Generate a checked route that matches any method."
  [path args & body]
  `(checked compojure/ANY ~path ~args ~@body))