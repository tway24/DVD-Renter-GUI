(ns video.features
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [video.load :refer :all]
            ))

;;-------------------------------------------------------------------------------
;;----------------         UPDATE MOVIE DB        -------------------------------
;;-------------------------------------------------------------------------------

;;Add new copies
(defn add-copy [movie new-qnt]
  (let [id (get-movie-id movie)
        update-qnt (+ new-qnt (:qnt (@movie-atom id)))]
    (swap! movie-atom assoc-in [id :qnt] update-qnt)
    ;;TODO: Write in another thread
    (writer movie-path @movie-atom)))

;;Add new movies
(defn add-movie [movie qnt price]
  (let [id (inc (get-last-id))]
    (swap! movie-atom assoc id {:name movie :qnt qnt :price price })
    ;;TODO: Write in another thread
    (writer movie-path @movie-atom)))

;; TODO: if qnt is already 0 raise alert/exception
(defn remove-movie [movie]
  "Removes one copy. If movie to be removed, then returns true else false."
  (let [id (get-movie-id movie)
        not-rented (is-not-rented movie)]
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
;; TODO: If qnt=0 and still call this Function, then raise alert
(defn rent-movie [movie renter]
  (let []
    (add-copy movie -1)
    (swap! rent-atom assoc {:movie movie :renter renter} (due-date))
    (writer rent-path @rent-atom)
    ))

(defn return-movie [movie renter]
  (let []
    (add-copy movie 1)
    (swap! rent-atom dissoc {:movie movie :renter renter})
    (writer rent-path @rent-atom)))













