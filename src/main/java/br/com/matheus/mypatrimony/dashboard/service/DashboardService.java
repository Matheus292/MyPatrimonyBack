package br.com.matheus.mypatrimony.dashboard.service;

import br.com.matheus.mypatrimony.dashboard.dto.CardInfoDTO;
import br.com.matheus.mypatrimony.dashboard.dto.DashboardInfoDTO;
import br.com.matheus.mypatrimony.dashboard.dto.GraphicInfoDTO;
import br.com.matheus.mypatrimony.dashboard.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private DashboardRepository repository;

    public ResponseEntity getDashboardInfo(){
        DashboardInfoDTO dash = new DashboardInfoDTO();
        dash.setCards(new ArrayList<>());
        dash.getCards().add(getValorInvestido());
        dash.getCards().add(getUltimaVerificacaoPatrimonio());
        dash.getCards().add(getUltimaVerificacaoPatrimonioDolar());

        dash.setGraphics(new ArrayList<>());
        dash.getGraphics().add(getEvolucaoPatrimonial());
        dash.getGraphics().add(getEvolucaoPatrimonialDolar());
        dash.getGraphics().add(getInvestimentByYear());
        dash.getGraphics().add(getDividendByYear());
        dash.getGraphics().add(getDividendByMonth());
        return ResponseEntity.ok(dash);
    }

    private CardInfoDTO getValorInvestido(){
        Double valorInvestido = repository.getTotalInvestido();
        return CardInfoDTO.builder()
                .order(3)
                .title("Total investido (R$)")
                .currency("BRL")
                .value(valorInvestido)
                .build();
    }

    private CardInfoDTO getUltimaVerificacaoPatrimonio(){
        Object[] obj = repository.valorPatrimonioAtual();
        return CardInfoDTO.builder().order(1)
                .title("Patrimônio (R$)")
                .value(obj[1])
                .currency("BRL")
                .date((Date) obj[0])
                .build();
    }

    private CardInfoDTO getUltimaVerificacaoPatrimonioDolar(){
        Object[] obj = repository.valorPatrimonialDolar();
        return CardInfoDTO.builder().order(2)
                .title("Patrimônio (U$)")
                .value(obj[1])
                .date((Date) obj[0])
                .currency("USD")
                .build();
    }

    private GraphicInfoDTO getEvolucaoPatrimonial(){
        List<Object[]> dados = repository.getEvolucaoPatrimonialReais();
        return GraphicInfoDTO.builder()
                .title("Evolução Patrimonial (R$)")
                .type("line")
                .locale("pt_BR")
                .dataSet(dados)
                .build();
    }
    private GraphicInfoDTO getEvolucaoPatrimonialDolar(){
        List<Object[]> dados = repository.getEvolucaoPatrimonialDolar();
        return GraphicInfoDTO.builder()
                .title("Evolução Patrimonial (U$)")
                .type("line")
                .dataSet(dados)
                .locale("en_US")
                .build();
    }

    private GraphicInfoDTO getInvestimentByYear(){
        List<Object[]> dados = repository.getInvestimentosPorAno();
        return GraphicInfoDTO.builder()
                .title("Investimento por ano")
                .type("bar")
                .dataSet(dados)
                .locale("pt_BR")
                .build();
    }

    private GraphicInfoDTO getDividendByYear(){
        List<Object[]> dados = repository.getDividendByYear();
        return GraphicInfoDTO.builder()
                .title("Lucros recebidos por ano")
                .type("bar")
                .dataSet(dados)
                .locale("pt_BR")
                .build();
    }

    private GraphicInfoDTO getDividendByMonth(){
        List<Object[]> dados = repository.getDividendByMonth();
        return GraphicInfoDTO.builder()
                .title("Lucros recebidos por mês")
                .type("bar")
                .dataSet(dados)
                .locale("pt_BR")
                .build();
    }


}
