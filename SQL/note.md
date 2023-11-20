## 1. SELECT 전반 기능
```sql
SELECT
  CustomerID AS '아이디',
  CustomerName AS '고객명',
  City AS '도시',
  Country AS '국가'
FROM Customers
WHERE
  City = 'London' OR Country = 'Mexico'
ORDER BY CustomerName
LIMIT 0, 5;
```

<br>

## 2. 각종 연산자들
> SELECT 문에 들어갈 경우 연산 결과가 참인지 거짓인지 출력
> WHERE 문에 들어갈 경우 해당 조건이 만족하는 행 출력

* +, -, *, /	각각 더하기, 빼기, 곱하기, 나누기
* %, MOD	나머지
-----------------------------------
* IS	양쪽이 모두 TRUE 또는 FALSE
* IS NOT	한쪽은 TRUE, 한쪽은 FALSE
-----------------------------------
* AND, &&	양쪽이 모두 TRUE일 때만 TRUE
* OR, ||	한쪽은 TRUE면 TRUE
-----------------------------------
* =	양쪽 값이 같음
* !=, <>	양쪽 값이 다름
* \>, <	(왼쪽, 오른쪽) 값이 더 큼
* \>=, <=	(왼쪽, 오른쪽) 값이 같거나 더 큼
-----------------------------------
* BETWEEN {MIN} AND {MAX}	두 값 사이에 있음
* NOT BETWEEN {MIN} AND {MAX}	두 값 사이가 아닌 곳에 있음
-----------------------------------
* IN (...)	괄호 안의 값들 가운데 있음
* NOT IN (...)	괄호 안의 값들 가운데 없음
-----------------------------------
* LIKE '... % ...'	0~N개 문자를 가진 패턴
* LIKE '... _ ...'	_ 갯수만큼의 문자를 가진 패턴

<br>

## 3. 문자와 숫자열을 다루는 함수들

### 숫자 관련 함수
* ROUND	반올림
* CEIL	올림
* FLOOR	내림
-----------------------------------
* ABS	절대값
-----------------------------------
* GREATEST	(괄호 안에서) 가장 큰 값
* LEAST	(괄호 안에서) 가장 작은 값
-----------------------------------
* POW(A, B), POWER(A, B)	A를 B만큼 제곱
* SQRT	제곱근
-----------------------------------
* TRUNCATE(N, n)	N을 소숫점 n자리까지 선택
-----------------------------------
* MAX	가장 큰 값
* MIN	가장 작은 값
* COUNT	갯수 (NULL값 제외)
* SUM	총합
* AVG	평균 값

<br>


### 문자 관련 함수

* UCASE, UPPER	모두 대문자로
* LCASE, LOWER	모두 소문자로
-----------------------------------
* CONCAT(...)	괄호 안의 내용 이어붙임
* CONCAT_WS(S, ...)	괄호 안의 내용 S로 이어붙임
-----------------------------------
* SUBSTR, SUBSTRING	주어진 값에 따라 문자열 자름
* LEFT	왼쪽부터 N글자
* RIGHT	오른쪽부터 N글자
-----------------------------------
* LENGTH	문자열의 바이트 길이
* CHAR_LENGTH, CHARACTER_LEGNTH	문자열의 문자 길이
-----------------------------------
* TRIM	양쪽 공백 제거
* LTRIM	왼쪽 공백 제거
-----------------------------------
* RTRIM	오른쪽 공백 제거
* LPAD(S, N, P)	S가 N글자가 될 때까지 P를 이어붙임
* RPAD(S, N, P)	S가 N글자가 될 때까지 P를 이어붙임
-----------------------------------
* REPLACE(S, A, B)	S중 A를 B로 변경
* INSTR(S, s)	S중 s의 첫 위치 반환, 없을 시 0
-----------------------------------
* CAST(A AS T)	A를 T 자료형으로 변환
 CONVERT(A, T)	A를 T 자료형으로 변환

 <br>

## 4. 시간/날짜 관련 및 기타 함수들

### 시간/날짜 관련 함수
* CURRENT_DATE, CURDATE	현재 날짜 반환
* CURRENT_TIME, CURTIME	현재 시간 반환
* CURRENT_TIMESTAMP, NOW 현재 시간과 날짜 반환
-----------------------------------
* DATE 문자열에 따라 날짜 생성
* TIME 문자열에 따라 시간 생성
-----------------------------------
* YEAR	주어진 DATETIME값의 년도 반환
* MONTHNAME	주어진 DATETIME값의 월(영문) 반환
* MONTH	주어진 DATETIME값의 월 반환
* WEEKDAY	주어진 DATETIME값의 요일값 반환(월요일: 0)
* DAYNAME	주어진 DATETIME값의 요일명 반환
* DAYOFMONTH, DAY	주어진 DATETIME값의 날짜(일) 반환
-----------------------------------
* HOUR	주어진 DATETIME의 시 반환
* MINUTE	주어진 DATETIME의 분 반환
* SECOND	주어진 DATETIME의 초 반환
-----------------------------------
* ADDDATE, DATE_ADD	시간/날짜 더하기
* SUBDATE, DATE_SUB	시간/날짜 빼기
-----------------------------------
* DATE_DIFF	두 시간/날짜 간 일수차
* TIME_DIFF	두 시간/날짜 간 시간차
-----------------------------------
* LAST_DAY	해당 달의 마지막 날짜
-----------------------------------
* DATE_FORMAT	시간/날짜를 지정한 형식으로 반환
* STR _ TO _ DATE(S, F)	S를 F형식으로 해석하여 시간/날짜 생성


