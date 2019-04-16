(ns native-app-playground.shared.components
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync reg-event-fx]]
            [native-app-playground.events]
            [native-app-playground.subs]))


;; ===================================
;; ReactNative Components declaration
;; ===================================

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))

(def view (r/adapt-react-class (.-View ReactNative)))

(def datepicker (r/adapt-react-class (.-DatePickerIOS ReactNative)))

(def touchable (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def text-component (r/adapt-react-class (.-Text ReactNative)))


(def choice (r/adapt-react-class (.-Picker ReactNative)))

(def choice-item (r/adapt-react-class (.-Item (.-Picker ReactNative))))

(def slider (r/adapt-react-class (.-Slider ReactNative)))

(def text-input (r/adapt-react-class (.-TextInput ReactNative)))


;; ===================================
;; Default styles
;; ===================================


(def form-value-label-style
  {:font-size 18
   :font-weight "100"
   :line-height 30
   :text-align "left"
   :border-bottom-color "#DDDDDD"
   :border-bottom-width 10
   :border-style "solid"})

(def view-form-control-wrapper
  {:border-bottom-color "#3c3c3c"
   :border-bottom-width 1
   :border-style "solid"})

(def form-container-style
  {:flex-direction "column"
   :margin 40
   :align-items "stretch"
   :flex 1
   ;:justify-content "center"
   })

(def editable-text-style
  {:width "100%"
   :border-color "gray"
   :color "#777"
   :margin-bottom 20
   :text-align
   "center"})



;; ===================================================
;; Helper Fns probably better moved to utils or events
;; ===================================================

(defn dispatch-event [evt args]
  (if (keyword? evt)
    (dispatch [evt args])
    (apply evt args)))


;; =====================================
;; Reusable Components with Local state
;; =====================================


(defn editable-date-component
  ([value]
   (editable-date-component value "Date" :set-date))
  ([value label event]
   (let [editable? (r/atom false)]
     (fn [value label event]
       [view {:style view-form-control-wrapper}
        (if @editable?
         [datepicker {:on-date-change (fn [d]
                                        (reset! editable? false)
                                        (dispatch-sync [event d]))
                      :date value}]
         [touchable {:on-press #(reset! editable? true)}
          [text-component {:style form-value-label-style}
           (str label ": " (.toDateString value))]])]))))

(defn editable-choice-component [value label event]
  (let [editable? (r/atom false)]
    (fn [value label event]
      [view {:style view-form-control-wrapper}
       (if @editable?
         [choice {:style {:height 200 :width 200}
                  :selected-value value
                  :on-value-change (fn [v]
                                     (reset! editable? false)
                                     (dispatch-sync [event v]))}
          [choice-item {:label "Charmander" :value "charmander"}]
          [choice-item {:label "Squirtle" :value "squirtle"}]
          [choice-item {:label "Bulbasaur" :value "bulbasaur"}]]
         [touchable {:on-press #(reset! editable? true)}
          [text-component {:style form-value-label-style} (str label ": " value)]])])))

(defn editable-slider-component
  [value label event]
   (let [editable? (r/atom false)]
     (fn [value label event]
       [view {:style view-form-control-wrapper}
        (if @editable?
          [slider {:maximum-value 100
                   :minimum-value 0
                   :value value
                   :step 10
                   :on-sliding-complete (fn [v]
                                          (reset! editable? false)
                                          (dispatch-sync [event v]))}]
         [touchable {:on-press #(reset! editable? true)}
          [text-component {:style form-value-label-style}
           (str label ": " value)]])])))

(defn editable-text
  [value label event & options ]
  (fn [value label event {:keys [style-wrapper style-input]}]
    (let [style-text-view (merge  view-form-control-wrapper style-wrapper)
          style-input (merge editable-text-style style-input)]
      [view {:style style-text-view}
       [text-input {:value value
                    :placeholder (str label )
                    :style style-input
                    :editable true
                    :on-change-text #(dispatch-event event %)}]])))

(def animated (.-Animated ReactNative))
(def animated-value (.-Value animated))
(def animated-view (r/adapt-react-class (.-View animated)))


(defn animated-bounce-view []
    (r/create-class
      {:get-initial-state
       ;; instantiate a new Animated.Value class with 0 = completely hidden
        #(clj->js {:bounceValue (animated-value. 0)})
       :component-did-mount
        (fn [this]
          (.setValue (.. this -state -bounceValue) 1.5)
          (->
            ;; set the animation properties and start it off
            (.spring animated (.. this -state -bounceValue) #js {:toValue 0.8
                                                                 :friction 1})
            (.start)))
       :display-name "animated-view-impl"
       ;; render does not receive this as first argument
       ;; to get this, we need to use (reagent.core/current-component)
       :reagent-render
          (fn []
            (let [this (r/current-component)]
              [animated-view {:style {:flex 1
                                      :transform [{:scale (.. this -state -bounceValue)}]
                                      :backgroundColor "black"
                                      :borderRadius 10
                                      :margin 15
                                      :shadowColor "#000000"
                                      :shadowOpacity 0.7
                                      :shadowRadius 2
                                      :shadowOffset {:height 1 :width 0}}}
               [text-component {:style {:color "white"}} "This is a test"]]))}))


(defn animated-left-right-view []
    (r/create-class
      {:get-initial-state
       ;; instantiate a new Animated.Value class with 0 = completely hidden
       #(clj->js {:bounceValue (animated-value. -300)
                  ;:left (animated-value. 100)
                  })
       :component-did-mount
        (fn [this]
          (.setValue (.. this -state -bounceValue) -100)
          (->
            ;; set the animation properties and start it off
           (.spring animated (.. this -state -bounceValue) #js {:toValue 0
                                                                :friction 5})
           
           (.start)))
       :display-name "animated-left-right"
       ;; render does not receive this as first argument
       ;; to get this, we need to use (reagent.core/current-component)
       :reagent-render
          (fn []
            (let [this (r/current-component)]
              [animated-view {:style {:flex 1
                                        ;:transform [{:opacity (.. this -state -bounceValue)}]
                                      :left (.. this -state -bounceValue)
                                      :backgroundColor "black"
                                      ;; :borderRadius 10
                                      :margin 0
                                      :shadowColor "#333333"
                                      :shadowOpacity 0.7
                                      :shadowRadius 2
                                      :shadowOffset {:height 1 :width 0}}}
               [text-component {:style {:color "white"}} "This is a left to right"]]))}))

(defn animated-up-down-view []
    (r/create-class
      {:get-initial-state
       ;; instantiate a new Animated.Value class with 0 = completely hidden
       #(clj->js {:bounceValue (animated-value. 0)
                  ;:left (animated-value. 100)
                  })
       :component-did-mount
        (fn [this]
          (.setValue (.. this -state -bounceValue) 0)
          (->
            ;; set the animation properties and start it off
           (.decay animated (.. this -state -bounceValue) #js {:velocity 0.5
                                                               :toValue 0})
           
           (.start)))
       :display-name "animated-up-down"
       ;; render does not receive this as first argument
       ;; to get this, we need to use (reagent.core/current-component)
       :reagent-render
          (fn []
            (let [this (r/current-component)]
              [animated-view {:style {:flex 1
                                        ;:transform [{:opacity (.. this -state -bounceValue)}]
                                      :top (.. this -state -bounceValue)
                                      :backgroundColor "#330033"
                                      ;; :borderRadius 10
                                      :margin 0
                                      :shadowColor "#330033"
                                      :shadowOpacity 0.7
                                      :shadowRadius 2
                                      :shadowOffset {:height 1 :width 0}}}
               [text-component {:style {:color "white"}} "This is a up to down bounce"]]))}))

(defn animated-right-left-view []
    (r/create-class
      {:get-initial-state
       ;; instantiate a new Animated.Value class with 0 = completely hidden
       #(clj->js {:bounceValue (animated-value. -300)
                  ;:left (animated-value. 100)
                  })
       :component-did-mount
        (fn [this]
          (.setValue (.. this -state -bounceValue) -300)
          (->
            ;; set the animation properties and start it off
           (.timing animated (.. this -state -bounceValue) #js {:duration 500
                                                                :delay 0
                                                                :toValue 0})
           
           (.start)))
       :display-name "animated-right-left"
       ;; render does not receive this as first argument
       ;; to get this, we need to use (reagent.core/current-component)
       :reagent-render
          (fn []
            (let [this (r/current-component)]
              [animated-view {:style {:flex 1
                                        ;:transform [{:opacity (.. this -state -bounceValue)}]
                                      :right (.. this -state -bounceValue)
                                      :backgroundColor "#330033"
                                      ;; :borderRadius 10
                                      :margin 0
                                      :shadowColor "#330033"
                                      :shadowOpacity 0.7
                                      :shadowRadius 2
                                      :shadowOffset {:height 1 :width 0}}}
                [text-component {:style {:color "white"}} "This is a up to down bounce"]]))}))
