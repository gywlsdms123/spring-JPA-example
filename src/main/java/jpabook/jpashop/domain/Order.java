package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class              Order {
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    // ManyToOne, OneToOne 쓸 때는 LAZY 꼭 첨부할 것!!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    // 다쪽의 id를 주인으로 잡고 값을 바꿈 (FK가 있는 부분!!)

    // JPQL select o From order o; -> SQL select * from order
    // 이러면 order 행 다 가져옴... 100행있으면 100번 읽는다는 소리
    // 지연로딩을 쓰면 order만 들고 옴
    // 근데 조인 데이터를 들고 오고 싶다면 fetch join을 사용하면 된다

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    //XToOne의 기본 패치 전략이 EAGER임...
    // OneToX는 LAZY
    //
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [order, cancel]

    //==연관관계 메서드==//
    public void setMember(Member meber){
        this.member = member;
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }

    //== 조회 로직==//

    /**
     *
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        /*
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();*/

        int totalPrice = 0;
        for(OrderItem orderItem: orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
