# Spring Data JPA


### 1. 쿼리 메소드 기능

### 1-1. 메소드명
```java
  //1. 메소드 이름으로 쿼리 생성
  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  //2. NamedQuery 사용 -> 실무에서 잘 사용하지 X
  @Query(name = "Member.findByUsername")
  List<Member> findByUsername(@Param("username") String username);

  //3. 리포지토리 메소드에 쿼리 정의하기 -> 메소드 이름이 길어지는 경우 사용하기 좋음
  //@Query안에 쿼리를 정의하면 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음
  @Query("select m from Member m where m.username = :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);
```

<br>

### 1-2. @Query로 값, DTO 조회하기
```java
  //객체가 아니라 단순히 값 하나를 조회
  @Query("select m.username from Member m")
  List<String> findUsernameList();

  //DTO로 직접 조회
  //DTO로 조회할때는 new를 통해 dto의 생성자와 매칭되도록 해야함
  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
         "from Member m join m.team t")
  List<MemberDto> findMemberDto();
```

<br>

### 1-3. 파라미터 바인딩
```java
@Query("select m from Member m where m.username = :name")
 Member findMembers(@Param("name") String username); 

 //컬렉션 파라미터 바인딩
 //Collection 타입으로 in절 지원
@Query("select m from Member m where m.username in :names")
List<Member> findByNames(@Param("names") List<String> names);
```

<br>

### 1-4. 반환 타입
```java
  List<Member> findListByUsername(String name); //컬렉션
  //List<Member> aaa = memberRepository.findListByUsername("AAA");
  //!!"AAA"에 해당되는 값이 없어도 null을 반환하는게 아니라 빈 컬랙션을 반환함
  // -> null인 경우를 가정하는 코드를 작성할 필요X

  Member findMemberByUsername(String name); //단건
  //Member aaa1 = memberRepository.findMemberByUsername("AAA");
  //단건인 경우에는 해당되는 값이 없으면 null 반환
  //만약 결과가 2개 이상이면 exception 터짐

  Optional<Member> findOptionalByUsername(String name); //단건 Optional
  //Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");
  //조회 결과가 있을 수도 없을 수도 있으면 Optional을 사용하는 것이 좋음
```

<br>

### 2. 페이징과 정렬
#### 페이징과 정렬 파라미터
* org.springframework.data.domain.Sort : 정렬 기능
* org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
#### 특별한 반환 타입
* org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
* org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
* List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

```java
/*
   순수 JPA
*/
    //해당 나이와 일치하는 멤버들을 이름 내림차순으로 정렬
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    //해당 나이와 일치하는 멤버들의 수를 카운트
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

/*
   Spring Data JPA
*/

  //추가 count 쿼리 결과를 포함하는 페이징
    Page<Member> findPageByAge(int age, Pageable pageable);

    //추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    //List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
    List<Member> findListByAge(int age, Pageable pageable);

    //페이징 하는 것 없이 정렬만
    List<Member> findSortByAge(int age, Sort sort);

    //count 쿼리를 다음과 같이 분리할 수 있음
    //count 쿼리에서는 join이 필요 없기 때문에 불필요한 join을 막아줌
    //스프링 부트 3 이상(하이버네이트 6)에서는 알아서 의미없는 join문을 최적화하기 때문에 count 쿼리에서 join X
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

 
/*
  paging 테스트 
*/
@Test
public void page() throws Exception {

    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    PageRequest pageRequest = PageRequest.of(0, 3,Sort.by(Sort.Direction.DESC, "username"));

    int age = 10;
    Page<Member> page= memberRepository.findPageByAge(age, pageRequest);

    List<Member> content = page.getContent(); //조회된 데이터
    assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
    assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
    assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
    assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
    assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
    assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
}
```
#### Page 인터페이스 
```java
public interface Page<T> extends Slice<T> {
 int getTotalPages(); //전체 페이지 수
 long getTotalElements(); //전체 데이터 수
 <U> Page<U> map(Function<? super T, ? extends U> converter); //변환기
}
```

#### Slice 인터페이스 
```java
public interface Slice<T> extends Streamable<T> {
 int getNumber(); //현재 페이지
 int getSize(); //페이지 크기
 int getNumberOfElements(); //현재 페이지에 나올 데이터 수
 List<T> getContent(); //조회된 데이터
 boolean hasContent(); //조회된 데이터 존재 여부
 Sort getSort(); //정렬 정보
 boolean isFirst(); //현재 페이지가 첫 페이지 인지 여부
 boolean isLast(); //현재 페이지가 마지막 페이지 인지 여부
 boolean hasNext(); //다음 페이지 여부
 boolean hasPrevious(); //이전 페이지 여부Pageable getPageable(); //페이지 요청 정보
 Pageable nextPageable(); //다음 페이지 객체
 Pageable previousPageable();//이전 페이지 객체
 <U> Slice<U> map(Function<? super T, ? extends U> converter); //변환기
}

```

