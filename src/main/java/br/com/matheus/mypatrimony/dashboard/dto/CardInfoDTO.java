package br.com.matheus.mypatrimony.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CardInfoDTO {
    private String title;
    private Object value;
    private String currency;
    private Date date;
    private Integer order;
}
