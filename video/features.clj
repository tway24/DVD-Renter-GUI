(ns video.features
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [video.dbfn :refer :all]))

;;-------------------------------------------------------------------------------
;;----------------         UPDATE MOVIE DB        -------------------------------
;;-------------------------------------------------------------------------------

;;Add new copies
(defn add-copy [movie new-qnt]
  (let [id (get-movie-id movie)
        update-qnt (+ new-qnt (:qnt (@movie-atom id)))]
    (swap! movie-atom assoc-in [id :qnt] update-qnt)
    (writer movie-path @movie-atom)))

;;Add new movies
(defn add-movie [movie qnt price]
  (let [id (inc (get-last-id))]
    (swap! movie-atom assoc id {:name movie :qnt qnt :price price })
    (writer movie-path @movie-atom)))

(defn remove-movie [movie]
  "Returns true if movie to be removed from inventory itself, else false."
  (let [id (get-movie-id movie)
        not-rented (is-not-rented movie)]
    ;;reduce copies by 1
    (add-copy movie -1)
    (if (and not-rented (zero? (get-qnt id)))
      (do
        (swap! movie-atom dissoc id)
        (writer movie-path @movie-atom)
        true)
      false)))

(defn change-price [movie new-price]
  (let [id (get-movie-id movie)]
    (swap! movie-atom assoc-in [id :price] new-price)
    (writer movie-path @movie-atom)))

;;-------------------------------------------------------------------------------
;;----------------         UPDATE RENT DB        -------------------------------
;;-------------------------------------------------------------------------------

;; Rent a movie
(defn rent-movie [movie renter]
  (let []
    (add-copy movie -1)
    (swap! rent-atom assoc {:movie movie :renter renter} (due-date))
    (writer rent-path @rent-atom)))

(defn return-movie [movie renter]
  (let []
    (add-copy movie 1)
    (swap! rent-atom dissoc {:movie movie :renter renter})
    (writer rent-path @rent-atom)))