| 형식 | 설명 |
| ------------ | ------------- |
| %Y	|년도 4자리|
| %y	|년도 2자리|
| %M	|월 영문|
| %m	|월 숫자|
| %D	|일 영문(1st, 2nd, 3rd...)|
| %d, %e|	일 숫자 (01 ~ 31)|
| %T	|hh:mm:ss|
| %r|	hh:mm:ss AM/PM|
| %H, %k|	시 (~23)|
| %h, %l|	시 (~12)|
| %i	|분|
| %S, %s|	초|
| %p	|AM/PM|

<br>

### 기타 함수들
* IF(조건, T, F)	조건이 참이라면 T, 거짓이면 F 반환
```
SELECT IF (1 > 2, '1는 2보다 크다.', '1은 2보다 작다.');
```
* 보다 복잡한 경우는 CASE문 사용
```sql
SELECT
  Price,
  IF (Price > 30, 'Expensive', 'Cheap'),
  CASE
    WHEN Price < 20 THEN '저가'
    WHEN Price BETWEEN 20 AND 30 THEN '일반'
    ELSE '고가'
  END
FROM Products;
```
* IFNULL(A, B)	A가 NULL일 시 B 출력

<br>

## 5. 조건에 따라 그룹으로 묵기

#### GROUP BY

조건에 따라 그룹을 묶은 뒤 그룹 함수를 적용하면, 같은 그룹으로 묶인 데이터끼리 함수가 실행되어 결과가 출력된다.
```sql
SELECT
  CategoryID,
  MAX(Price) AS MaxPrice,   --CategoryID가 같은 price들 중 최댓값
  MIN(Price) AS MinPrice,   --CategoryID가 같은 price들 중 최솟값
FROM Products
GROUP BY CategoryID;

```

#### WITH ROLLUP 
그룹화된 값들의 전체 집계값을 출력한다.
```sql
SELECT 
  Country, COUNT(*)  --겹치지 않는 Country가 몇개 있는지 카운트
FROM Suppliers
GROUP BY Country
WITH ROLLUP;  --카운트된 값들의 합
```

#### HAVING 
WHERE문은 그룹화 하기 전 데이터 적용된다면, HAVING문은 그룹화된 데이터에 조건을 추가하여 걸러낸다.  
```sql
SELECT
  CategoryID,
  MAX(Price) AS MaxPrice, 
  MIN(Price) AS MinPrice,
  TRUNCATE((MAX(Price) + MIN(Price)) / 2, 2) AS MedianPrice,
  TRUNCATE(AVG(Price), 2) AS AveragePrice
FROM Products
WHERE CategoryID > 2  --CategoryID > 2인 함수들만 그룹화
GROUP BY CategoryID
HAVING
  AveragePrice BETWEEN 20 AND 30 
  AND MedianPrice < 40;
   --그룹화하여 계산된 결과 중에서 AveragePrice가 20에서 30 사이이고 MedianPrice < 40 인 데이터들만 출력
```

#### DISTINCT
GROUP BY 와 달리 집계함수가 사용되지 않고, 정렬되지 않은 상태로 중복된 값들을 제거한다.
```sql
SELECT
  Country,
  COUNT(DISTINCT CITY)  --같은 Country 중에 겹치지 않는 CITY가 몇개있는 지 나타냄
FROM Customers
GROUP BY Country;
```

<br>

## 6. 쿼리 안에 서브쿼리

### 1) 비상관 서브커리
두 쿼리가 따로 실행, 내부 쿼리가 우선 실행되고 그 결과가 외부 쿼리의 where절으로 사용된다.
```sql
SELECT
  CategoryID, CategoryName, Description
FROM Categories
WHERE
  CategoryID IN
  (SELECT CategoryID FROM Products
  WHERE Price > 50);
  -- products 테이블에서 Price > 50인 CategoryID를 가져오고,
  -- Categories 테이블의 CategoryID가 가져온 CategoryID에 속하면 
  -- 그 CategoryID와 CategoryName, Description 을 가져옴
```
* ~ ALL	서브쿼리의 모든 결과에 대해 ~하다
* ~ ANY	서브쿼리의 하나 이상의 결과에 대해 ~하다

