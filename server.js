const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');

const app = express();
const upload = multer({ dest: 'uploads/' });

app.use(express.static(path.join(__dirname)));

// Função para ajustar os pesos
function ajustarPesos(pesos, bias, entrada, erro, taxaAprendizado) {
    for (let j = 0; j < pesos[0].length; j++) {
        for (let i = 0; i < pesos.length; i++) {
            pesos[i][j] += taxaAprendizado * erro[j] * entrada[i];
        }
        bias[j] += taxaAprendizado * erro[j];
    }
}

// Função para calcular o erro da camada oculta
function calcularErroOculto(erroSaida, pesosOcultoSaida, camadaOculta, funcaoAtivacao) {
    return camadaOculta.map((_, indiceOculta) => {
        const somaErro = erroSaida.reduce((soma, erro, indiceSaida) => {
            return soma + erro * pesosOcultoSaida[indiceOculta][indiceSaida];
        }, 0);
        return somaErro * funcaoAtivacao.derivada(camadaOculta[indiceOculta]);
    });
}

// Função para ativar uma camada da rede neural
function ativarCamada(entrada, pesos, bias, funcaoAtivacao) {
    return pesos[0].map((_, coluna) =>
        funcaoAtivacao.func(
            entrada.reduce((soma, valor, linha) => soma + valor * pesos[linha][coluna], bias[coluna])
        )
    );
}

// Função para validar o modelo (calcular matriz de confusão)
function validarModelo(dadosTeste, pesosEntradaOculta, pesosOcultaSaida, biasOculta, biasSaida, classesUnicas, funcaoAtivacao) {
    const entradas = dadosTeste.map(linha => Object.values(linha).slice(0, -1).map(Number));
    const classesReais = dadosTeste.map(linha => linha.classe);

    const matrizConfusao = {};
    classesUnicas.forEach(classe => {
        matrizConfusao[classe] = classesUnicas.reduce((acc, cls) => {
            acc[cls] = 0;
            return acc;
        }, {});
    });

    entradas.forEach((entrada, indice) => {
        const camadaOculta = ativarCamada(entrada, pesosEntradaOculta, biasOculta, funcaoAtivacao);
        const camadaSaida = ativarCamada(camadaOculta, pesosOcultaSaida, biasSaida, funcaoAtivacao);

        const classePrevista = classesUnicas[camadaSaida.indexOf(Math.max(...camadaSaida))];
        const classeReal = classesReais[indice];

        matrizConfusao[classeReal][classePrevista]++;
    });

    return matrizConfusao;
}

// Função para calcular a precisão com base na matriz de confusão
function calcularAcuracia(matrizConfusao) {
    const total = Object.values(matrizConfusao).reduce(
        (soma, linha) => soma + Object.values(linha).reduce((subSoma, valor) => subSoma + valor, 0),
        0
    );
    const corretos = Object.keys(matrizConfusao).reduce(
        (soma, chave) => soma + matrizConfusao[chave][chave],
        0
    );
    return ((corretos / total) * 100).toFixed(2);
}

// Outras funções auxiliares
function parsearCSV(caminhoArquivo) {
    return new Promise((resolver, rejeitar) => {
        const dados = [];
        fs.createReadStream(caminhoArquivo)
            .pipe(csv())
            .on('data', (linha) => dados.push(linha))
            .on('end', () => resolver(dados))
            .on('error', (erro) => rejeitar(erro));
    });
}

function normalizarDados(dados) {
    const colunas = Object.keys(dados[0]).slice(0, -1);
    const estatisticas = colunas.map(coluna => ({
        minimo: Math.min(...dados.map(linha => parseFloat(linha[coluna]))),
        maximo: Math.max(...dados.map(linha => parseFloat(linha[coluna]))),
    }));

    return dados.map(linha => {
        const linhaNormalizada = {};
        colunas.forEach((coluna, indice) => {
            const alcance = estatisticas[indice].maximo - estatisticas[indice].minimo || 1;
            linhaNormalizada[coluna] = (parseFloat(linha[coluna]) - estatisticas[indice].minimo) / alcance;
        });
        linhaNormalizada.classe = linha.classe;
        return linhaNormalizada;
    });
}

function inicializarMatriz(linhas, colunas) {
    return Array.from({ length: linhas }, () =>
        Array.from({ length: colunas }, () => Math.random() * 0.1 - 0.05)
    );
}

