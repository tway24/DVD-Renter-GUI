

;Working Code/Meeting Requirements  28/30
;Add new movies
;Remove a movie in the inventory
;Rent a movie in the inventory
;Keep track of the movies that are rented
;Add new copies of existing movies
;Change the price of a movie
;Find the price and/or quantity of a movie by either name or id. - no -2
;Keep data in memory

;Show a list of available movies, which includes its name, price, and quantity
;Show a list of rented movies, which includes its name, renter and date due
;Select an available movie and rent it
;Select an rented movie and return it to the available movies
;Select an available movie and remove it from the inventory

;Clojure & Functional constructs 20/20

;Quality of Code                 9/10

; long functions in gui.clj

;Total 		57/60


(ns video.core
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [video.dbfn :refer :all]
            [video.gui :refer :all]
            [video.features :refer :all]))

;; Files
;;   1. dbfn.clj : database functions file
;;                 read movie-inventory and renter-data functions
;;                 update the same
;;                 write to file
;;   2. features.clj : Contains all top-level database functions
;;   3. gui_lib.clj : GUI basic functions
;;   4. gui.clj  : Top GUI functions

;; README
;; Edit - This menu contains actions for:
;;            1. Add movie
;;            2. Add copy
;;            3. Change price
;;
;; For "Add copy" and "Change price" movie needs to be selected from table
;; Find - This menu is to find any movie and show quantity and/or price
;;      - Appropriate check-box needs to be selected
;;
;; Movie's tab contains:
;;           1. inventory listing
;;           2. Rent-button
;;           3. Remove-button
;;
;; To remove/rent, first movie needs to be selected
;;
;; Renter's tab contains:
;;           1. current renting listing
;;           2. Return-button
;;
;; To return, first movie needs to be selected

(defn -main
  "Video store main function"
  [& args]
  (start-gui))

(-main)
