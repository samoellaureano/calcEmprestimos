import React from 'react';
import './ResultTable.css';

const ResultTable = ({ results }) => {
    // metodo para aplicar mascara monetária
    const maskCurrency = (valor, locale = 'pt-BR', currency = 'BRL') => {
        return new Intl.NumberFormat(locale, {
            style: 'currency',
            currency: currency
        }).format(valor)
    }

    return (
        <div className="table-container">
            {(//{results.length > 0 &&(
                <table className="styled-table">
                    <thead>
                        <tr>
                            <th colSpan="3">Empréstimo</th>
                            <th colSpan="2">Parcela</th>
                            <th colSpan="2">Principal</th>
                            <th colSpan="3">Juros</th>
                        </tr>
                        <tr>
                            <th>Data Competência</th>
                            <th>Valor de Empréstimo</th>
                            <th>Saldo Devedor</th>
                            <th>Consolidada</th>
                            <th>Total</th>
                            <th>Amortização</th>
                            <th>Saldo</th>
                            <th>Provisão</th>
                            <th>Acumulado</th>
                            <th>Pago</th>
                        </tr>
                    </thead>
                    <tbody>
                        {results.map((result, index) => (
                            <tr key={index}>
                                <td>{result.dataCompetencia}</td>
                                <td>{maskCurrency(result.valorEmprestimo)}</td>
                                <td>{maskCurrency(result.saldoDevedor)}</td>
                                <td>{result.consolidada}</td>
                                <td>{maskCurrency(result.parcelaTotal)}</td>
                                <td>{maskCurrency(result.amortizacao)}</td>
                                <td>{maskCurrency(result.saldo)}</td>
                                <td>{maskCurrency(result.jurosProvisao)}</td>
                                <td>{maskCurrency(result.jurosAcumulado)}</td>
                                <td>{maskCurrency(result.jurosPago)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ResultTable;
