### 1. SELECT 전반 기능
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

### 2. 각종 연산자들
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

### 3. 문자와 숫자열을 다루는 함수들

#### 숫자 관련 함수
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


#### 문자 관련 함수

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

 ### 4. 시간/날짜 관련 및 기타 함수들

#### 시간/날짜 관련 함수
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

#### 기타 함수들
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