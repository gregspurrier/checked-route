(ns checked-route.core)

(defn accept-anything
  [x]
  nil)

(def ^:private default-metadata
  {:pre-check-xform  identity
   :check            accept-anything
   :post-check-xform identity
   :optional         false})

(defn- ^:testable bound-variables
  "Extract the symbols that are bound by the provided route args
destructuring form."
  [args]
  (->> (tree-seq coll? seq args)
       (filter symbol?)
       (remove (partial = '&))))

(defn- check-spec
  [sym]
  (merge default-metadata (meta sym)))

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

;; TODO - Add checks for required parameters
(defn- check-form
  [syms-and-checks]
  `(->> [~@(map (fn [[sym spec]]
                  `['~sym (~(:check spec) ~sym)])
                syms-and-checks)]
        (remove (comp empty? second))
        (into {})))

(defmacro checked
  [verb path args & body]
  (let [syms-and-checks (args->syms-and-checks args)
        error-map-sym (gensym)]
    `(~verb ~path ~args
            (let [~@(pre-check-binding-forms syms-and-checks)]
              (let [~error-map-sym ~(check-form syms-and-checks)]
                (if (empty? ~error-map-sym)
                  (let [~@(post-check-binding-forms syms-and-checks)]
                    ~@body)
                  {:status 400
                   :body {:errors ~error-map-sym}}))))))
