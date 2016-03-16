(defproject onyx-twitter "0.8.10.0-SNAPSHOT"
  :description "Onyx plugin for twitter"
  :url "FIX ME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.onyxplatform/onyx "0.9.0-alpha12"]
                 [org.twitter4j/twitter4j-core "4.0.4"]
                 [org.twitter4j/twitter4j-stream "4.0.4"]
                 [org.clojure/core.async "0.2.374"]
                 [aero "0.2.0"]]
  :profiles {:dev {:dependencies []
                   :plugins []}})