function obterFuncaoAtivacao(tipo) {
    if (tipo === 'linear') {
        return {
            func: x => x / 10,
            derivada: () => 1 / 10,
        };
    } else if (tipo === 'logistica') {
        return {
            func: x => 1 / (1 + Math.exp(-x)),
            derivada: y => y * (1 - y),
        };
    } else if (tipo === 'tangenteHiperbolica') {
        return {
            func: x => Math.tanh(x),
            derivada: y => 1 - Math.pow(y, 2),
        };
    }
    throw new Error('Função de ativação inválida');
}

app.post('/treinar', upload.fields([{ name: 'arquivoTreinamento' }, { name: 'arquivoTeste' }]), async (req, res) => {
    try {
        const dadosTreinamentoBrutos = await parsearCSV(req.files['arquivoTreinamento'][0].path);
        const dadosTesteBrutos = await parsearCSV(req.files['arquivoTeste'][0].path);
        const numNeuroniosOcultos = parseInt(req.body.camadaOculta, 10);

        if (!dadosTreinamentoBrutos.length || !dadosTesteBrutos.length) {
            throw new Error('Os arquivos CSV de treinamento ou teste estão vazios.');
        }

        // Normalizar dados
        const dadosTreinamento = normalizarDados(dadosTreinamentoBrutos);
        const dadosTeste = normalizarDados(dadosTesteBrutos);

        // Determinar números de entradas e saídas
        const numEntradas = Object.keys(dadosTreinamento[0]).slice(0, -1).length;
        const classesUnicas = [...new Set(dadosTreinamento.map(linha => linha.classe))];
        const numSaidas = classesUnicas.length;

        // Inicializar pesos e bias
        const pesosEntradaOculta = inicializarMatriz(numEntradas, numNeuroniosOcultos);
        const pesosOcultaSaida = inicializarMatriz(numNeuroniosOcultos, numSaidas);
        const biasOculta = new Array(numNeuroniosOcultos).fill(0);
        const biasSaida = new Array(numSaidas).fill(0);

        // Configurar função de ativação
        const funcaoAtivacao = obterFuncaoAtivacao(req.body.funcaoAtivacao);
        const errosPorEpoca = [];

        // Treinamento
        for (let epoca = 0; epoca < req.body.epocas; epoca++) {
            let erroTotal = 0;

            for (let i = 0; i < dadosTreinamento.length; i++) {
                const entrada = Object.values(dadosTreinamento[i]).slice(0, -1).map(Number);
                const esperado = classesUnicas.map(cls => (cls === dadosTreinamento[i].classe ? 1 : 0));

                const camadaOculta = ativarCamada(entrada, pesosEntradaOculta, biasOculta, funcaoAtivacao);
                const camadaSaida = ativarCamada(camadaOculta, pesosOcultaSaida, biasSaida, funcaoAtivacao);

                const erroSaida = esperado.map((t, idx) => t - camadaSaida[idx]);
                const erroOculto = calcularErroOculto(erroSaida, pesosOcultaSaida, camadaOculta, funcaoAtivacao);

                ajustarPesos(pesosOcultaSaida, biasSaida, camadaOculta, erroSaida, req.body.taxaAprendizado);
                ajustarPesos(pesosEntradaOculta, biasOculta, entrada, erroOculto, req.body.taxaAprendizado);

                erroTotal += erroSaida.reduce((soma, erro) => soma + Math.pow(erro, 2), 0);
            }

            console.log(`Época ${epoca + 1}, Erro Total: ${erroTotal.toFixed(4)}`);
            errosPorEpoca.push(erroTotal);
            if (erroTotal < req.body.erroMinimo) break;
        }

        // Validar modelo
        const matrizConfusao = validarModelo(dadosTeste, pesosEntradaOculta, pesosOcultaSaida, biasOculta, biasSaida, classesUnicas, funcaoAtivacao);
        const acuracia = calcularAcuracia(matrizConfusao);

        // Enviar todos os dados ao frontend
        res.json({
            numEntradas,
            numSaidas,
            matrizConfusao,
            acuracia,
            errosPorEpoca
        });
    } catch (erro) {
        console.error(erro);
        res.status(500).json({ erro: erro.message });
    }
});

app.listen(3000, () => {
    console.log('Servidor rodando na porta 3000.');
});
