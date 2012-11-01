(ns checked-route.core-test
  (:use checked-route.core
        midje.sweet
        [midje.util :only (expose-testables)]))

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
    (meta (first (bound-variables '[^{:required true} x])))
    => {:required true}))

(unfinished check-fn)
(facts "about check-parameter"
  (check-parameter nil check-fn false)
  => nil

  (check-parameter nil check-fn true)
  => "required but missing"

  (check-parameter ..val.. check-fn false)
  => ..check-result..
  (provided (check-fn ..val..) => ..check-result..)

  (check-parameter ..val.. check-fn true)
  => ..check-result..
  (provided (check-fn ..val..) => ..check-result..))