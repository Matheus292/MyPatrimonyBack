package br.com.matheus.mypatrimony.investiment.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investiment_type")
public class InvestimentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Double weight;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insertion_date")
    private Date insertionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date")
    private Date lastModificationDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "investimentType")
    private Set<Active> actives;
}
