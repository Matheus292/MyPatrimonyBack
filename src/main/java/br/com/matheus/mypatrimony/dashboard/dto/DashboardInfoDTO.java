package br.com.matheus.mypatrimony.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DashboardInfoDTO {
    private List<CardInfoDTO> cards;
    private List<GraphicInfoDTO> graphics;
}
