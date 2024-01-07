# QueryDSL

### JPAQueryFactory 생성
```java
//EntityManager를 사용하여 JPAQueryFactory 생성
//필드로 제공해도 문제되지 X
JPAQueryFactory  queryFactory = new JPAQueryFactory(em);

QMember qMember = new QMember("m"); //별칭 직접 지정 -> 같은 테이블을 조인해야 하는 경우 사용
QMember qMember = QMember.member; //기본 인스턴스 사용 -> static import 해서 사용
```
```java
//다음과 같이 JPAQueryFactory 를 스프링 빈으로 등록해서 주입받아 사용해도 된다.
@Bean  
JPAQueryFactory jpaQueryFactory(EntityManager em) {
return new JPAQueryFactory(em);
}
```
### 검색
```java
queryFactory
        .selectFrom(member)  //select member from Member
        .where(member.username.eq("member1")  //username이 member1이고 나이가 10살
                 .and(member.age.eq(10)))
            .fetchOne();
```

### 검색 조건
검색 조건이 여러 개인 경우 .and() , .or() 또는 , 를 사용할 수 있다.
```java
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") //username != 'member1'
member.username.eq("member1").not() // username != 'member1'
member.username.isNotNull() //이름이 is not null
member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) //between 10, 30
member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30
member.username.like("member%") //like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
...
```

### 결과 조회
```java
//List
List<Member> fetch = queryFactory
         .selectFrom(member)
         .fetch();

//단 건
Member findMember1 = queryFactory
        .selectFrom(member)
        .fetchOne();

//처음 한 건 조회
Member findMember2 = queryFactory
        .selectFrom(member)
        .fetchFirst();
```

### 정렬

 * 회원 나이 내림차순(desc)
 * 회원 이름 올림차순(asc)
 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)

```java
queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();
```

### 페이징
```java
queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1) //0부터 시작(zero index)
        .limit(2) //최대 2건 조회
        .fetch();
```

### 집합
* COUNT(m), //회원수
 * SUM(m.age), //나이 합
 * AVG(m.age), //평균 나이
 * MAX(m.age), //최대 나이
 * MIN(m.age) //최소 나이
```java
List<Tuple> result = queryFactory
        .select(team.name, member.age.avg()) //팀 이름과 각 팀의 평균나이
        .from(member)
        .join(member.team, team)
        .groupBy(team.name) //team 이름이 같은 멤버끼리 그룹화
        .fetch();
 ```

 ### 조인
* join() , innerJoin() : 내부 조인(inner join)
* leftJoin() : left 외부 조인(left outer join)
* rightJoin() : rigth 외부 조인(rigth outer join)
 ```java
queryFactory
        .selectFrom(member)
        .join(member.team, team)
        .where(team.name.eq("teamA"))  
        .fetch();
 ```

### 세타 조인
* 연관관계가 없는 필드로 조인
* 외부조인은 불가능
* from 절에 둘 다 넣고, where절에 조인할 필드 넣음
```java
 queryFactory
        .select(member)
        .from(member, team) 
        .where(member.username.eq(team.name)) // 회원의 이름이 팀 이름과 같은 회원 조회
        .fetch();
```

### ON절을 활용한 조인
* 1. 조인 대상 필터링
* 2. 연관관계 없는 엔티티 외부 조인
1. 조인 대상 필터링
```java
List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(member.team, team)
        .on(team.name.eq("teamA"))
        .fetch();
```
on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면, where 절
에서 필터링 하는 것과 기능이 동일하다. 따라서 on 절을 활용한 조인 대상 필터링을 사용할 때, 내부조인 이면 익
숙한 where 절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용한다.


2. 
```java

List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(team)  //member.team의 id로 join 하는게 아니기 때문에, member.team이 아니라 team이 들어감
        .on(member.username.eq(team.name)) //세타조인에서는 조건이 where에 들어갔다면 여기는 on에 들어감
        .fetch();
```

### 페치조인
```java
 queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin()
        .where(member.username.eq("member1"))
        .fetchOne();
 ```

 ### 서브쿼리
 * com.querydsl.jpa.JPAExpressions 사용
 * static import 활용하면 JPAExpressions 생략
 * from 절에 서브쿼리는 JPA에서 지원하지 X
     1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
     2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
     3. nativeSQL을 사용한다.
 ```java
 //where절에 서브 쿼리
  List<Member> result =  queryFactory
        .selectFrom(member)
        .where(member.age.eq(
                    JPAExpressions
                    .select(memberSub.age.max())
                    .from(memberSub)
                ))
        .fetch();
 ```
 ```java
 //select 절에 서브 쿼리
List<Tuple> fetch = queryFactory
        .select(member.username,
                JPAExpressions
                        .select(memberSub.age.avg())
                        .from(memberSub)
        ).from(member)
        .fetch();
```

### case문
```java
List<String> result = queryFactory
        .select(new CaseBuilder()
        .when(member.age.between(0, 20)).then("0~20살")
        .when(member.age.between(21, 30)).then("21~30살")
        .otherwise("기타"))
         from(member)
        .fetch();
 ```

