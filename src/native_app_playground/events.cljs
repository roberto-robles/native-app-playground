(ns native-app-playground.events
  (:require
   [re-frame.core :refer [reg-event-db after]]
   [clojure.spec.alpha :as s]
   [native-app-playground.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :set-greeting
 validate-spec
 (fn [db [_ value]]
   (assoc db :greeting value)))

;; Custom
(reg-event-db
 :update-username
 validate-spec
 (fn [db [_ username]]
   (assoc db :username username)))

(reg-event-db
 :update-password
 validate-spec
 (fn [db [_ password]]
   (assoc db :password password)))

(reg-event-db
 :set-date
 validate-spec
 (fn [db [_ date]]
   (assoc db :date date)))

(reg-event-db
 :set-starter
 validate-spec
 (fn [db [_ starter]]
   (assoc db :starter starter)))

(reg-event-db
 :set-percentage
 validate-spec
 (fn [db [_ percentage]]
   (assoc db :percentage percentage)))

(reg-event-db
 :set-view
 validate-spec
 (fn [db [_ view]]
   (assoc db :view view)))
