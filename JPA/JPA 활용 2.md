## API 개발 기본

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

## x to one 관계 조회

!! entity를 그대로 반환할 경우 무한 루프에 빠지게 되며 여러가지 심각한 문제가 발생    

1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.      
---> n+1문제 발생   
ex) 만약 order 데이터가 2개일 경우
    1) order select로 2개의 order 반환
    2) 첫 번째 order에 대한 member와 delivery select 쿼리 나감
    3) 두 번째 orderdp 대한 member와 delivery select 쿼리 나감     
--> 총 쿼리가 5번 실행됨 

```java
 @GetMapping("/api/v1/simple-orders")
    public List<SimpleOrderDto> ordersV1() {
        List<Order> orders = orderRepository.findAll();  //orderRepository에서 order 조회
        List<SimpleOrderDto> result = orders.stream()  //order을 모두 dto로 변환
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

 @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
           orderId = order.getId();
           name = order.getMember().getName();  //member select 쿼리 나감
           orderDate = order.getOrderDate();  
           orderStatus = order.getStatus();
           address = order.getDelivery().getAddress();  //delivery select 쿼리 나감
           ;
        }
    }
   
```

<br>

2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.     
쿼리 한 번에 order, member, delivery를 다 조인해서 select      
OrderRepository - 추가 코드 findAll() ->  findAllWithMemberDelivery()
```java

  @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        //실행시 단 한 개의 쿼리만 나감
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    //OrderRepository
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
V1, V2는 order을 조회해서 dto로 반환했지만, 여기서는 직업 dto를 반환받는 쿼리를 작성      
-> 장점: 모든 값을 조회하지 않고 원하는 값만 가져올 수 있기 때문에 성능이 더 좋다.   
-> 단점: 재활용성이 떨어지며, dto로 조회했기 때문에 값을 변경할 수 없다.          
--> 트레픽이 엄청 크지 않은 경우 v2, v3에서 성능 차이가 크지 않기 v2를 사용하는 것을천
```java
//OrderSimpleQueryDto
@Data
public class OrderSimpleQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

//생성자에서 Order를 직접 받으면 안되고 파라미터로 받아야함
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime
            orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}

//OrderSimpleQueryRepository
public List<OrderSimpleQueryDto> findOrderDtos() {

        return em.createQuery(
            //jpa에서 dto를 직접 매핑할 수는 없기 때문에 new를 사용하여 쿼리를 작성해야 함
                        "select new jpabook.jpashop.repository.order.simplequery" +
                                ".OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }

    
```

<br>

4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.

<br>

## x to Many 관계 조회

1. 엔티티를 DTO로 변환  
-> N+1문제 발생
* order 1번
* member , address N번(order 조회 수 만큼)
* orderItem N번(order 조회 수 만큼)
* item N번(orderItem 조회 수 만큼)
```java

    @GetMapping("/api/v1/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; 
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; //orderItem조차도 dto로 변환해야 한다.

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;//상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

```

<br>

2. fetch join 사용     
-> 한계:
* 페이징 불가능 (일 을 기준으로 페이질을 하는 게 아니라 다 를 기준으로 row가 생성됨)
* 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 
```java
@GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

 //OrderRepository   
    public List<Order> findAllWithItem() {
        return em.createQuery(
            //order가 컬렉션 페치 조인 때문에 중복 조회되는 것을 막기 위해 distinct 입력
            //db -> 객체로 매핑될 때 중복되는 것을 삭제해줌
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }

```

<br>

3. fetch join의 페이징 한계 극복
* ToOne(OneToOne, ManyToOne) 관계는 모두 페치조인 한다. ToOne 관계는 row수를 증가시
키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
* 컬렉션은 지연 로딩으로 조회한다.
    * 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size , @BatchSize 를 적용한다.
    * hibernate.default_batch_fetch_size: 글로벌 설정
    * @BatchSize: 개별 최적화 
    * 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.
    * size는 100~1000 사이를 선택하는 것을 권장
    
```java

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //(1) fetch join
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

//orderRepository
        public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

      @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; 
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; 

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()    
             //(2) 한번에 두 개의 order에 대한 orderItem 다 가져옴
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice; 
        private int count; 

        public OrderItemDto(OrderItem orderItem) {
            //(3) 한번에 두 개의 order에 대한 Item 다 가져옴
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
//-> 쿼리 총 3번 나감
```

최적화 옵션
```yml
  jpa: #띄어쓰기 2칸
    properties: #띄어쓰기 4칸
      hibernate: #띄어쓰기 6칸
        default_batch_fetch_size: 1000  #띄어쓰기 6칸
```

<br>

4. JPA에서 DTO 직접 조회   
-> N+1문제 발생
```java

  @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

//orderQueryRepository
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;
    /**
     * 컬렉션은 별도로 조회
     * Query: 루트 1번, 컬렉션 N 번
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();
        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행)  -> 조회된 order의 개수만큼 실행됨
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }
    
    /**
     * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query." +
                                "OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class).getResultList();
    } 
    
    /**
     * 1:N 관계인 orderItems 조회
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query" +
                                ".OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId",
                        OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}

//OrderQueryDto

@Data
@EqualsAndHashCode(of = "orderId")
public class OrderQueryDto {
    
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; 
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate,
                         OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}

//OrderItemQueryDto
@Data
public class OrderItemQueryDto {

    @JsonIgnore
    private Long orderId; //주문번호
    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
```

<br>

5. JPA에서 DTO 직접 조회 시 N+1 문제 해결
* Query: 루트 1번, 컬렉션 1번
* 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
* ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을 한꺼번에 조회
```java
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

//orderQueryRepositor
        public List<OrderQueryDto> findAllByDto_optimization() {

        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();
        //orderItem 컬렉션을 MAP 한방에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));
        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query" +
                                ".OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }
```