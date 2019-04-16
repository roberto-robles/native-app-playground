(ns native-app-playground.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
  :get-username
  (fn [db _]
    (:username db)))

(reg-sub
  :get-password
  (fn [db _]
    (:password db)))

(reg-sub
  :get-date
  (fn [db _]
    (:date db)))

(reg-sub
  :get-starter
  (fn [db _]
    (:starter db)))

(reg-sub
  :get-percentage
  (fn [db _]
    (:percentage db)))


;; View management
(reg-sub
  :get-current-view
  (fn [db _]
    (:view db)))

