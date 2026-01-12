package br.com.matheus.mypatrimony.dashboard.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Double getTotalInvestido() {
        final StringBuilder query = new StringBuilder();
        query.append("select sum (")
                .append("case when i.exchange_id isnull then (i.price_unit * i.amount)")
                .append(" else (i.price_unit * i.amount * (select price from exchange where id = i.exchange_id limit 1))")
                .append("  end) as total ")
                .append("   from investiment as i ")
                .append("   inner join active a")
                .append("   on i.active_id = a.id")
                .append("   inner join investment_type it")
                .append("   on it.id = a.investiment_type")
                .append("   where i.bill_id in  (select id from bill where local_id in (select id from local where name <> 'BINANCE' ))")
                .append("   and i.operation_id  in (1,3)");

        Object r = entityManager.createNativeQuery(query.toString())
                .getSingleResult();

        return Objects.nonNull(r) ? ((Number) r).doubleValue() : 0.0;
    }

    public Object[] valorPatrimonioAtual() {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" v.date_verification AS dataVerificacao, ")
                .append(" SUM( ")
                .append("     CASE bv.coin_id ")
                .append("         WHEN 1 THEN bv.value ")
                .append("         WHEN 2 THEN bv.value * e.value_target ")
                .append("         ELSE 0 ")
                .append("     END ")
                .append(" ) AS valor ")
                .append("FROM verification v ")
                .append("INNER JOIN bill_verification bv ")
                .append("    ON v.id = bv.verification_id ")
                .append("LEFT JOIN exchange e ")
                .append("    ON e.id = v.exchange_id ")
                .append("GROUP BY v.date_verification ")
                .append("ORDER BY dataVerificacao DESC ")
                .append("LIMIT 1 ");


        Object[] result = (Object[]) entityManager
                .createNativeQuery(sql.toString())
                .getSingleResult();

        return result;
    }

    public Object[] valorPatrimonialDolar() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(" v.date_verification AS dataVerificacao, ")
                .append(" SUM( ")
                .append("     CASE bv.coin_id ")
                .append("         WHEN 1 THEN bv.value / ( ")
                .append("             SELECT e.value_target ")
                .append("             FROM exchange e ")
                .append("             WHERE e.id = v.exchange_id ")
                .append("             LIMIT 1 ")
                .append("         ) ")
                .append("         WHEN 2 THEN bv.value ")
                .append("         ELSE 0 ")
                .append("     END ")
                .append(" ) AS valor ")
                .append("FROM verification v ")
                .append("INNER JOIN bill_verification bv ")
                .append("    ON v.id = bv.verification_id ")
                .append("GROUP BY v.date_verification ")
                .append("ORDER BY dataVerificacao DESC limit 1");

        Object[] result = (Object[]) entityManager
                .createNativeQuery(sql.toString())
                .getSingleResult();

        return result;
    }

    public List<Object[]> getEvolucaoPatrimonialReais() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" v.date_verification AS \"Data Verificação\", ")
                .append(" SUM( ")
                .append("     ROUND( ")
                .append("         CASE bv.coin_id ")
                .append("             WHEN 1 THEN bv.value ")
                .append("             WHEN 2 THEN bv.value * ( ")
                .append("                 SELECT e.value_target ")
                .append("                 FROM exchange e ")
                .append("                 WHERE e.id = v.exchange_id ")
                .append("                 LIMIT 1 ")
                .append("             ) ")
                .append("             ELSE 0 ")
                .append("         END, ")
                .append("         2 ")
                .append("     ) ")
                .append(" ) AS \"Valor (R$)\" ")
                .append("FROM bill_verification bv ")
                .append("INNER JOIN verification v ")
                .append("    ON v.id = bv.verification_id ")
                .append("INNER JOIN bill b ")
                .append("    ON b.id = bv.bill_id ")
                .append("INNER JOIN local l ")
                .append("    ON l.id = b.local_id ")
                .append("INNER JOIN bill_type bt ")
                .append("    ON bt.id = b.bill_type ")
                .append("GROUP BY v.date_verification ")
                .append("ORDER BY \"Data Verificação\" ASC ");

        List<Object[]> result = (List<Object[]>) entityManager
                .createNativeQuery(sql.toString())
                .getResultList();

        return result;

    }

    public List<Object[]> getEvolucaoPatrimonialDolar() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" v.date_verification AS \"Data Verificação\", ")
                .append(" ROUND( ")
                .append("     SUM( ")
                .append("         CASE bv.coin_id ")
                .append("             WHEN 1 THEN bv.value / ( ")
                .append("                 SELECT e.value_target ")
                .append("                 FROM exchange e ")
                .append("                 WHERE e.id = v.exchange_id ")
                .append("                 LIMIT 1 ")
                .append("             ) ")
                .append("             WHEN 2 THEN bv.value ")
                .append("             ELSE 0 ")
                .append("         END ")
                .append("     ), ")
                .append("     2 ")
                .append(" ) AS \"Valor ($)\" ")
                .append("FROM verification v ")
                .append("INNER JOIN bill_verification bv ")
                .append("    ON v.id = bv.verification_id ")
                .append("GROUP BY v.date_verification ")
                .append("ORDER BY \"Data Verificação\" ASC ");


        List<Object[]> result = (List<Object[]>) entityManager
                .createNativeQuery(sql.toString())
                .getResultList();

        return result;

    }


    public List<Object[]> getInvestimentosPorAno() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" (CASE ")
                .append("     WHEN operation_date IS NOT NULL ")
                .append("         THEN to_char(operation_date, 'YYYY') ")
                .append("     ELSE to_char(settlement_date, 'YYYY') ")
                .append(" END) AS \"Ano\", ")
                .append(" SUM( ")
                .append("     CASE ")
                .append("         WHEN exchange_id IS NULL ")
                .append("             THEN ROUND(total, 2) ")
                .append("         ELSE ")
                .append("             ROUND( ")
                .append("                 total * ( ")
                .append("                     SELECT e.price ")
                .append("                     FROM exchange e ")
                .append("                     WHERE e.id = exchange_id ")
                .append("                     LIMIT 1 ")
                .append("                 ), ")
                .append("                 2 ")
                .append("             ) ")
                .append("     END ")
                .append(" ) AS \"Total\" ")
                .append("FROM investiment ")
                .append("GROUP BY \"Ano\" ")
                .append("ORDER BY \"Ano\" ASC ");

        List<Object[]> result = (List<Object[]>) entityManager
                .createNativeQuery(sql.toString())
                .getResultList();

        return result;

    }

    public List<Object[]> getDividendByYear() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" to_char(income_date, 'YYYY') AS \"Ano\", ")
                .append(" SUM( ")
                .append("     CASE ")
                .append("         WHEN coin_id = 1 ")
                .append("             THEN ROUND(total, 2) ")
                .append("         ELSE ")
                .append("             ROUND( ")
                .append("                 total * ( ")
                .append("                     SELECT e.price ")
                .append("                     FROM exchange e ")
                .append("                     WHERE e.id = exchange_id ")
                .append("                     LIMIT 1 ")
                .append("                 ), ")
                .append("                 2 ")
                .append("             ) ")
                .append("     END ")
                .append(" ) AS \"Total\" ")
                .append("FROM income ")
                .append("GROUP BY \"Ano\" ")
                .append("ORDER BY \"Ano\" ASC ");


        List<Object[]> result = (List<Object[]>) entityManager
                .createNativeQuery(sql.toString())
                .getResultList();

        return result;

    }

    public List<Object[]> getDividendByMonth() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(" to_char(income_date, 'YYYY-MM') AS \"Data\", ")
                .append(" SUM( ")
                .append("     CASE ")
                .append("         WHEN coin_id = 1 ")
                .append("             THEN ROUND(total, 2) ")
                .append("         ELSE ")
                .append("             ROUND( ")
                .append("                 total * ( ")
                .append("                     SELECT e.price ")
                .append("                     FROM exchange e ")
                .append("                     WHERE e.id = exchange_id ")
                .append("                     LIMIT 1 ")
                .append("                 ), ")
                .append("                 2 ")
                .append("             ) ")
                .append("     END ")
                .append(" ) AS \"Total\" ")
                .append("FROM income ")
                .append("GROUP BY to_char(income_date, 'YYYY-MM') ")
                .append("ORDER BY \"Data\" ASC ");



        List<Object[]> result = (List<Object[]>) entityManager
                .createNativeQuery(sql.toString())
                .getResultList();

        return result;

    }




}