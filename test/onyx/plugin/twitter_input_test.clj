(ns onyx.plugin.twitter-input-test
  (:require [aero.core :refer [read-config]]
            [clojure.core.async :refer [<!!]]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]
            [onyx api
             [job :refer [add-task]]
             [test-helper :refer [validate-enough-peers! with-test-env]]]
            [onyx.plugin.core-async :as core-async]
            [onyx.plugin.twitter]
            [onyx.tasks
             [core-async :as async-task]
             [twitter :as twitter]]))

(defn build-job [twitter-config batch-size batch-timeout]
  (let [batch-settings {:onyx/batch-size batch-size :onyx/batch-timeout batch-timeout}
        base-job (merge {:workflow [[:in :out]]
                         :catalog []
                         :lifecycles []
                         :windows []
                         :triggers []
                         :flow-conditions []
                         :task-scheduler :onyx.task-scheduler/balanced})]
    (-> base-job
        (add-task (twitter/stream :in (merge twitter-config batch-settings)))
        (add-task (async-task/output :out batch-settings)))))

(deftest get-tweet-test
  (testing "We can get tweets"
    (let [{:keys [env-config peer-config twitter-config]}
          (read-config (io/resource "config.edn") {:profile :test})
          job (build-job twitter-config 2 1000)
          {:keys [out]} (core-async/get-core-async-channels job)]
      (with-test-env [test-env [6 env-config peer-config]]
        (validate-enough-peers! test-env job)
        (onyx.api/submit-job peer-config job)
        (clojure.pprint/pprint (<!! out))
        (is (<!! out))))))
