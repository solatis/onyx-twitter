## onyx-twitter

Onyx plugin for twitter. **docs incomming**

#### Installation

In your project file:

```clojure
[onyx-twitter "0.9.0.0"]
```

In your peer boot-up namespace:

```clojure
(:require [onyx.plugin.twitter])
```

#### Task Bundle

`onyx.tasks.twitter/stream`

| key                          | value               | description  |
|----------------------------- | --------------------|--------------|
| :twitter/consumer-key        | String              | API key      |
| :twitter/consumer-secret     | String              | API key      |
| :twitter/access-token        | String              | API key      |
| :twitter/access-secret       | String              | API key      |
| :twitter/keep-keys           | [Any]               | Keys to keep in the tweet map after deconstructing the POJO tweet. Defaults to [:id :lang :text]|

#### Contributing

Pull requests into the master branch are welcomed.

#### License

Copyright © 2015 FIX ME

Distributed under the Eclipse Public License, the same as Clojure.
