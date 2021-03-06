;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

; Author: Stephen C. Gilardi
;;  scgilardi (gmail)
;;  Created 30 October 2008
;;

(ns clojure.test-clojure.numbers
  (:use clojure.test))


; TODO:
; ==
; and more...


;; *** Types ***

(deftest Coerced-Byte
  (let [v (byte 3)]
    (are [x] (true? x)
     (instance? SByte v)                  ;;; Byte
     (number? v)
     (integer? v)
     (rational? v))))

(deftest Coerced-Short
  (let [v (short 3)]
    (are [x] (true? x)
     (instance? Int16 v)        ;;; Short
     (number? v)
     (integer? v)
     (rational? v))))

(deftest Coerced-Integer
  (let [v (int 3)]
    (are [x] (true? x)
     (instance? Int32 v)       ;;; Integer
     (number? v)
     (integer? v)
     (rational? v))))

(deftest Coerced-Long          ;;; Int64
  (let [v (long 3)]
    (are [x] (true? x)
     (instance? Int64 v)
     (number? v)
     (integer? v)
     (rational? v))))

(deftest Coerced-BigInteger
  (let [v (bigint 3)]
    (are [x] (true? x)
     (instance? BigInteger v)
     (number? v)
     (integer? v)
     (rational? v))))

(deftest Coerced-Float
  (let [v (float 3)]
    (are [x] (true? x)
     (instance? Single v)       ;;; Float
     (number? v)
     (float? v))))

(deftest Coerced-Double
  (let [v (double 3)]
    (are [x] (true? x)
     (instance? Double v)
     (number? v)
     (float? v))))

(deftest Coerced-BigDecimal
  (let [v (bigdec 3)]
    (are [x] (true? x)
     (instance? BigDecimal v)
     (number? v)
     (decimal? v)
     (not (float? v)))))


;; *** Functions ***

(defonce DELTA 1e-12)

(deftest test-add
  (are [x y] (= x y)
      (+) 0
      (+ 1) 1
      (+ 1 2) 3
      (+ 1 2 3) 6

      (+ -1) -1
      (+ -1 -2) -3
      (+ -1 +2 -3) -2

      (+ 1 -1) 0
      (+ -1 1) 0

      (+ 2/3) 2/3
      (+ 2/3 1) 5/3
      (+ 2/3 1/3) 1 )

  (are [x y] (< (- x y) DELTA)
      (+ 1.2) 1.2
      (+ 1.1 2.4) 3.5
      (+ 1.1 2.2 3.3) 6.6 )

  (is (> (+ Int32/MaxValue 10) Int32/MaxValue))  ; no overflow               ;;; Integer/MAX_VALUE
  (is (thrown? FormatException (+ "ab" "cd"))) )    ; no string concatenation           ;;; ClassCastException


(deftest test-subtract
  (is (thrown? ArgumentException (-)))         ;;; IllegalArgumentException
  (are [x y] (= x y)
      (- 1) -1
      (- 1 2) -1
      (- 1 2 3) -4

      (- -2) 2
      (- 1 -2) 3
      (- 1 -2 -3) 6

      (- 1 1) 0
      (- -1 -1) 0

      (- 2/3) -2/3
      (- 2/3 1) -1/3
      (- 2/3 1/3) 1/3 )

  (are [x y] (< (- x y) DELTA)
      (- 1.2) -1.2
      (- 2.2 1.1) 1.1
      (- 6.6 2.2 1.1) 3.3 )

  (is (< (- Int32/MinValue 10) Int32/MinValue)) )  ; no underflow        ;;; Integer/MIN_VALUE


(deftest test-multiply
  (are [x y] (= x y)
      (*) 1
      (* 2) 2
      (* 2 3) 6
      (* 2 3 4) 24

      (* -2) -2
      (* 2 -3) -6
      (* 2 -3 -1) 6

      (* 1/2) 1/2
      (* 1/2 1/3) 1/6
      (* 1/2 1/3 -1/4) -1/24 )

  (are [x y] (< (- x y) DELTA)
      (* 1.2) 1.2
      (* 2.0 1.2) 2.4
      (* 3.5 2.0 1.2) 8.4 )

  (is (> (* 3 (int (/ Int32/MaxValue 2.0))) Int32/MaxValue)) )  ; no overflow  ;;; Integer/MAX_VALUE


