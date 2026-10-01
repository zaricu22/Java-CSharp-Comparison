// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

package shop.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer;

    @Column(nullable = false)
    private LocalDate placedOn;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineEntity> lines = new ArrayList<>();

    protected OrderEntity() {
        // required by JPA
    }

    public OrderEntity(String customer, LocalDate placedOn) {
        this.customer = customer;
        this.placedOn = placedOn;
    }

    public void addLine(ProductEntity product, int quantity) {
        lines.add(new OrderLineEntity(this, product, quantity, product.getPrice()));
    }

    public BigDecimal total() {
        return lines.stream().map(OrderLineEntity::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public LocalDate getPlacedOn() {
        return placedOn;
    }

    public List<OrderLineEntity> getLines() {
        return lines;
    }
}
