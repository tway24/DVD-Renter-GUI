(ns video.core
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [video.load :refer :all]
            [video.gui :refer :all]
            [video.features :refer :all]))


;;Function declarations


;;TODO: what if files doesn't exist
;;TODO: what if user gives wrong movie-name, which doesn't exist?
;;TODO: Logging function to produce log of actions for debugging
;;TODO: Write a macro to do repititive tasks
;;TODO: Remove globlas: buttons
;;TODO: README
;;TODO: When movie is not selected -> Alert
;;TODO: Find price and quantity of movie by id
;;TODO: When someone 'cancel' action app shouldnt fail

;; README
;; Edit - This menu contains actions for:
;;            1. Add movie
;;            2. Add copy
;;            3. Change price
;;
;; For "Add copy" and "Change price" movie needs to be selected from table


(defn -main
  "Video store main function"
  [& args]
  (start-gui))







