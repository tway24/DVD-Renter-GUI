(ns video.gui_lib
  (:gen-class)
  (:require [seesaw.core :as seesaw]
            [seesaw.swingx :as swingx]
            [seesaw.selection :as selection]
            [seesaw.table :as table]
            [video.dbfn :refer :all]
            [video.features :refer :all]))

(defn display [content width height ]
  (let [window (seesaw/frame
                :title "Video-store"
                :content content
                :width width
                :height height
                :id :main-window)]
(-> window seesaw/pack! seesaw/show!)))


;;Convetrs movie-db to vector for swingx/table-x
(defn get-movie-vector [movie-map]
  (vec (for [k (keys movie-map)]
         (vec (vals (movie-map k))))))

;;Show-movie-inventory
(defn show-inventory []
  (let [movie-map (get-movie-vector @movie-atom)
        title "Inventory"
        table (swingx/table-x
                :horizontal-scroll-enabled? true
                :model [:columns  [:Name :Quantity :Price]
                        :rows movie-map]
                :id :movie-table
                :selection-mode :single)]
    (seesaw/scrollable table)))

;;Convetrs renter-db to vector for swingx/table-x
(defn get-rent-vector [rent-map]
  (vec (for [k (keys rent-map)]
    (if (map? k)
      (conj (vec (vals k)) (rent-map k))
      (conj [] k (rent-map k))))))

;; Show renter data
(defn show-renter []
  (let [data @rent-atom
        rent-vector (get-rent-vector data)
        title "Renter's info"
        table (swingx/table-x
                :horizontal-scroll-enabled? true
                :model [:columns  [:Movie :Renter :Date]
                        :rows rent-vector]
                :id :rent-table
                :selection-mode :single)]
    (seesaw/scrollable table)))

(defn get-movie-row [movie-table movie]
  "Given the movie-name returns the row number in which movie is."
  (let [total-row (seesaw.table/row-count movie-table)
        all-rows (seesaw.table/value-at movie-table (range total-row))
        sel-row (first (first (filter #(= movie (:Name (second %))) (map-indexed vector all-rows))))]
    sel-row))