### 2) 상관 서브쿼리
* 내부 쿼리의 값이 결정되는데 외부 쿼리에 의존한다.
* 외부 쿼리의 테이블 column 하나 하나 마다 서브 쿼리가 실행됨
```sql
SELECT
  ProductID, ProductName,
  (
    SELECT CategoryName FROM Categories C
    WHERE C.CategoryID = P.CategoryID
  ) AS CategoryName
FROM Products P;
-- Products 테이블의 ProductID, ProductName를 가져옴
-- Products 테이블의 해당 categoryID와 Categories테이블의 categoryID가 일치하는 colunm을 찾아 해당 CategoryName을 반환
-- -> Products 테이블의 ProductID, ProductName과 Categories 테이블의 CategoryName을 함께 보기 위함
```

```sql
SELECT
  SupplierName, Country, City,
  (
    SELECT COUNT(*) FROM Customers C
    WHERE C.Country = S.Country
  ) AS CustomersInTheCountry,
  (
    SELECT COUNT(*) FROM Customers C
    WHERE C.Country = S.Country 
      AND C.City = S.City
  ) AS CustomersInTheCity
FROM Suppliers S;
-- Suppliers의 SupplierName, Country, City을 가져옴
-- Suppliers Country와 Customers의 Country가 같은 column을 찾아 그 갯수를 반환
-- 그 둘의 city까지 같은 column을 찾아 그 갯수를 반환
-- --> 해당 국가에 있는 고객들과, 해당 도시에 있는 고객들의 수를 출력
```

```sql
SELECT
  CategoryID, CategoryName,
  (
    SELECT MAX(Price) FROM Products P
    WHERE P.CategoryID = C.CategoryID
  ) AS MaximumPrice,
  (
    SELECT AVG(Price) FROM Products P
    WHERE P.CategoryID = C.CategoryID
  ) AS AveragePrice
FROM Categories C;
-- 각 카테고리 별 제품들의 최고가와 평균가를 가져옴
```


```sql
SELECT
  ProductID, ProductName, CategoryID, Price
  -- ,(SELECT AVG(Price) FROM Products P2
  -- WHERE P2.CategoryID = P1.CategoryID)
FROM Products P1
WHERE Price < (
  SELECT AVG(Price) FROM Products P2
  WHERE P2.CategoryID = P1.CategoryID
);
-- 각 카테고리 별 평균 가격 보다 낮은 가격에 해당하는 column의 ProductID, ProductName, CategoryID, Price 출력
-- 
```

```sql
SELECT
SELECT
  CategoryID, CategoryName
  -- ,(SELECT MAX(P.Price) FROM Products P
  -- WHERE P.CategoryID = C.CategoryID
  -- ) AS MaxPrice
FROM Categories C
WHERE EXISTS (
  SELECT * FROM Products P
  WHERE P.CategoryID = C.CategoryID
  AND P.Price > 80
);
-- 각 카테고리 별 가격이 80이 넘어가는 것이 있는 column의 CategoryID, CategoryName 출력
```

<br>

## 7. JOIN - 여러 테이블 조립하기
 ON 뒤에 수식을 기준으로 여러 테이블 합치기

### 1) JOIN(INNER JOIN) - 내부 조인
* 양쪽 모두에 값이 있는 행(NOT NULL) 반환
* 'INNER '는 선택사항

```sql
SELECT C.CategoryID, C.CategoryName, P.ProductName
FROM Categories C
JOIN Products P 
  ON C.CategoryID = P.CategoryID; 
-- ambiguous 주의!
```

```sql
SELECT 
  C.CategoryName,
  MIN(O.OrderDate) AS FirstOrder,
  MAX(O.OrderDate) AS LastOrder,
  SUM(D.Quantity) AS TotalQuantity
FROM Categories C
JOIN Products P 
  ON C.CategoryID = P.CategoryID
JOIN OrderDetails D
  ON P.ProductID = D.ProductID
JOIN Orders O
  ON O.OrderID = D.OrderID
GROUP BY C.CategoryID;
-- 각 카테고리 별 첫 주문, 마지막 주문, 총 주문량 출력
```

#### SELF JOIN - 같은 테이블끼리
```sql
SELECT
  E1.EmployeeID, CONCAT_WS(' ', E1.FirstName, E1.LastName) AS Employee,
  E2.EmployeeID, CONCAT_WS(' ', E2.FirstName, E2.LastName) AS NextEmployee
FROM Employees E1 JOIN Employees E2
ON E1.EmployeeID + 1 = E2.EmployeeID;
-- 1번의 전, 마지막 번호의 다음은 없기 때문에 해당 행은 출력되지 X
```

