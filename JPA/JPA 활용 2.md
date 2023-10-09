### API 개발 기본

- @RestContoller : @Controller + @ResponseBody

- api 스펙을 위한 별도의 dto를 무조건 만들어야함, entity를 그대로 파라미터로 받으면 X 

- PUT 요청: 멱등성을 지님 - > 같은 걸 여러번 호출해도 결과가 달라지지 않음

- dto에는 @data 써도 큰 상관 X

- update 등을 할 때는 entity를 반환하기 보다는 entity id 값 정도 반환하는 것이 좋음 (command 와 query 분리)

- 회원 조회 API를 만들 때는 데이터를 수정, 변경할 일이 없으므로 jpa ddl-auto를 none으로 바꾸면 편함

- 부분 수정시 PATCH나 POST 사용, 전체 수정 시 PUT 사용         
ex) 회원 정보 중 이름만 바꾸는 경우는 부분 수정이므로 PATCH나 POST을 사용하는 것이 REST 스타일에 맞음

- 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어려움 -> 별도의 클래스를 생성하여 그 안에 담아서 반환

- dto는 entity를 참조해도 괜찮음

<br>

### x to one 관계 조회
 
1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.

```
 @GetMapping("/api/v1/simple-orders")
    public List<SimpleOrderDto> ordersV1() {
        List<Order> orders = orderRepository.findAll();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

```

<br>

2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.

findAll() ->  findAllWithMemberDelivery()
```
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .getResultList();
    }
```

<br>

3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.

```
public List<OrderSimpleQueryDto> findOrderDtos() {

        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery" +
                                ".OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
```

<br>

4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사
용한다.
