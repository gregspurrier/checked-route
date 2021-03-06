(ns checked-route.core)

(defn accept-anything
  [x]
  nil)

;; ## Default Bindings for Overridable Behavior
(def ^:dynamic *default-check-spec*
  "The default check specification for each route argument. These are
overriden with metadata attached to the individual arguments in the
route definition."
  {:pre-check-xform identity
   :check            accept-anything
   :post-check-xform identity
   :required         false})

(def ^:dynamic *missing-required-argument-error*
  "The error value to associate with a required argument in the errors
map when the argument is missing."
  "required but missing")

(def ^:dynamic *make-error-response*
  "A function that creates a Ring response from a map of argument symbols
to their associated errors."
  (fn [errors]
    {:status 400
     :body {:errors errors}}))

;; ## The `checked` Macro and Associated Support Code
(defn- ^:testable bound-variables
  "Extract the symbols that are bound by the provided route args
destructuring form."
  [args]
  (->> (tree-seq coll? seq args)
       (filter symbol?)
       (remove (partial = '&))))

(defn- check-spec
  [sym]
  (merge *default-check-spec* (meta sym)))

(defn- args->syms-and-checks
  "Converts a Compojure destructuring args form into a seq of [symbol,
  check-spec] pairs."
  [args]
  (map (juxt identity check-spec)
       (bound-variables args)))

(defn- xform-binding-forms
  [selector syms-and-checks]
  (mapcat (fn [[sym spec]]
            [sym `(~(selector spec) ~sym)])
          syms-and-checks))

(defn- pre-check-binding-forms
  [syms-and-checks]
  (xform-binding-forms :pre-check-xform syms-and-checks))

(defn- post-check-binding-forms
  [syms-and-checks]
  (xform-binding-forms :post-check-xform syms-and-checks))

(defn check-parameter
  [param-value check-fn required]
  (if param-value
    (check-fn param-value)
    (if required
      *missing-required-argument-error*)))

(defn- check-form
  [syms-and-checks]
  `(->> [~@(map (fn [[sym spec]]
                  `['~sym (check-parameter ~sym
                                           ~(:check spec)
                                           ~(:required spec))])
                syms-and-checks)]
        (remove (comp empty? second))
        (into {})))

(defmacro checked
  [verb path args & body]
  (let [syms-and-checks (args->syms-and-checks args)
        error-map-sym (gensym)]
    (if (seq syms-and-checks)
      `(~verb ~path ~args
              (let [~@(pre-check-binding-forms syms-and-checks)]
                (let [~error-map-sym ~(check-form syms-and-checks)]
                  (if (empty? ~error-map-sym)
                    (let [~@(post-check-binding-forms syms-and-checks)]
                      ~@body)
                    (*make-error-response* ~error-map-sym)))))
      `(~verb ~path ~args ~@body))))