<br>

 ### MVC에 paging 적용 
```java
@GetMapping("/members")
public Page<MemberDto> list(Pageable pageable) {
 return memberRepository.findAll(pageable).map(MemberDto::new);
}
```
* 파라미터로 Pageable 을 받을 수 있다. 
* Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성
* /members?page=0&size=3&sort=id,desc&sort=username,desc
    * page: 현재 페이지, 0부터 시작한다.
    * size: 한 페이지에 노출할 데이터 건수
    * sort: 정렬 조건을 정의한다. 
          * 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort 파라미터 추가 ( asc 생략 가능)
* map을 사용하여 Page 형태로 DTO로 변환

<br>

* 기본값
글로벌 설정: 스프링 부트
```yml
spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/ 
```
* 개별 설정
@PageableDefault 어노테이션을 사용 
```java
@RequestMapping(value = "/members_page", method = RequestMethod.GET)
public String list(@PageableDefault(size = 12, sort = "username", 
 direction = Sort.Direction.DESC) Pageable pageable) {
 ...
}
```
* 접두사
페이징 정보가 둘 이상이면 접두사로 구분      
@Qualifier 에 접두사명 추가 "{접두사명}_xxx"              
예제: /members?member_page=0&order_page=1
```java
public String list(
 @Qualifier("member") Pageable memberPageable,
 @Qualifier("order") Pageable orderPageable, ...
 ```
 
<br>

### 3. 벌크성 수정 쿼리

```java
/*
 순수 JPA
*/ 
    //해당 나이와 일치하는 멤버들의 수를 카운트
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

/*
   Spring Data JPA
*/
    //Modifying 어노테이션이 있어야 executeUpdate를 실행, 없으면 getResultList() 등을 호출해버림
    //clearAutomatically = true -> em.clear()의 역할,
    //벌크 수정은 1차 캐시를 거치지 않고 바로 DBd에 반영되기 때문에, 이 옵션이 없으면 1차 캐시에는 수정되기 전의 값이 남아있음
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


/*
  벌크 수 테스트 
*/
    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //쓰기 지연 sql 저장소에 쿼리를 저장했다가 jpql 문이 실행되기 전에 위 쿼리를 실행
        
        //나이 update -> 영속성 컨텍스트를 거치지 않고 DB에 반영
        int resultCount = memberRepository.bulkAgePlus(20);

        //save에 의해 1차 캐시에 member5의 값이 있기 때문에 1차 캐시에서 가져옴 -> 40 출력
        Member member5 = memberRepository.findMemberByUsername("member5");
        System.out.println(member5.getAge());
        
        //1차 캐시 비움
        //clearAutomatically = true 옵션을 사용하면 이걸 알아서 해줌
        em.flush();
        em.clear();
        
        //영속성 컨텍스트가 빈 상태이기 때문에 DB에서 가져옴 ->  41 출력
        member5 = memberRepository.findMemberByUsername("member5");
        System.out.println(member5.getAge());
        assertThat(resultCount).isEqualTo(3);
    }
```

<br>

### 4. @EntityGraph - 연관된 엔티티들을 SQL 한번에 조회하는 방법

```java
    //jpql을 사용하는 경우
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //jpql 없이 fetch join을 수행해줌
    //간단한 쿼리여서 jpql을 사용하기 귀찮을 때 주로 사용, 복잡한 쿼리면 jpql로 fetch join하는 것이 더 좋음
    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);
```

<br>

### 5. JPA Hint & Lock
```java
    //데이터를 변경할 일이 없고 100% 읽기만 할 목적으로 find 하는 경우
    //변경 감지를 사용하지 않기 때문에 스냅샷을 만들지 않음 -> 성능 최적화
    //이 옵션을 넣었을 때 이점이 확실한 경우에만 사용
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //JPA가 제공하는 lock을 어노테이션으로 사용 가능
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String name);

```

<br>

### 6. 사용자 정의 리포지토리 구현
인터페이스의 메서드를 직접 구현하기 위해 사용자 정의 리포지토리 구현   

1. 사용자 정의 인터페이스 생성
```java
public interface MemberRepositoryCustom {
 List<Member> findMemberCustom();
}
```

2. 사용자 정의 인터페이스 구현 클래스
```java
@RequiredArgsConstructor
//MemberRepositoryImpl도 가능
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
 private final EntityManager em;
 @Override
 public List<Member> findMemberCustom() {
 return em.createQuery("select m from Member m")
 .getResultList();
 }
}
```

3. 사용자 정의 인터페이스 상속
```java
public interface MemberRepository
 extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
```

4. 사용자 정의 메서드 호출
```java
List<Member> result = memberRepository.findMemberCustom();
```
--> 사용자 정 인터페이스 이름 + Impl을 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록


