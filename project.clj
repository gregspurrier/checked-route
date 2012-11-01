(defproject checked-route "0.1.0"
  :description "Type-checked routes for Compojure"
  :url "https://github.com/gregspurrier/checked-route"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/mit-license.php"
            :distribution :repo}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.4.0"]
                                  [midje "1.4.0"]
                                  [compojure "1.1.3"]]}}
  :min-lein-version "2.0.0")
