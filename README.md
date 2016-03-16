## onyx-twitter

Onyx plugin for twitter.

#### Installation

In your project file:

```clojure
[onyx-twitter "0.8.0.0"]
```

In your peer boot-up namespace:

```clojure
(:require [onyx.plugin.twitter])
```

#### Functions

##### sample-entry

Catalog entry:

```clojure
{:onyx/name :entry-name
 :onyx/plugin :onyx.plugin.twitter/input
 :onyx/type :input
 :onyx/medium :twitter
 :onyx/batch-size batch-size
 :onyx/doc "Reads segments from twitter"}
```

Lifecycle entry:

```clojure
[{:lifecycle/task :your-task-name
  :lifecycle/calls :onyx.plugin.twitter/lifecycle-calls}]
```

#### Attributes

|key                           | type      | description
|------------------------------|-----------|------------
|`:twitter/attr`            | `string`  | Description here.

#### Contributing

Pull requests into the master branch are welcomed.

#### License

Copyright © 2015 FIX ME

Distributed under the Eclipse Public License, the same as Clojure.