다음과 같은 임의의 순서로 회원을 출력하고 싶다면?
1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
2. 0 ~ 20살 회원 출력
3. 21 ~ 30살 회원 출력
```java
NumberExpression<Integer> rankPath = new CaseBuilder()
        .when(member.age.between(0, 20)).then(2)
        .when(member.age.between(21, 30)).then(1)
        .otherwise(3);
List<Tuple> result = queryFactory
        .select(member.username, member.age, rankPath)
        .from(member)
        .orderBy(rankPath.desc())
        .fetch();
```

### 상수, 문자 더하기
* constant()
* concat()
```java
Tuple result = queryFactory
         .select(member.username, Expressions.constant("A"))
        .from(member)
        .fetchFirst();
```
```java
String result = queryFactory
        .select(member.username.concat("_").concat(member.age.stringValue()))
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();
```

<br>

--------------------------------------------

<br>

## 1. DTO 조회

### 1-1. 프로퍼티 접근 - setter
```java
List<MemberDto> result = queryFactory
        .select(Projections.bean(MemberDto.class,
         member.username,
         member.age))
        .from(member)
        .fetch();
```

### 1-2. 필드 직접 접근
```java
List<MemberDto> result = queryFactory
        .select(Projections.fields(MemberDto.class,
        member.username,
        member.age))
        .from(member)
        .fetch();
```

#### 별칭이 다를 때
* ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
* username.as("memberName") : 필드에 별칭 적용
```java
List<UserDto> fetch = queryFactory
        .select(Projections.fields(UserDto.class, member.username.as("name"), //필드에 별칭 적
        ExpressionUtils.as(  //서브 쿼리에 별칭 적용
                JPAExpressions
                    .select(memberSub.age.max())
                    .from(memberSub), "age")
              )
        ).from(member)
        .fetch();
 ```

### 1-3. 생성자 사용
```java
List<MemberDto> result = queryFactory
        .select(Projections.constructor(MemberDto.class,
        member.username,
        member.age))
        .from(member)
        .fetch();
```
### 4. @QueryProjection
* 장점 : 컴파일러로 타입을 체크할 수 있디
* 단점     
    * DTO에 QueryDSL 어노테이션을 유지해야 한다.       
    * DTO까지 Q 파일을 생성해야 한다.

```java
@Data
public class MemberDto {

    private String username;
    private int age;
    public MemberDto() { }

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
``` 

```java
List<MemberDto> result = queryFactory
        .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();
``` 

<br>

## 2. 동적 쿼리

http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35
```java
@GetMapping("/v1/members")
 public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
    return memberJpaRepository.search(condition);
 }
 ```

### 2-1. builder 사용
```java
    //회원명, 팀명, 나이(ageGoe, ageLoe)
    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }

        if (hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }

        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }

        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }
``` 

### 2-2. Where절 파라미터 사용
```java
    public List<MemberTeamDto> search(MemberSearchCondition condition) {

        return queryFactory .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return isEmpty(username) ? null : member.username.eq(username);
    }

    private BooleanExpression teamNameEq(String teamName) {
        return isEmpty(teamName) ? null : team.name.eq(teamName);
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }
``` 

<br>

## 3. 수정, 삭제 벌크 연산
JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를 실행하
고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.
#### 수정
```java
long count = queryFactory
        .update(member)
        .set(member.username, "비회원")
        .where(member.age.lt(28))
        .execute();
 ``` 
#### 삭제
 ```java
long count = queryFactory
        .delete(member)
        .where(member.age.gt(18))
        .execute();
 ``` 

<br>

## 4. SQL function 호출하기
SQL function은 JPA와 같이 Dialect에 등록된 내용만 호출할 수 있다.
```java
//member M으로 변경하는 replace 함수 사용
String result = queryFactory
        .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                member.username, "member", "M"))
        .from(member)
        .fetchFirst();

 //소문자로 변경하는 함수 사용
 queryFactory
        .select(member.username)
        .from(member)
        .where(member.username.eq(Expressions.stringTemplate("function('lower', {0})", 
               member.username)))      

 //lower 같은 ansi 표준 함수들은 querydsl이 상당부분 내장하고 있디 때문에 다음과 같이 작성할 수 있다.
 .where(member.username.eq(member.username.lower()))               
``` 

<br>

## 5. 페이징 연동
http://localhost:8080/v2/members?size=5&page=2
```java
@GetMapping("/v2/members")
 public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
   return memberRepository.searchPageComplex(condition, pageable);
 }
```

```java
Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,  Pageable pageable);
``` 

```java
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition,,Pageable pageable) {

//데이터 조회 쿼리
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"), team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())  //pageable에서 offset을 가져와서 설정
                .limit(pageable.getPageSize())  //pageable에서 size를 가져와서 설정
                .fetch();

//카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

//count 쿼리가 생략 가능한 경우 생략해서 처리
//페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
//마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함, 
//더 정확히는 마지막 페이지이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때)
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
```
