import React, {useState} from 'react';
import ResultTable from './../resultTable/ResultTable';
import './Calculator.css';

const baseUrl = window.location.origin;

const Calculator = () => {
    const [dataInicial, setDataInicial] = useState('');
    const [dataFinal, setDataFinal] = useState('');
    const [primeiroPagamento, setPrimeiroPagamento] = useState('');
    const [valorEmprestimo, setValorEmprestimo] = useState('');
    const [taxaJuros, setTaxaJuros] = useState('');
    const [results, setResults] = useState([]);

    const validateFields = () => {
        if (!dataInicial || !dataFinal || !primeiroPagamento || !valorEmprestimo || !taxaJuros) {
            alert('Preencha todos os campos!');
            return false;
        }
        return true;
    }

    // Campo de valor do empréstimo
    const addMaskMoney = (e) => {
        let onlyDigits = e.target.value
            .split("")
            .filter(s => /\d/.test(s))
            .join("")
            .padStart(3, "0");
        let digitsFloat = onlyDigits.slice(0, -2) + "." + onlyDigits.slice(-2);
        setValorEmprestimo(maskCurrency(digitsFloat));
    }

    const maskCurrency = (valor, locale = 'pt-BR', currency = 'BRL') => {
        return new Intl.NumberFormat(locale, {
            style: 'currency',
            currency: currency
        }).format(valor)
    }

    // Campo de taxa de juros
    const addMaskPercent = (e) => {
        let onlyDigits = e.target.value
            .split("")
            .filter(s => /\d/.test(s))
            .join("");

        let paddedDigits = onlyDigits.padStart(3, '0');

        let integerPart = paddedDigits.slice(0, -2);
        let fractionalPart = paddedDigits.slice(-2);

        const percentageValue = parseFloat(`${integerPart}.${fractionalPart}`) / 100;

        setTaxaJuros(maskPercent(percentageValue));
    }

    const handleKeyPress = (e) => {
        if (e.keyCode === 8) {
            let value = e.target.value.replace(/[^0-9]/g, '');

            value = value.slice(0, -1);

            if (value.length === 0 || value === '00') {
                e.target.value = maskPercent(0);
                return;
            }

            const decimalValue = parseFloat(value) / 100;

            setTaxaJuros(maskPercentHandler(decimalValue));
        } else {
            addMaskPercent(e);
        }
    }

    const maskPercent = (valor, locale = 'pt-BR') => {
        return new Intl.NumberFormat(locale, {
            style: 'percent',
            minimumFractionDigits: 2
        }).format(valor)
    }

    const maskPercentHandler = (e) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'percent'
        }).format(e);
    }

    // Validação de datas, data final deve ser maior que a data inicial
    const validateEndDate = (e) => {
        const date = new Date(e.target.value);
        const initialDate = new Date(dataInicial);

        if (date < initialDate) {
            alert('Data final deve ser maior que a data inicial!');
            return;
        }
        setDataFinal(e.target.value);
    }

    // A data de primeiro pagamento deve ser maior que a data inicial e menor que a data final.
    const validateFirstPayment = (e) => {
        const date = new Date(e.target.value);
        const initialDate = new Date(dataInicial);
        const finalDate = new Date(dataFinal);

        if (date < initialDate || date > finalDate) {
            alert('Data de primeiro pagamento deve ser maior que a data inicial e menor que a data final!');
            return;
        }
        setPrimeiroPagamento(e.target.value);
    }

    // Função para calcular
    const handleCalculate = () => {
        if (!validateFields()) {
            return;
        }

        const data = {
            dataInicial,
            dataFinal,
            primeiroPagamento,
            valorEmprestimo: parseFloat(valorEmprestimo.replace(/\./g, '').replace(',', '.').replace('R$', '').trim()),
            taxaJuros: parseFloat(taxaJuros.replace(/\./g, '').replace(',', '.').replace('%', '').trim())
        };

        console.log(data);

        fetch(`${baseUrl}/calcLoan/calculate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        })
            .then(response => response.json())
            .then(data => {
                setResults(data);
            })
            .catch((error) => {
                    console.error('Error:', error);
                }
            );
    }

    return (
        <div className='divCalculator'>
            <h1>Calculadora de Empréstimos</h1>
            <div className="fieldsCalc">
                <div>
                    <label>
                        Data Inicial:
                    </label>
                    <input
                        type="date"
                        value={dataInicial}
                        onChange={(e) => setDataInicial(e.target.value)}
                    />
                </div>
                <div>
                    <label>
                        Data Final:
                    </label>
                    <input
                        type="date"
                        value={dataFinal}
                        onChange={(e) => validateEndDate(e)}
                    />
                </div>
                <div>
                    <label>
                        Primeiro Pagamento:
                    </label>
                    <input
                        type="date"
                        value={primeiroPagamento}
                        onChange={(e) => validateFirstPayment(e)}
                    />
                </div>
                <div>
                    <label>
                        Valor do Empréstimo:
                    </label>
                    <input
                        type="text"
                        value={valorEmprestimo}
                        onChange={(e) => setValorEmprestimo(e.target.value)}
                        onKeyUp={(e) => addMaskMoney(e)}
                    />
                </div>
                <div>
                    <label>
                        Taxa de Juros:
                    </label>
                    <input
                        type="text"
                        value={taxaJuros}
                        onChange={(e) => setTaxaJuros(e.target.value)}
                        onKeyUp={(e) => handleKeyPress(e)}
                    />
                </div>
                <div className='divButton'>
                    <button onClick={handleCalculate}>Calcular</button>
                </div>
            </div>
            <ResultTable results={results}/>
        </div>
    );
};

export default Calculator;
