
### 기본 설정

#### JPA와 DB 설정, 동작확인            
main/resources/application.yml
```
spring: #띄어쓰기 없음
  datasource: #띄어쓰기 2칸
    url: jdbc:h2:tcp://localhost/~/jpashop #4칸
    username: sa 
    password:

 driver-class-name: org.h2.Driver
  jpa: #띄어쓰기 2칸
    hibernate: #띄어쓰기 4칸
      ddl-auto: create #띄어쓰기 6칸
    properties: #띄어쓰기 4칸
      hibernate: #띄어쓰기 6칸
        #show_sql: true #띄어쓰기 8칸
        format_sql: true #띄어쓰기 8칸

logging.level: #띄어쓰기 없음
  org.hibernate.SQL: debug  #띄어쓰기 2칸
  org.hibernate.orm.jdbc.bind: trace #띄어쓰기 2칸

```
> show_sql : 옵션은 System.out 에 하이버네이트 실행 SQL을 남긴다.            
> org.hibernate.SQL : 옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.

<br>

#### 쿼리 파라미터 로그 남기기
```
implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'
```

<br>

### 도메인 설계시 주의점 
* 엔티티에는 가급적 Setter를 사용 X (값 타입은 변경 불가능하게 설계)
* 모든 연관관계는 지연로딩으로 설정! -> @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정
* 컬렉션은 필드에서 초기화
```
 @ManyToMany(mappedBy = "items")
 private List<Category> categories = new ArrayList<Category>();
 ```
 * 연관관계 편의 메서드를 사용하면 하나의 메서드로 양측 관계를 모두 설정할 있다.
 ```
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
 ```

* enumType은 무조건 String으로 설정한다. @Enumerated(EnumType.STRING) 

<br>

### API 설계시 주의점
* db가 변경되는 모든 작업은 transaction 안에서 수행되어야 한다.

* db를 조회만 하는 메서드는 readonly=true로 설정가능하다.

* 데이터를 가지고 있는 엔터티에 직접 비즈니스 로직을 작성하는 것이 좋다.
 -> 예를 들어서 Item 객체의 stockQuantity가 증가하거나 감소하는 로직

 * 테스트를 할 때 @Transactional을 사용하면 반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백시킨다.

 * 연관관계 매핑이 많아 생성하기 복잡한 엔터티는 별도의 생성 메서드가 있으면 좋다.

* CascadeType.ALL은 해당 객체를 매핑하는 객체가 자신밖에 없을 때 사용 가능함

* 생성 메서드가 별도로 존재하는 엔터티의 경우 기본 생성자로 생성하여 set메서드로 설정하는 것을 막기 위해서 해당 엔터티의 기본생성자를 protected로 하는 것이 좋음 혹은 @NoArgsConstructor(access = AccessLevel.PROTECTED)

* 절대 entity 를 외부로 호출하고 반환하면 XXXX -> dto 사용

* 엔티티를 변경할 때는 항상 변경 감지를 사용
* 컨트롤러에서 어설프게 엔티티를 생성 X
* 트랜잭션이 있는 서비스 계층에 식별자( id )와 변경할 데이터를 명확하게 전달(파라미터 or dto)
* 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경 -> 트랜잭션 커밋 시점에 변경 감지가 실행
