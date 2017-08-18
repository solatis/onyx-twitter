## onyx-twitter

Onyx plugin for Twitter.

#### Installation

In your project file:

```clojure
[org.onyxplatform/onyx-twitter "0.10.0"]
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
| :twitter/keep-keys           | [Any]               | Keys to keep in the tweet map after deconstructing the POJO tweet. Defaults to `[:id :lang :text]`. `:all` will keep all the tweet's keys|
| :twitter/track               | [String]              | An array of strings that you to track against the firehose.|

#### Contributing

Pull requests into the master branch are welcomed.

#### License

Copyright © 2016 Distributed Masonry

Distributed under the Eclipse Public License, the same as Clojure.
