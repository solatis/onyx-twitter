(ns onyx.tasks.twitter
  (:require [schema.core :as s]
            [onyx.schema :as os]))

(s/defn input-task
  [task-name :- s/Keyword opts]
  {:task {:task-map (merge
                     {:onyx/name task-name
                      :onyx/plugin :onyx.plugin.buffered-reader/new-buffered-input
                      :simple-input/build-input :onyx.plugin.twitter/consume-tweets
                      :onyx/type :input
                      :onyx/medium :twitter}
                     opts)
          :lifecycles [{:lifecycle/task task-name
                        :lifecycle/calls :onyx.plugin.twitter/twitter-reader-calls}]}})
