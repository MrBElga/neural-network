const Neuronio = require('./Neuronio');

class MLP {
    constructor(camadaEntrada, camadaOculta, camadaSaida, funcaoAtivacao, taxaAprendizado, erroMinimo, maxInteracoes) {
        this.camadaEntrada = camadaEntrada;
        this.camadaOculta = Array.from({ length: camadaOculta }, () => new Neuronio(camadaEntrada.length));
        this.camadaSaida = Array.from({ length: camadaSaida }, () => new Neuronio(camadaOculta));
        this.funcaoAtivacao = funcaoAtivacao;
        this.taxaAprendizado = taxaAprendizado;
        this.erroMinimo = erroMinimo;
        this.maxInteracoes = maxInteracoes;
    }

    funcaoAtivacaoLogistica(net) {
        return 1 / (1 + Math.exp(-net));
    }

    funcaoAtivacaoLogisticaDerivada(saida) {
        return saida * (1 - saida);
    }

    calcularCamadaOculta(entradas) {
        return this.camadaOculta.map(neuronio => {
            neuronio.net = neuronio.pesos.reduce((sum, peso, i) => sum + peso * entradas[i], 0);
            neuronio.saida = this.funcaoAtivacaoLogistica(neuronio.net);
            return neuronio.saida;
        });
    }

    calcularCamadaSaida(saidasOcultas) {
        return this.camadaSaida.map(neuronio => {
            neuronio.net = neuronio.pesos.reduce((sum, peso, i) => sum + peso * saidasOcultas[i], 0);
            neuronio.saida = this.funcaoAtivacaoLogistica(neuronio.net);
            return neuronio.saida;
        });
    }

    treinar(entradasTreino, saidasEsperadas) {
        let errosPorEpoca = [];
        for (let epoca = 0; epoca < this.maxInteracoes; epoca++) {
            let erroTotal = 0;
            entradasTreino.forEach((entradas, idx) => {
                const saidasOcultas = this.calcularCamadaOculta(entradas);
                const saidas = this.calcularCamadaSaida(saidasOcultas);

                const erroSaida = saidasEsperadas[idx].map((esperado, i) => esperado - saidas[i]);
                erroTotal += erroSaida.reduce((sum, erro) => sum + Math.pow(erro, 2), 0);

                this.camadaSaida.forEach((neuronio, i) => {
                    neuronio.erro = erroSaida[i] * this.funcaoAtivacaoLogisticaDerivada(neuronio.saida);
                });

                this.camadaOculta.forEach((neuronio, i) => {
                    const erroOculta = this.camadaSaida.reduce((sum, neuronioSaida, j) => sum + neuronioSaida.erro * neuronioSaida.pesos[i], 0);
                    neuronio.erro = erroOculta * this.funcaoAtivacaoLogisticaDerivada(neuronio.saida);
                });

                this.camadaSaida.forEach(neuronio => neuronio.atualizarPesos(saidasOcultas, this.taxaAprendizado));
                this.camadaOculta.forEach(neuronio => neuronio.atualizarPesos(entradas, this.taxaAprendizado));
            });

            errosPorEpoca.push(erroTotal);
            if (erroTotal < this.erroMinimo) break;
        }
        return errosPorEpoca;
    }
}

module.exports = MLP;