<br>

### 2) LEFT/RIGHT OUTER JOIN - 외부 조인
* 반대쪽에 데이터가 있든 없든(NULL), 선택된 방향에 있으면 출력 - 행 수 결정
* 'OUTER '는 선택사항

```sql
SELECT
  E1.EmployeeID, CONCAT_WS(' ', E1.FirstName, E1.LastName) AS Employee,
  E2.EmployeeID, CONCAT_WS(' ', E2.FirstName, E2.LastName) AS NextEmployee
FROM Employees E1
LEFT JOIN Employees E2
ON E1.EmployeeID + 1 = E2.EmployeeID
ORDER BY E1.EmployeeID;
-- 9번의 다음 번호가 없어도 해당 column을 출력
-- LEFT를 RIGHT로 바꿔서도 실행해 볼 것 -> 1번의 이전 번호가 없어도 해당 column을 출
```
<br>

 ### 3) CROSS JOIN - 교차 조인
 * 조건 없이 모든 조합 반환(A * B) -> 두 테이블의 모든 조합을 볼 수 있다.

```sql
SELECT
  E1.LastName, E2.FirstName
FROM Employees E1
CROSS JOIN Employees E2
ORDER BY E1.EmployeeID;
```

<br>

## 8. UNION - 집합으로 나누기
* 두 개 이상의 SELECT 문의 결과 집합을 하나로 결합
* UNION:	중복을 제거한 집합
* UNION ALL:	중복을 제거하지 않은 집합

```sql
SELECT CustomerName AS Name, City, Country, 'CUSTOMER'
FROM Customers
UNION
SELECT SupplierName AS Name, City, Country, 'SUPPLIER'
FROM Suppliers
ORDER BY Name;
-- Customers 테이블의 CustomerName과 Suppliers 테이블의 SupplierName을 Name이라는 이름으로 함께 가져옴
```

<br>

## 9. 테이블 만들고 데이터 입력, 삭제

#### 테이블 생성
```sql
CREATE TABLE people (
  person_id INT,
  person_name VARCHAR(10),
  age TINYINT,
  birthday DATE
);
```

#### 테이블 수정
```sql
-- 테이블명 변경
ALTER TABLE people RENAME TO  friends,
-- 컬럼 자료형 변경
CHANGE COLUMN person_id person_id TINYINT,
-- 컬럼명 변경
CHANGE COLUMN person_name person_nickname VARCHAR(10), 
-- 컬럼 삭제
DROP COLUMN birthday,
-- 컬럼 추가
ADD COLUMN is_married TINYINT AFTER age;
```

#### 테이블 삭제
```sql
DROP TABLE friends;
```

#### 데이터 입력
```sql
INSERT INTO people
  (person_id, person_name, age, birthday)
  VALUES 
    (4, 'Kim', 30, '1991-03-01'),
    (5, 'Lee', 15, '2006-12-07'),
    (6, 'Park', 24, '1997-10-30');
```

#### 테이블 생성시 제약 조건 추가
```sql
CREATE TABLE people (
  person_id INT AUTO_INCREMENT PRIMARY KEY,
  person_name VARCHAR(10) NOT NULL,
  nickname VARCHAR(10) UNIQUE NOT NULL,
  age TINYINT UNSIGNED,
  is_married TINYINT DEFAULT 0
);
```
* AUTO_INCREMENT	새 행 생성시마다 자동으로 1씩 증가
* PRIMARY KEY	중복 입력 불가, NULL(빈 값) 불가
* UNIQUE	중복 입력 불가
* NOT NULL	NULL(빈 값) 입력 불가
* UNSIGNED	(숫자일시) 양수만 가능
* DEFAULT	값 입력이 없을 시 기본값

<br>

#### 칼럼 삭제하기
```sql
DELETE FROM businesses  --여기까지 입력하면 전체 삭제, 새로운 데이터를 입력하면 pk가 이어서 증가
WHERE status = 'CLS';   --주어진 조건을 만족하는 행만 삭제
```

```sql
TRUNCATE businesses; --행 전체 삭제, 테이블을 초기화하여 새로운 데이터를 입력하면 pk가 1부터 증가
```

#### 칼럼 수정하기
```sql
UPDATE menus
SET menu_name = '삼선짜장'
WHERE menu_id = 12;
-- menu 테이블의 menu_id가 12인 칼럼의 menu_name을 삼선짜장으로 변경
``` 

```sql 
UPDATE menus
SET menu_name = CONCAT('전통 ', menu_name)
WHERE fk_business_id IN (
  SELECT business_id 
  FROM sections S
  LEFT JOIN businesses B
    ON S.section_id = B.fk_section_id 
  WHERE section_name = '한식'
);
-- 한식인 식당에 해당하는 모든 메뉴 앞에 '전통'을 더함
``` 