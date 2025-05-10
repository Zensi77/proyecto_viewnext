package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com.juanma.proyecto_vn.shared.Utils.enums.paymentMethodEnum;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@Builder
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductOrderEntity> productOrder;

    @Column(name = "total_price", nullable = false)
    private Double total_price;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "payment_method", nullable = false, columnDefinition = "enum('CREDIT_CARD', 'PAYPAL', 'GOOGLE_PAY', 'APPLE_PAY')")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private paymentMethodEnum paymentMethod = paymentMethodEnum.CREDIT_CARD;
}