<br>

### 6. Auditing

```java
//JPA 엔터티 수명 주기 이벤트를 수신하고, 감사 정보를 자동으로 채우는 역할
@EntityListeners(AuditingEntityListener.class) 
//공통 필드 및 매핑 정보를 가진 추상 부모 클래스를 정의
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreatedDate @Column(updatable = false)  //등록일
    private LocalDateTime createdDate;
    
    @LastModifiedDate //수정
    private LocalDateTime lastModifiedDate;

    @CreatedBy  //등록자
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy  //수정자
    private String lastModifiedBy;

}
```

```java
public class Member extends BaseEntity {}
```

등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록
```java
@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
```
실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음

<br>

참고: 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도 있다. 그래서
다음과 같이 Base 타입을 분리하고, 원하는 타입을 선택해서 상속한다.
```java
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;}
```
```java
public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}
```

<br>

#### 전체 적용
@EntityListeners(AuditingEntityListener.class) 를 생략하고 스프링 데이터 JPA 가 제공하는 이벤트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면 된다.
 META-INF/orm.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm 
        http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd"
        version="2.2">
<persistence-unit-metadata>
<persistence-unit-defaults>
<entity-listeners>
<entity-listener
class="org.springframework.data.jpa.domain.support.AuditingEntityListener"/>
</entity-listeners>
</persistence-unit-defaults>
</persistence-unit-metadata>
</entity-mappings>
```

<br>

### 7. 스프링 데이터 JPA 분석
스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체
```java
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> ...{

    @Transactional
    public <S extends T> S save(S entity) {

        if (entityInformation.isNew(entity)) {
           em.persist(entity);
           return entity;
        } else {
           return em.merge(entity);
        }
    }
        ...
}
```
* @Repository 적용: JPA 예외를 스프링이 추상화한 예외로 변환
* @Transactional 트랜잭션 적용
   * 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
   * 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음(사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임)
* @Transactional(readOnly = true)
  * 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음

<br>

### save() 메서드
* 새로운 엔티티면 저장( persist )
* 새로운 엔티티가 아니면 병합( merge )    
-> 수정을 할 경우에는 save가 아니라 무조건 변경감지를 사용해야 함

#### 새로운 엔티티를 판단하는 기본 전략
* 식별자가 객체일 때 null 로 판단
* 식별자가 자바 기본 타입일 때 0 으로 판단
* Persistable 인터페이스를 구현해서 판단 로직 변경 가능
- > JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운 엔티티
로 인식해서 정상 동작한다. 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미 식별자 값이
있는 상태로 save() 를 호출한다. 따라서 이 경우 merge() 가 호출된다. merge() 는 우선 DB를 호출해서 값
을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다. 따라서 Persistable 를 사
용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다. 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다. (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)

#### Persistable 구현
 ```java
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
  
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    } 
    
    @Override
    public boolean isNew() {  //생성 시간이 비어있으면 새로운 엔티티라고 판
        return createdDate == null;
    }
}
```

<br>

### 8. Projections
엔티티 대신에 DTO를 편리하게 조회할 때 사용
* 인터페이스
```java
public interface UsernameOnly {
 String getUsername();
}
```
* 리포지토리
```java
public interface MemberRepository ... {
 List<UsernameOnly> findProjectionsByUsername(String username);
 }
```
* 쿼리
```sql
select m.username from member m
where m.username=‘m1’;
```

<br>

#### 인터페이스가 아닌 구체적인 DTO 형식도 가능
생성자의 파라미터 이름으로 매칭 
```java
public class UsernameOnlyDto {

    private final String username;
    
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
```

#### 동적 Projections
 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
```java
<T> List<T> findProjectionsByUsername(String username, Class<T> type); 
```
```java
List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1",UsernameOnly.class);
```

#### 스프링의 SpEL 문법도 지원 
```java
public interface UsernameOnly { @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
String getUsername();
}
```
-> DB에서 엔티티 필드를 다 조회해온 다음에 계산하기 때문에  JPQL SELECT 절 최적화 X

#### 중첩 구조 처리
 ```java
public interface NestedClosedProjection {

    String getUsername();

    TeamInfo getTeam();
    interface TeamInfo { String getName();
    }
}
```
-> username은 root라서 username만 select 하지만 team은 root 엔티티가 아니기 때문에 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산


#### 네이티브 쿼리 활용
스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용 
```java
@Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " +
 "FROM member m left join team t ON m.team_id = t.team_id",
 countQuery = "SELECT count(*) from member",
 nativeQuery = true)
Page<MemberProjection> findByNativeProjection(Pageable pageable);
```

### 정리
* 프로젝션 대상이 root 엔티티면 유용하다.
* 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
* 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
* 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자
* 네이티브 쿼리 활용 측면에선 유용하게 쓸 수 있다.