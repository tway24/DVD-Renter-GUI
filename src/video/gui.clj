(ns video.gui
  (:gen-class)
  (:require [seesaw.core :as seesaw]
            [seesaw.swingx :as swingx]
            [seesaw.dev :as dev]
            [seesaw.selection :as selection]
            [seesaw.table :as table]
            [seesaw.bind :as bind]
            [video.load :refer :all]
            [video.features :refer :all]))

;;(def movie-file "movies_db.clj")
(declare start-gui)

;;-------------------------------------------------------------------------------
;;--------------------        GUI Functions        ------------------------------
;;-------------------------------------------------------------------------------

;;TODO: add "OK" button, which will dispose this window
(defn display [content width height ]
  (let [window (seesaw/frame
                :title "Video-store"
                :content content
                :width width
                :height height
                :id :main-window)]
(-> window seesaw/pack! seesaw/show!)))


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

(defn get-rent-vector [rent-map]
  (vec (for [k (keys rent-map)]
    (if (map? k)
      (conj (vec (vals k)) (rent-map k))
      (conj [] k (rent-map k)))
    )))

;;TODO: generalize function for renter + movie
(defn show-renter []
  (let [data @rent-atom
        rent-vector (get-rent-vector data)
        title "Renter's info"
        table (swingx/table-x
                :horizontal-scroll-enabled? true
                :model [:columns  [:Movie :Renter :Date]
                        :rows rent-vector]
                :id :rent-table
                :selection-mode :single
               )]
    (seesaw/scrollable table)))


;;(def tab-op (dev/show-options (show-inventory)))

