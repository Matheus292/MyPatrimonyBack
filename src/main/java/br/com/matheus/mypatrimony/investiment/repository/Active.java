package br.com.matheus.mypatrimony.investiment.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "active")
public class Active implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investiment_type")
    private InvestimentType investimentType;

    @Column(name = "cnpj")
    private String cnpj;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insertion_date")
    private Date insertionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date")
    private Date lastModificationDate;

    @Column(name = "id_status")
    private Long idStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "active")
    private Set<Investiment> investiments;
}
