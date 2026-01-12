package br.com.matheus.mypatrimony.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GraphicInfoDTO {
    private String title;
    private Object dataSet;
    private String locale;
    private String type;
}
