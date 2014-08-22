(ns clj-weibo.core
  (:require [clj-weibo.oauth2 :as oauth2])
  (:require [clojure.string :as cstr])
  (:use [aleph http formats]
        [lamina core connections api]
        [cheshire core]))

(def all-scopes {:all "all"
                 :email "email"
                 :direct_messages_write "direct_messages_write"
                 :direct_messages_read "direct_messages_read"
                 :invitation_write "invitation_write"
                 :friendships_groups_read "friendships_groups_read"
                 :friendships_groups_write "friendships_groups_write"
                 :statuses_to_me_read "statuses_to_me_read"
                 :follow_app_official_microblog "follow_app_official_microblog"})

(def weibo-default-config {:host "https://api.weibo.com"
                           :endpoint-auth "/oauth2/authorize"
                           :endpoint-token "/oauth2/access_token"
                           :client-id "cliend_id"
                           :client-secret "client-secret"
                           :all-scopes all-scopes
                           :scopes [:all]
                           :redirect-uri "http://127.0.0.1/"
                           :response-type "code"})

;;(def my-weibo-config (merge weibo-default-config {:client-id "3675563603"
;;                                 :client-secret "20aa49fd22124720ef470624b3eb90da"}))

(def tokens (atom {}))

(defn weibo-set-token [config token]
  (reset! tokens (oauth2/get-tokens config token))
  )


(defn weibo-wrap-api [ params  config ]
  (let [{:keys [method api-version api-group api-key data]} params ]
    (oauth2/with-tokens at tokens config
      (case method
        :get (->> (sync-http-request {:method :get
                                      :url (str "https://api.weibo.com/" (str api-version) "/"
                                                (if (= nil api-group) ""
                                                    (str  (name api-group) "/"))
                                                (if (namespace api-key) (str (namespace api-key) "/" (name api-key))
                                                    (name api-key))
                                                ".json?access_token=" at "&"
                                                (when data
                                                  (cstr/join "&" (map #(str (name (key %)) "=" (val %)) data))
                                                  )
                                                )
                                      })
                  :body channel->lazy-seq (map bytes->string) (apply str) parse-string)
        :post (->> (sync-http-request {:method :post
                                       :url  (str "https://api.weibo.com/" (str api-version) "/"
                                                  (if (= nil api-group) ""
                                                      (str (name api-group) "/"))
                                                  (if (namespace api-key) (str (namespace api-key) "/" (name api-key))
                                                      (name api-key)) ".json")
                                       ;;:content-type "application/json; charset=UTF-8"
                                       :content-type "application/x-www-form-urlencoded"
                                       :body (cstr/join "&" (map #(str (name (key %)) "=" (val %))
                                                                 (merge data {:access_token at})) )}

                                      )
                   :body channel->lazy-seq (map bytes->string) (apply str) parse-string
                   )
        )


      ;;(->> (sync-http-request {:method method
      ;;                         :url url
      ;;                         })
      ;;     :body channel->lazy-seq ( map bytes->string) (apply str) parse-string
      ;;     )
      )
    )
  )
