class Neuronio {
    constructor(numPesos) {
        this.net = 0;
        this.saida = 0;
        this.erro = 0;
        this.pesos = Array.from({ length: numPesos }, () => Math.random() * 0.2 - 0.1);
    }

    atualizarPesos(entradas, taxaAprendizado) {
        this.pesos = this.pesos.map((peso, i) => peso + taxaAprendizado * this.erro * entradas[i]);
    }
}

module.exports = Neuronio;