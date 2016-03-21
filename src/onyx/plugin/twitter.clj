(ns onyx.plugin.twitter
  (:require [clojure.core.async :refer [<!! >!! chan close!]]
            [onyx.plugin simple-input
             [buffered-reader :as buffered-reader]])
  (:import [twitter4j Status StatusListener TwitterStream TwitterStreamFactory StatusJSONImpl]
           [twitter4j.conf Configuration ConfigurationBuilder]))

(defn config-with-password ^Configuration [consumer-key consumer-secret
                                           access-token access-secret]
  "Build a twitter4j configuration object with a username/password pair"
  (.build (doto  (ConfigurationBuilder.)
            (.setOAuthConsumerKey consumer-key)
            (.setOAuthConsumerSecret consumer-secret)
            (.setOAuthAccessToken access-token)
            (.setOAuthAccessTokenSecret access-secret))))

(defn status-listener [cb]
  "Implementation of twitter4j's StatusListener interface"
  (proxy [StatusListener] []
    (onStatus [^twitter4j.Status status]
      (cb status))
    (onException [^java.lang.Exception e] (.printStackTrace e))
    (onDeletionNotice [^twitter4j.StatusDeletionNotice statusDeletionNotice])
    (onScrubGeo [userId upToStatusId] ())
    (onTrackLimitationNotice [numberOfLimitedStatuses]
      (println numberOfLimitedStatuses))))

(defn get-twitter-stream ^TwitterStream [config]
  (let [factory (TwitterStreamFactory. ^Configuration config)]
    (.getInstance factory)))

(defn add-stream-callback! [stream cb]
  (let [tc (chan 1000)]
    (.addListener stream (status-listener cb))
    (.sample stream)))

(defmacro safeget [f obj]
  `(try (~f ~obj) (catch NullPointerException e# nil)))

(defn tweetobj->map [^StatusJSONImpl tweet-obj]
  (try {:contributors (let [x (safeget .getContributors tweet-obj)]
                        (vec x))
        :created-at (safeget .getCreatedAt tweet-obj)
        :geolocation (when-let [x (safeget .getGeoLocation tweet-obj)]
                       {:latitude (safeget .getLatitude x)
                        :longitude (safeget .getLongitude x)})
        :id (safeget .getId tweet-obj)
        :in-reply-to-screen-name (safeget .getInReplyToScreenName tweet-obj)
        :in-reply-to-status-id (safeget .getInReplyToStatusId tweet-obj)
        :in-reply-to-user-id (safeget .getInReplyToUserId tweet-obj)
        :retweet-count (safeget .getRetweetCount tweet-obj)
        :text (safeget .getText tweet-obj)
        :place (when-let [x (.getPlace tweet-obj)]
                 {:country (safeget .getCountry x)
                  :country-code (safeget .getCountryCode x)})
        :user (when-let [x (safeget .getUser tweet-obj)]
                {:created-at (safeget .getCreatedAt x)
                 :description (safeget .getDescription x)
                 :favourites-count (safeget .getFavouritesCount x)
                 :followers-count (safeget .getFollowersCount x)
                 :friends-count (safeget .getFriendsCount x)
                 :id (safeget .getId x)
                 :location (safeget .getLocation x)
                 :name (safeget .getName x)
                 :screen-name (safeget .getScreenName x)
                 :geo-enabled? (safeget .isGeoEnabled x)})}
       (catch Exception e
         {:error e})))

(defrecord ConsumeTweets [event task-map segment]
  onyx.plugin.simple-input/SimpleInput
  (start [this]
    (let [{:keys [twitter/consumer-key
                  twitter/consumer-secret
                  twitter/access-token
                  twitter/access-secret]} (:task-map this)
          configuration (config-with-password consumer-key consumer-secret
                                              access-token access-secret)
          twitter-stream (get-twitter-stream configuration)
          twitter-feed-ch (chan 1000)]
      (add-stream-callback! twitter-stream (fn [m] (>!! twitter-feed-ch m)))
      (assoc this
             :twitter-stream twitter-stream
             :twitter-feed-ch twitter-feed-ch)))
  (stop [{:keys [twitter-stream twitter-feed-ch]}]
    (do (close! twitter-feed-ch)
        (.shutdown ^TwitterStream twitter-stream)
        (.cleanUp  ^TwitterStream twitter-stream)))
  (checkpoint [this]
    -1)
  (segment-id[this]
    -1)
  (segment [this]
    segment)
  (next-state [{:keys [twitter-feed-ch] :as this}]
    (assoc this :segment {:tweet (tweetobj->map (<!! twitter-feed-ch))}))
  (recover [this offset]
    this)
  (checkpoint-ack [this offset]
    this)
  (segment-complete! [this segment]))

(defn consume-tweets [{:keys [onyx.core/task-map] :as event}]
  (map->ConsumeTweets {:event event
                       :task-map task-map}))

(def twitter-reader-calls
  {:lifecycle/before-task-start (fn [event lifecycle]
                                  (let [plugin (get-in event [:onyx.core/task-map :onyx/plugin])]
                                    (case plugin
                                      :onyx.plugin.buffered-reader/new-buffered-input
                                      (buffered-reader/inject-buffered-reader event lifecycle))))
   :lifecycle/after-task-stop (fn [event lifecycle]
                                (let [plugin (get-in event [:onyx.core/task-map :onyx/plugin])]
                                  (case plugin
                                    :onyx.plugin.buffered-reader/new-buffered-input
                                    (buffered-reader/close-buffered-reader event lifecycle))))})