(deftest test-divide
  (are [x y] (= x y)
      (/ 1) 1
      (/ 2) 1/2
      (/ 3 2) 3/2
      (/ 4 2) 2
      (/ 24 3 2) 4
      (/ 24 3 2 -1) -4

      (/ -1) -1
      (/ -2) -1/2
      (/ -3 -2) 3/2
      (/ -4 -2) 2
      (/ -4 2) -2 )

  (are [x y] (< (- x y) DELTA)
      (/ 4.5 3) 1.5
      (/ 4.5 3.0 3.0) 0.5 )

  (is (thrown? ArithmeticException (/ 0)))
  (is (thrown? ArithmeticException (/ 2 0)))
  (is (thrown? ArgumentException (/))) )              ;;; IllegalArgumentException


;; mod
;; http://en.wikipedia.org/wiki/Modulo_operation
;; http://mathforum.org/library/drmath/view/52343.html
;;
;; is mod correct?
;; http://groups.google.com/group/clojure/browse_frm/thread/2a0ee4d248f3d131#
;;
;; Issue 23: mod (modulo) operator
;; http://code.google.com/p/clojure/issues/detail?id=23

(deftest test-mod
  ; wrong number of args
  (is (thrown? ArgumentException (mod)))           ;;; IllegalArgumentException
  (is (thrown? ArgumentException (mod 1)))         ;;; IllegalArgumentException
  (is (thrown? ArgumentException (mod 3 2 1)))     ;;; IllegalArgumentException

  ; divide by zero
  (is (thrown? ArithmeticException (mod 9 0)))
  (is (thrown? ArithmeticException (mod 0 0)))

  (are [x y] (= x y)
    (mod 4 2) 0
    (mod 3 2) 1
    (mod 6 4) 2
    (mod 0 5) 0

    (mod 2 1/2) 0
    (mod 2/3 1/2) 1/6
    (mod 1 2/3) 1/3

    (mod 4.0 2.0) 0.0
    (mod 4.5 2.0) 0.5

    ; |num| > |div|, num != k * div
    (mod 42 5) 2      ; (42 / 5) * 5 + (42 mod 5)        = 8 * 5 + 2        = 42
    (mod 42 -5) -3    ; (42 / -5) * (-5) + (42 mod -5)   = -9 * (-5) + (-3) = 42
    (mod -42 5) 3     ; (-42 / 5) * 5 + (-42 mod 5)      = -9 * 5 + 3       = -42
    (mod -42 -5) -2   ; (-42 / -5) * (-5) + (-42 mod -5) = 8 * (-5) + (-2)  = -42

    ; |num| > |div|, num = k * div
    (mod 9 3) 0       ; (9 / 3) * 3 + (9 mod 3) = 3 * 3 + 0 = 9
    (mod 9 -3) 0
    (mod -9 3) 0
    (mod -9 -3) 0

    ; |num| < |div|
    (mod 2 5) 2       ; (2 / 5) * 5 + (2 mod 5)        = 0 * 5 + 2          = 2
    (mod 2 -5) -3     ; (2 / -5) * (-5) + (2 mod -5)   = (-1) * (-5) + (-3) = 2
    (mod -2 5) 3      ; (-2 / 5) * 5 + (-2 mod 5)      = (-1) * 5 + 3       = -2
    (mod -2 -5) -2    ; (-2 / -5) * (-5) + (-2 mod -5) = 0 * (-5) + (-2)    = -2

    ; num = 0, div != 0
    (mod 0 3) 0       ; (0 / 3) * 3 + (0 mod 3) = 0 * 3 + 0 = 0
    (mod 0 -3) 0
  )
)

;; rem & quot
;; http://en.wikipedia.org/wiki/Remainder

(deftest test-rem
  ; wrong number of args
  (is (thrown? ArgumentException (rem)))           ;;; IllegalArgumentException
  (is (thrown? ArgumentException (rem 1)))         ;;; IllegalArgumentException
  (is (thrown? ArgumentException (rem 3 2 1)))     ;;; IllegalArgumentException

  ; divide by zero
  (is (thrown? ArithmeticException (rem 9 0)))
  (is (thrown? ArithmeticException (rem 0 0)))
  
  (are [x y] (= x y)
    (rem 4 2) 0
    (rem 3 2) 1
    (rem 6 4) 2
    (rem 0 5) 0

    (rem 2 1/2) 0
    (rem 2/3 1/2) 1/6
    (rem 1 2/3) 1/3

    (rem 4.0 2.0) 0.0
    (rem 4.5 2.0) 0.5

    ; |num| > |div|, num != k * div
    (rem 42 5) 2      ; (8 * 5) + 2 == 42
    (rem 42 -5) 2     ; (-8 * -5) + 2 == 42
    (rem -42 5) -2    ; (-8 * 5) + -2 == -42
    (rem -42 -5) -2   ; (8 * -5) + -2 == -42

    ; |num| > |div|, num = k * div
    (rem 9 3) 0
    (rem 9 -3) 0
    (rem -9 3) 0
    (rem -9 -3) 0

    ; |num| < |div|
    (rem 2 5) 2
    (rem 2 -5) 2
    (rem -2 5) -2
    (rem -2 -5) -2
    
    ; num = 0, div != 0
    (rem 0 3) 0
    (rem 0 -3) 0
  )
)

