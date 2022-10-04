#!/usr/bin/env racket

;comment out following line to run in repl
#lang racket
(require rackunit)

;to trace function fn, add (trace fn) after fn's definition
(require racket/trace)  

;;; Some of the exercises below refer to employees.
;;; An employee is represented by a 4-tuple (name age department salary)

;;test data for employees
(define EMPLOYEES
  '((tom 33 cs 85000.00)
    (joan 23 ece 110000.00)
    (bill 29 cs 69500.00)
    (john 28 me 58200.00)
    (sue 19 cs 22000.00)
    ))

;; #1: 15-points
;;return list of employees having department dept
;;must be implemented recursively
;; Hint: use equal? to check for department equality

(define (dept-employees dept employees)
  (cond [(null? employees) '()]
    [(equal? dept (list-ref (car employees ) 2 ) )
     (append  (cons (car employees ) (dept-employees dept (cdr employees))))]
     [else (dept-employees dept (cdr employees))])
  )


;(trace dept-employees)
 (check-equal? (dept-employees 'ece EMPLOYEES) '((joan 23 ece 110000.00)))
 (check-equal? (dept-employees 'cs EMPLOYEES)
 	      '((tom 33 cs 85000.00)
 		(bill 29 cs 69500.00)
 		(sue 19 cs 22000.00)
 		))
 (check-equal? (dept-employees 'ce EMPLOYEES) '())

;; #2: 5-points
;;return list of names of employees belonging to department dept
;;must be implemented recursively
;;Hint: almost the same as the previous exercise
(define (dept-employees-names dept employees)
  (cond [(null? employees) '()]
    [(equal? dept (list-ref (car employees ) 2 ) )
     (append  (cons (list-ref (car employees ) 0) (dept-employees-names dept (cdr employees))))]
     [else (dept-employees-names dept (cdr employees))])
  )
  

(check-equal? (dept-employees-names 'ece EMPLOYEES) '(joan))
(check-equal? (dept-employees-names 'cs EMPLOYEES) '(tom bill sue))
(check-equal? (dept-employees-names 'ce EMPLOYEES) '())

;; #3: 15-points
;;Given list indexes containing 0-based indexes and a list possibly
;;containing lists nested to an abitrary depth, return the element
;;in list indexed successively by indexes. Return 'nil if there is
;;no such element.
;;Hint: use list-ref
(define (list-access indexes list)
  (cond [(null? indexes) list]
        [(>= (car indexes) (length list)) 'nil]
        [else (list-access (cdr indexes) (list-ref list (car indexes)))]
  )
 )

 (check-equal? (list-access '(1) '(a b c)) 'b)
 (check-equal? (list-access '(2) '(a b (c))) '(c))
 (check-equal? (list-access '(2 0) '(a b (c))) 'c)
 (check-equal? (list-access '(3) '(a b (c))) 'nil)
 (check-equal? (list-access '(2 1) '(a b (c))) 'nil)
 (check-equal? (list-access '() '((1 2 3) (4 (5 6 (8)))) )
 	      '((1 2 3) (4 (5 6 (8)))))
 (check-equal? (list-access '(1) '((1 2 3) (4 (5 6 (8)))) )
 	      '(4 (5 6 (8))))
 (check-equal? (list-access '( 1 1 2) '((1 2 3) (4 (5 6 (8)))) )
 	      '(8))
 (check-equal? (list-access '( 1 1 2 0) '((1 2 3) (4 (5 6 (8)))) )
 	      '8)
 (check-equal? (list-access '(0 1) '((1))) 'nil)


;; #4: 15-points
;;return sum of salaries for all employees
;;must be tail-recursive
;;Hint: use a nested auxiliary function with an accumulating parameter

(define (salary-sum-helper list sum-so-far)
  (cond
    [(equal? list '()) sum-so-far]
    [else (salary-sum-helper (cdr list)(+ sum-so-far (list-ref (car list) 3 ))  )]
    )
  )
(define (employees-salary-sum employees)
  (salary-sum-helper employees 0)
  )

(check-equal? (employees-salary-sum EMPLOYEES) 344700.00)
(check-equal? (employees-salary-sum '()) 0)

;; #5: 15-points
;;return list of pairs giving name and salary of employees belonging to
;;department dept
;;cannot use recursion
;;Hint: use filter and map
(define (dept-employees-names-salaries dept employees)
  (map (lambda (pair)
          (list (list-ref pair 0) (list-ref pair 3))
         )
      (filter (lambda (emp)
             (equal? (list-ref emp 2 ) dept )
            ) employees))
 )

;(trace dept-employees-names-salaries)
 (check-equal? (dept-employees-names-salaries 'ece EMPLOYEES) '((joan 110000.00)))
 (check-equal? (dept-employees-names-salaries 'cs EMPLOYEES)
 	      '((tom 85000.00)
 		(bill 69500.00)
 		(sue 22000.00)
 		))
 (check-equal? (dept-employees-names-salaries 'ce EMPLOYEES) '())

;; #6: 15-points
;;return average salary of all employees; 0 if employees empty
;;cannot use recursion
;;Hint: use foldl
(define (employees-average-salary employees)
  (cond
   [(equal? (length employees) 0) 0]
   [else (/
          (foldl (lambda (emp1 emp2)
                       (+(list-ref emp1 3) emp2)
                  ) 0 employees)
   (length employees))]
 ))
;(trace employees-average-salary)
(check-equal? (employees-average-salary EMPLOYEES) (/ 344700.00 5))
(check-equal? (employees-average-salary '()) 0)

;; #7: 20-points
;; given an integer or list of nested lists containing integers,
;; return a string containing its JSON representation without any
;; whitespace
;; Hints: use (number->string n) to convert integer n to a string.
;;        use (string-append str1 str2 ...) to append str1 str2 ...
;;        use (string-join str-list sep) to join strings in str-list using sep
;; also see toJson() methods in java-no-deps Parser.java in prj1-sol

(define (helper x)
  (cond
    [(number? x) (number->string x)]
    [ else (int-list-json x)]
   )
 )

(define (int-list-json int-list)
  (cond
    [(number? int-list) (number->string int-list)]
    
    [else (string-append
      "["
      (string-join
         (to-String (map  helper int-list))
       ",")
      "]"
     )
    ]
   )
  )

(define (to-String list)
  (map (lambda (x)
         (cond
           [(number? x) (number->string x)]
           [else x]
           )
         )
    list)

  )

;(trace int-list-json)
(check-equal? (int-list-json '(1 2 3)) "[1,2,3]")
(check-equal? (int-list-json '(1 (2 (4 5) 6))) "[1,[2,[4,5],6]]")
(check-equal? (int-list-json '()) "[]")
(check-equal? (int-list-json 42) "42")
	 