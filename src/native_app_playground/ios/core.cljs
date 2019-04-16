(ns native-app-playground.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync reg-event-fx]]
            [native-app-playground.events]
            [native-app-playground.subs]
            [native-app-playground.shared.components :as ui]
            ))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))

(def logo-img (js/require "./images/cljs.png"))

(def views-list [{:label "Login" :view :login :key "login"}
                 {:label "List" :view :list :key "list"}
                 {:label "DateTimePicker iOS" :view :datepicker-ios :key "datepicker-ios"}
                 {:label "Form Controls" :view :form :key "form"}
                 {:label "Web View" :view :web :key "web"}
                 {:label "Animated Bounce View" :view :animated-bounce :key "animated-view"}
                 {:label "Animated Left->Right " :view :animated-left-right :key "animated-left-right"}
                 {:label "Animated Up->Down " :view :animated-up-down :key "animated-up-down"}
                 {:label "Animated Right->Left " :view :animated-right-left :key "animated-right-left"}
                 {:label "default" :view :default :key "default"}])

(defn alert [title]
      (.alert (.-Alert ReactNative) title))


(defmulti get-view (fn [view]
                     view))

(defmethod get-view :animated-bounce []
  [ui/animated-bounce-view])

(defmethod get-view :animated-left-right []
  [ui/animated-left-right-view])

(defmethod get-view :animated-up-down []
  [ui/animated-up-down-view])

(defmethod get-view :animated-right-left []
  [ui/animated-right-left-view])


(defmethod get-view :login []
  (let [greeting (subscribe [:get-greeting])
        username (subscribe [:get-username])
        password (subscribe [:get-password])
        text (r/adapt-react-class (.-Text ReactNative))
        view (r/adapt-react-class (.-View ReactNative))
        image (r/adapt-react-class (.-Image ReactNative))
        touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative))
        text-input (r/adapt-react-class (.-TextInput ReactNative))
        current-view (subscribe [:get-current-view])
        temp-password (r/atom "")]
    [view {:style {:flex-direction "column" :margin 40 :align-items "stretch" :flex 1 :justify-content "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} (name @current-view)]
       ;; Image reference example
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       ;; Text input example
     [ui/editable-text
      @username
      "Username"
      :update-username
      {:style-wrapper {:border-bottom-width 0}
       :style-input {:font-size 20 }}]
     [ui/editable-text
      @password
      "Password"
      :update-password
      {:style-wrapper {:border-bottom-width 0}
       :style-input {:font-size 20 }}]
     ;; Button with event example
       [touchable-highlight {:style {:background-color "#000" :padding 10 :border-radius 5}
                             :on-press (fn []
                                         #_(alert (str "Hello " @username))
                                         (dispatch [:set-view :list]))}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Login"]]]))

(defmethod get-view :list []
  (let [list-view (r/adapt-react-class (.-FlatList ReactNative))
        view (r/adapt-react-class (.-View ReactNative))
        text (r/adapt-react-class (.-Text ReactNative))
        current-view (subscribe [:get-current-view])]

    [view {:style {:flex-direction "column" :margin 40 :align-items "stretch" :flex 1 :justify-content "center"}}
       [text (str "Kitchen Sink app:" (name @current-view))  ]
       [list-view {:data views-list
                   :render-item (fn [elem]
                                  (let [label (aget elem "item" "label") ;;(.-label (.-item elem)) 
                                        as-view (.-view (.-item elem))]
                                    (r/as-element 
                                     [text {:style {:font-size 30 :font-weight "400" :margin-bottom 5}
                                            :on-press (fn []
                                                        #_(alert as-view)
                                                        (dispatch-sync [:set-view (keyword as-view)]))}  label])))}]]))



(defmethod get-view :form []
  (let [list-view (r/adapt-react-class (.-FlatList ReactNative))
        view (r/adapt-react-class (.-View ReactNative))
        my-date (subscribe [:get-date])
        starter (subscribe [:get-starter])
        percentage (subscribe [:get-percentage])]

    [view {:style ui/form-container-style}
     [ui/editable-date-component @my-date "My Date" :set-date]
     [ui/editable-choice-component @starter "Choose your pokemon: " :set-starter]
     [ui/editable-slider-component @percentage "Choose Percentage: " :set-percentage]]))

(defmethod get-view :web []
  (let [view (r/adapt-react-class (.-View ReactNative))
        text (r/adapt-react-class (.-Text ReactNative))
        webview (r/adapt-react-class (.-WebView ReactNative))]

    [webview {:source {:url "https://github.com/roberto-robles"} :style {:height "50%"}}]))

(defmethod get-view :snapshot-view []
  (let [view (r/adapt-react-class (.-View ReactNative))
        text (r/adapt-react-class (.-Text ReactNative))
        snapshot-view (r/adapt-react-class (.-SnapshotViewIOS ReactNative))]

    [snapshot-view ]))
 
(defmethod get-view :default []
  (get-view :form))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])
        username (subscribe [:get-username]) 
        text (r/adapt-react-class (.-Text ReactNative))
        view (r/adapt-react-class (.-View ReactNative))
        image (r/adapt-react-class (.-Image ReactNative))
        touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative))
        dummy-input (r/adapt-react-class (.-TextInput ReactNative))
        current-view (subscribe [:get-current-view])
        view-text (name :key)]
    (fn []
      [view {:style {:flex-direction "column" :align-items "stretch" :flex 1 :background-color "#FFFFFF"}}
       [view {:style {:justify-content "center" :flex-direction "row" :padding-top 35 :flex 0.05 :background-color "#330033" :height 50}}
        [text {:style {:color "white" :font-size 30}
               :on-press (fn []
                           #_(alert (str "Hello " @username))
                           (dispatch [:set-view :list]))} "Kitchen Sink"]]
       [text (str @current-view)]
       (get-view @current-view)]
      )))  

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "NativeAppPlayground" #(r/reactify-component app-root)))
