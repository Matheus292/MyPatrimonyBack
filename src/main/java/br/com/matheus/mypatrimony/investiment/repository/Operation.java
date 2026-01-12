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
@Table(name = "operation")
public class Operation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insertion_date")
    private Date insertionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date")
    private Date lastModificationDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "operation")
    private Set<Investiment> investiments;

}
