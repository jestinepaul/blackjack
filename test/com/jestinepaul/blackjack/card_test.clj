(ns com.jestinepaul.blackjack.card-test
  (:require [clojure.test :refer :all]
            [com.jestinepaul.blackjack.card :refer :all]))

(deftest distinct-cards-test
  (testing "Testing number of distinct cards"
    (let [cards (into #{}
                      (for [_ (range 103)]
                        (take-card)))]
      (is
        (=
          (count cards)
          (* 4 13))))))


(deftest score-calculation-1
  (testing "Hand with two cards"
    (is (= (calculate-score [{:rank "3"} {:rank "J"}])
           13))))

(deftest score-calculation-2
  (testing "Hand with A"
    (is (= (calculate-score [{:rank "A"} {:rank "9"}])
           20))))

(deftest score-calculation-3
  (testing "Hand with A as 1"
    (is (= (calculate-score [{:rank "A"} {:rank "J"} {:rank "2"}])
           13))))

(deftest score-calculation-4
  (testing "Hand with multiple As"
    (is (= (calculate-score [{:rank "A"}  {:rank "A"} {:rank "A"}])
           13))))