;; Movie Buttons
(defn rent-fn [event]
  (let [renter (seesaw/input "Enter renter's name below")
        root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        rent-table (seesaw/select root [:#rent-table])
        sel-row (selection/selection movie-table)
        movie-map (table/value-at movie-table sel-row)
        qnt (:Quantity movie-map)
        movie (:Name movie-map)]
    ;;(println movie renter)
    (if renter
      (do
        (rent-movie movie renter)
        (seesaw.table/update-at! movie-table sel-row (assoc movie-map :Quantity (dec qnt)))
        (seesaw.table/insert-at! rent-table 0 [movie renter (due-date)])))))

(def rent-button
  (seesaw/button
    :text "Rent"
    :listen [:action (fn [event] (rent-fn event))]))

(defn remove-fn [event]
  (let [root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        sel-row (selection/selection movie-table)
        movie-map (table/value-at movie-table sel-row)
        movie (:Name movie-map)
        qnt (:Quantity movie-map)]
    (if (remove-movie movie)
      (seesaw.table/remove-at! movie-table sel-row)
      (seesaw.table/update-at! movie-table sel-row (assoc movie-map :Quantity (dec qnt))))))

(def remove-button
  (seesaw/button
    :text "Remove"
    :listen [:action (fn [event] (remove-fn event))]))

(def movie-btns (seesaw/left-right-split
  rent-button
  remove-button))

;;Rent Buttons
(defn get-movie-row [movie-table movie]
  (let [total-row (seesaw.table/row-count movie-table)
        all-rows (seesaw.table/value-at movie-table (range total-row))
        sel-row (first (first (filter #(= movie (:Name (second %))) (map-indexed vector all-rows))))]
    sel-row))

(defn return-fn [event]
  (let [root (seesaw/to-root event)
        rent-table (seesaw/select root [:#rent-table])
        sel-row (selection/selection rent-table)
        rent-map (table/value-at rent-table sel-row)
        movie (:Movie rent-map)
        renter (:Renter rent-map)
        movie-table (seesaw/select root [:#movie-table])
        movie-row (get-movie-row movie-table movie)
        movie-map (table/value-at movie-table movie-row)
        qnt (:Quantity movie-map)
        ]
    (return-movie movie renter)
    (seesaw.table/remove-at! rent-table sel-row)
    (seesaw.table/update-at! movie-table movie-row (assoc movie-map :Quantity (inc qnt)))))

(def return-button
  (seesaw/button
    :text "Return"
    :listen [:action (fn [event] (return-fn event))]))

(defn add-copy-fn [event]
  (let [root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        sel-row (selection/selection movie-table)
        movie-map (table/value-at movie-table sel-row)
        movie (:Name movie-map)
        qnt (:Quantity movie-map)
        new-qnt-str (seesaw/input (str "Enter number of copies to add to " movie))
        new-qnt (if new-qnt-str (read-string new-qnt-str) 0)
        update-qnt (if new-qnt-str (+ qnt new-qnt) 0)]
    (if-not movie
      (seesaw/alert "No movie was selected!")
      (if new-qnt-str (do
                        (add-copy movie new-qnt)
                        (seesaw.table/update-at! movie-table sel-row (assoc movie-map :Quantity update-qnt)))))))

(defn get-movie-info [event movie-table]
  (let [root (seesaw/to-root event)
        sel-row 0
        movie (seesaw/text (seesaw/select root [:#name]))
        qnt (seesaw/text (seesaw/select root [:#qnt]))
        price (seesaw/text (seesaw/select root [:#price]))]
    ;;(println movie qnt price root movie-table)
    (if (and movie qnt price)
      (do (add-movie movie (read-string qnt) (read-string price))
          (seesaw.table/insert-at! movie-table sel-row {:Name movie :Quantity qnt :Price price})
          (seesaw/dispose! root)))))

(defn add-movie-fn [event]
  (let [root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        movie-form (seesaw/grid-panel
                      :border "New movie form"
                      :columns 2
                      :items ["Name" (seesaw/text :text "" :id :name )
                              "Quantity" (seesaw/text :text "" :id :qnt)
                              "Price" (seesaw/text :text  "" :id :price)])
        save-btn (seesaw/button
                  :text "Save"
                  :listen [:action (fn [event] (get-movie-info event movie-table))])
        add-movie-pnl (seesaw/top-bottom-split movie-form save-btn)]
    (display add-movie-pnl 200 200)))

(defn change-price-fn [event]
  (let [root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        sel-row (selection/selection movie-table)
        movie-map (table/value-at movie-table sel-row)
        movie (:Name movie-map)
        price (:Price movie-map)
        new-price-str (seesaw/input (str "Enter new price of " movie))
        new-price (if new-price-str (read-string new-price-str) price)]
    (if-not movie
      (seesaw/alert "No movie was selected!")
      (if new-price-str (do
                      (change-price movie new-price)
                      (seesaw.table/update-at! movie-table sel-row (assoc movie-map :Price new-price)))))))

(defn show-movie-info [event]
  (let [root (seesaw/to-root event)
        movie (seesaw/text (seesaw/select root [:#name]))
        qnt-box (seesaw/select root [:#qnt-box])
        price-box (seesaw/select root [:#price-box])
        qnt-sel (seesaw/value qnt-box)
        price-sel (seesaw/value price-box)
        movie-map (first (filter #(= movie (:name %))(vals @movie-atom)))
        qnt (:qnt movie-map)
        price (:price movie-map)
        qnt-str (str "Quantity:     " qnt "\n")
        price-str (str "Price:           $" price)
        out-str (if movie-map (str (if qnt-sel qnt-str "") "  "(if price-sel price-str "")) "NOT FOUND !!!")
        lbl (seesaw/select root [:#find-out])]
    (seesaw/config! lbl :text out-str)))

(defn get-movie [event]
  (let [root (seesaw/to-root event)
        movie-table (seesaw/select root [:#movie-table])
        find-form (seesaw/grid-panel
                      :border "Find movie"
                      :columns 2
                      :items ["Name" (seesaw/text :text "" :id :name)])
        find-btn (seesaw/button
                  :text "Find"
                  :listen [:action (fn [event] (show-movie-info event ))])
        qnt-box (seesaw/checkbox
                  :text "Quantity"
                  :id :qnt-box
                  :selected? true)
        price-box (seesaw/checkbox
                  :text "Price"
                  :id :price-box
                  :selected? true)
        box-pnl (seesaw/left-right-split qnt-box price-box)
        out-lbl (seesaw/label :text "                                                  " :font "ARIAL-20" :id :find-out)
        find-pnl (-> box-pnl (seesaw/top-bottom-split find-form) (seesaw/top-bottom-split find-btn) (seesaw/top-bottom-split out-lbl))]
    (display find-pnl 200 200)))

(defn start-gui []
  (let [movie-panel (seesaw/top-bottom-split
                      (show-inventory)
                      movie-btns)
        rent-panel (seesaw/top-bottom-split
                      (show-renter)
                      return-button)
        tabs (seesaw/tabbed-panel
               :placement :top
               :tabs [{:title "Movies"
                       :tip "Show inventory"
                       :content movie-panel }
                      {:title "Renter"
                       :tip "Show renters"
                       :content rent-panel}])
        edit-menu (seesaw/menubar
                    :items [(seesaw/menu
                             :text "Find"
                             :items [(seesaw/action
                                      :name "Movie..."
                                      :key "menu F"
                                      :handler (fn [e] (get-movie e)))])
                            (seesaw/menu
                             :text "Edit"
                             :items [(seesaw/action
                                      :name "Add Movie..."
                                      :key "menu M"
                                      :handler (fn [e] (add-movie-fn e)))
                                     (seesaw/action
                                      :name "Add Copies..."
                                      :key "menu C"
                                      :handler (fn [e] (add-copy-fn e)))
                                     (seesaw/action
                                      :name "Change Price..."
                                      :key "menu P"
                                      :handler (fn [e] (change-price-fn e)))])])

        window (seesaw/frame
                :title "Video-store"
                :content tabs
                :width 200
                :height 200
                :menubar edit-menu
                :id :main-window)]
    (-> window seesaw/pack! seesaw/show!)))






