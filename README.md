# checked-route

`checked-route` allows you to check--e.g., against a schema defined
with [clj-schema](https://github.com/runa-dev/clj-schema)--Compojure
route parameters before handing them over to the route's body. Any check failures result in a 401 error being returned to the caller.

For example, consider this Compojure route that expects two parameters, `bar` and `baz`:

```clojure
(POST "/foo" [bar baz] (do-something bar baz))
```

If `bar` and `baz` have check functions `check-bar` and `check-baz`, respectively, that return nil on success and error message(s) on failure, then you can use `checked-route` to enforce them:

```clojure
(checked POST "/foo" [^{:check check-bar} bar ^{:check check-baz} baz]
  (do-something bar baz))
```

The only changes made to the original route were:

1. Prefixing the route form with `checked`
2. Adding metadata to `bar` and `baz` indicating which check functions to use

When no metadata is added to an argument, it will be considered required, but unchecked. To indicate that an argument is optional, tag it with the `:optional true` metadata. See the section on metadata below for more information.

## Installation via Leiningen
To use `checked-route`, add the following to the `:dependencies` section of your project.clj file:

```clojure
[checked-route "0.1.0-SNAPSHOT"]
```

then add the following require clause to your namespace declaration:

```clojure
(:require [checked-route.core :refer (checked)])
```

## Recognized Metadata
The following fields in an argument's metadata affect the checking process:

- `:check` -- a function of one argument that will be used to check the route argument. It should return nil when the argument is valid and an error result to return to the caller otherwise. If you are using `clj-schema`, you can use something like `(partial validation-errors my-schema)`
- `:pre-check-xform` -- a function of one argument that will be used to transform its value before checking is performed.
- `:pre-check-xform` -- a function of one argument that will be used to transform its value after successful checking is performed.

If pre- or post-check transform functions are provided, the argument's binding for the body of the route will reflect these transformations.

## License

Copyright (c) 2012 Greg Spurrier

Decomatic is distributed under the MIT license. Please see LICENSE.txt for the details.

