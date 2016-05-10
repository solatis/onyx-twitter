(ns onyx.tasks.twitter
  (:require [schema.core :as s]
            [onyx.schema :as os]))

(def TwitterStreamTaskMap
  {:twitter/consumer-key s/Str
   :twitter/consumer-secret s/Str
   :twitter/access-token s/Str
   :twitter/access-secret s/Str
   (os/restricted-ns :twitter) s/Any})

(s/defn stream
  ([task-name :- s/Keyword opts]
   {:task {:task-map (merge
                      {:onyx/name task-name
                       :onyx/plugin :onyx.plugin.buffered-reader/new-buffered-input
                       :simple-input/build-input :onyx.plugin.twitter/consume-tweets
                       :onyx/type :input
                       :onyx/medium :twitter}
                      opts)
           :lifecycles [{:lifecycle/task task-name
                         :lifecycle/calls :onyx.plugin.twitter/twitter-reader-calls}]}
    :schema {:task-map TwitterStreamTaskMap}})
  ([task-name :- s/Keyword
    consumer-key :- s/Str
    consumer-secret :- s/Str
    access-token :- s/Str
    access-secret :- s/Str
    task-opts :- {s/Any s/Any}]
   (stream task-name (merge {:twitter/consumer-key consumer-key
                             :twitter/consumer-secret consumer-secret
                             :twitter/access-token access-token
                             :twitter/access-secret access-secret}
                            task-opts))))
