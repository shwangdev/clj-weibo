# clj-weibo

This Clojure library was designed to provide APIs for the third part applications to access sina weibo. It was flexble for developers to extend their requirements.

##Author

* Author : Xiang Wang (wxjeacen@gmail.com)
* Date :  2014.08.21

## Usage

```clojure
    (use '[clj-weibo core oauth2])
    (def my-weibo-config (merge weibo-default-config
                                {:client-id "Your App ID"
                                 :client-secret "Your App Secret"} ))


    ;; Get oauth2 authentication
    (request-auth-url my-weibo-config)
    ;; here will generate a url link for user to get oauth2 authentication.

    ;; set your oauth2 token
    (weibo-set-token "Oauth2 Token")

    ;; refer the API
    (weibo-wrap-api {:method :get
                     :api-version 2
                     :api-group :statuses
                     :api-key :public_timeline
                     :data {:count 10}} my-weibo-config)

    ;; Another example

    (-> (weibo-wrap-api {:method :post
                          :api-version 2
                          :api-group :statuses
                          :api-key :update
                          :data {:status
                                (url-encode "Sent from Clojure Weibo API from Xiang Wang !") }}
                        my-weibo-config)
        (get "text"))
```


## License

Copyright © 2014

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
