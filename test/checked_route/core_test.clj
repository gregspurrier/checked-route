(ns checked-route.core-test
  (:use checked-route.core
        midje.sweet
        [midje.util :only (expose-testables)])
  (:require [compojure.core :refer [POST]]))

(expose-testables checked-route.core)

(facts "about bound-variables"
  (fact "handles all Compjure destructuring forms"
    (bound-variables '{{foo "foo"} :params})
    => '[foo]
    (bound-variables '[x y z])
    => '[x y z]
    (bound-variables '[x y & z])
    => '[x y z]
    (bound-variables '[x y :as r])
    => '[x y r]
    (bound-variables '[x y :as {u :uri}])
    => '[x y u])
  (fact "preserves symbol metadata"
    (meta (first (bound-variables '[^{:optional true} x])))
    => {:optional true}))
