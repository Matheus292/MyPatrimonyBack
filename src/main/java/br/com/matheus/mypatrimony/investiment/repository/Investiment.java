package br.com.matheus.mypatrimony.investiment.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investiment")
public class Investiment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_id")
    private Active active;

    @ManyToOne
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @Column(name = "price_unit")
    private Double priceUnit;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "total")
    private Double total;

    @Temporal(TemporalType.DATE)
    @Column(name = "operation_date")
    private Date operationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "setlement_date")
    private Date settlementDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insertion_date")
    private Date insertionDate;

}