(deftest test-quot
  ; wrong number of args
  (is (thrown? ArgumentException (quot)))           ;;; IllegalArgumentException
  (is (thrown? ArgumentException (quot 1)))         ;;; IllegalArgumentException
  (is (thrown? ArgumentException (quot 3 2 1)))     ;;; IllegalArgumentException

  ; divide by zero
  (is (thrown? ArithmeticException (quot 9 0)))
  (is (thrown? ArithmeticException (quot 0 0)))
  
  (are [x y] (= x y)
    (quot 4 2) 2
    (quot 3 2) 1
    (quot 6 4) 1
    (quot 0 5) 0

    (quot 2 1/2) 4
    (quot 2/3 1/2) 1
    (quot 1 2/3) 1

    (quot 4.0 2.0) 2.0
    (quot 4.5 2.0) 2.0

    ; |num| > |div|, num != k * div
    (quot 42 5) 8     ; (8 * 5) + 2 == 42
    (quot 42 -5) -8   ; (-8 * -5) + 2 == 42
    (quot -42 5) -8   ; (-8 * 5) + -2 == -42
    (quot -42 -5) 8   ; (8 * -5) + -2 == -42

    ; |num| > |div|, num = k * div
    (quot 9 3) 3
    (quot 9 -3) -3
    (quot -9 3) -3
    (quot -9 -3) 3

    ; |num| < |div|
    (quot 2 5) 0
    (quot 2 -5) 0
    (quot -2 5) 0
    (quot -2 -5) 0

    ; num = 0, div != 0
    (quot 0 3) 0
    (quot 0 -3) 0
  )
)


;; *** Predicates ***

;; pos? zero? neg?

(deftest test-pos?-zero?-neg?
  (let [nums [[(byte 2) (byte 0) (byte -2)]
              [(short 3) (short 0) (short -3)]
              [(int 4) (int 0) (int -4)]
              [(long 5) (long 0) (long -5)]
              [(bigint 6) (bigint 0) (bigint -6)]
              [(float 7) (float 0) (float -7)]
              [(double 8) (double 0) (double -8)]
              [(bigdec 9) (bigdec 0) (bigdec -9)]
              [2/3 0 -2/3]]
        pred-result [[pos?  [true false false]]
                     [zero? [false true false]]
                     [neg?  [false false true]]] ]
    (doseq [pr pred-result]
      (doseq [n nums]
        (is (= (map (first pr) n) (second pr))
          (pr-str (first pr) n))))))


;; even? odd?

(deftest test-even?
  (are [x] (true? x)
    (even? -4)
    (not (even? -3))
    (even? 0)
    (not (even? 5))
    (even? 8))
  (is (thrown? ArithmeticException (even? 1/2)))
  (is (thrown? ArithmeticException (even? (double 10)))))

(deftest test-odd?
  (are [x] (true? x)
    (not (odd? -4))
    (odd? -3)
    (not (odd? 0))
    (odd? 5)
    (not (odd? 8)))
  (is (thrown? ArithmeticException (odd? 1/2)))
  (is (thrown? ArithmeticException (odd? (double 10)))))

(defn- expt
  "clojure.contrib.math/expt is a better and much faster impl, but this works.
Math/pow overflows to Infinity."
  [x n] (apply * (replicate n x)))

(deftest test-bit-shift-left
  (are [x y] (= x y)
       2r10 (bit-shift-left 2r1 1)
       2r100 (bit-shift-left 2r1 2)
       2r1000 (bit-shift-left 2r1 3)
       2r00101110 (bit-shift-left 2r00010111 1)
       2r00101110 (apply bit-shift-left [2r00010111 1])
       2r01 (bit-shift-left 2r10 -1)
       (expt 2 32) (bit-shift-left 1 32)
       (expt 2 10000) (bit-shift-left 1 10000)
       ))

(deftest test-bit-shift-right
  (are [x y] (= x y)
       2r0 (bit-shift-right 2r1 1)
       2r010 (bit-shift-right 2r100 1)
       2r001 (bit-shift-right 2r100 2)
       2r000 (bit-shift-right 2r100 3)
       2r0001011 (bit-shift-right 2r00010111 1)
       2r0001011 (apply bit-shift-right [2r00010111 1])
       2r100 (bit-shift-right 2r10 -1)
       1 (bit-shift-right (expt 2 32) 32)
       1 (bit-shift-right (expt 2 10000) 10000)
       ))