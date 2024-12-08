const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');

const app = express();
const upload = multer({ dest: 'uploads/' });

app.use(express.static(path.join(__dirname)));

// Função para ajustar os pesos
function adjustWeights(weights, bias, input, error, learningRate) {
    for (let j = 0; j < weights[0].length; j++) {
        for (let i = 0; i < weights.length; i++) {
            weights[i][j] += learningRate * error[j] * input[i];
        }
        bias[j] += learningRate * error[j];
    }
}

// Função para calcular o erro da camada oculta
function calculateHiddenError(outputError, weightsHiddenOutput, hiddenLayer, activationFunc) {
    return hiddenLayer.map((_, hiddenIdx) => {
        const errorSum = outputError.reduce((sum, err, outputIdx) => {
            return sum + err * weightsHiddenOutput[hiddenIdx][outputIdx];
        }, 0);
        return errorSum * activationFunc.derivative(hiddenLayer[hiddenIdx]);
    });
}

// Função para ativar uma camada da rede neural
function activateLayer(input, weights, bias, activationFunc) {
    return weights[0].map((_, col) =>
        activationFunc.func(
            input.reduce((sum, val, row) => sum + val * weights[row][col], bias[col])
        )
    );
}

// Função para validar o modelo (calcular matriz de confusão)
function validateModel(testData, weightsInputHidden, weightsHiddenOutput, biasHidden, biasOutput, uniqueClasses, activationFunc) {
    const inputs = testData.map(row => Object.values(row).slice(0, -1).map(Number));
    const actualClasses = testData.map(row => row.classe);

    const confusionMatrix = {};
    uniqueClasses.forEach(cls => {
        confusionMatrix[cls] = uniqueClasses.reduce((acc, c) => {
            acc[c] = 0; // Inicializa contagem com 0
            return acc;
        }, {});
    });

    inputs.forEach((input, idx) => {
        const hiddenLayer = activateLayer(input, weightsInputHidden, biasHidden, activationFunc);
        const outputLayer = activateLayer(hiddenLayer, weightsHiddenOutput, biasOutput, activationFunc);

        const predictedClass = uniqueClasses[outputLayer.indexOf(Math.max(...outputLayer))];
        const actualClass = actualClasses[idx];

        confusionMatrix[actualClass][predictedClass]++;
    });

    return confusionMatrix;
}

// Função para calcular a precisão com base na matriz de confusão
function calculateAccuracy(confusionMatrix) {
    const total = Object.values(confusionMatrix).reduce(
        (sum, row) => sum + Object.values(row).reduce((subSum, value) => subSum + value, 0),
        0
    );
    const correct = Object.keys(confusionMatrix).reduce(
        (sum, key) => sum + confusionMatrix[key][key],
        0
    );
    return ((correct / total) * 100).toFixed(2);
}

// Outras funções auxiliares
function parseCSV(filePath) {
    return new Promise((resolve, reject) => {
        const data = [];
        fs.createReadStream(filePath)
            .pipe(csv())
            .on('data', (row) => data.push(row))
            .on('end', () => resolve(data))
            .on('error', (err) => reject(err));
    });
}

function normalizeData(data) {
    const columns = Object.keys(data[0]).slice(0, -1);
    const stats = columns.map(col => ({
        min: Math.min(...data.map(row => parseFloat(row[col]))),
        max: Math.max(...data.map(row => parseFloat(row[col]))),
    }));

    return data.map(row => {
        const normalizedRow = {};
        columns.forEach((col, idx) => {
            const range = stats[idx].max - stats[idx].min || 1;
            normalizedRow[col] = (parseFloat(row[col]) - stats[idx].min) / range;
        });
        normalizedRow.classe = row.classe;
        return normalizedRow;
    });
}

function initializeMatrix(rows, cols) {
    return Array.from({ length: rows }, () =>
        Array.from({ length: cols }, () => Math.random() * 0.1 - 0.05)
    );
}

function getActivationFunction(type) {
    if (type === 'linear') {
        return {
            func: x => x / 10,
            derivative: () => 1 / 10,
        };
    } else if (type === 'logistic') {
        return {
            func: x => 1 / (1 + Math.exp(-x)),
            derivative: y => y * (1 - y),
        };
    } else if (type === 'tanh') {
        return {
            func: x => Math.tanh(x),
            derivative: y => 1 - Math.pow(y, 2),
        };
    }
    throw new Error('Função de ativação inválida');
}

function calculateHiddenNeurons(inputSize, outputSize, method = 'arithmetic') {
    if (method === 'arithmetic') {
        return Math.ceil((inputSize + outputSize) / 2);
    } else if (method === 'geometric') {
        return Math.ceil(Math.sqrt(inputSize * outputSize));
    }
    throw new Error('Método inválido para cálculo de neurônios ocultos');
}

function trainAndTestModel(trainData, testData, params) {
    const {
        numInputs, numOutputs, hiddenLayerSize, learningRate, epochs, activation, minError,
    } = params;

    const inputs = trainData.map(row => Object.values(row).slice(0, -1).map(Number));
    const outputs = trainData.map(row => row.classe);
    const uniqueClasses = [...new Set(outputs)];

    const weightsInputHidden = initializeMatrix(numInputs, hiddenLayerSize);
    const weightsHiddenOutput = initializeMatrix(hiddenLayerSize, numOutputs);
    const biasHidden = new Array(hiddenLayerSize).fill(0);
    const biasOutput = new Array(numOutputs).fill(0);

    const activationFunc = getActivationFunction(activation);
    const errorByEpoch = [];

    for (let epoch = 0; epoch < epochs; epoch++) {
        let totalError = 0;

        for (let i = 0; i < inputs.length; i++) {
            const input = inputs[i];
            const target = uniqueClasses.map(cls => (cls === outputs[i] ? 1 : 0));

            const hiddenLayer = activateLayer(input, weightsInputHidden, biasHidden, activationFunc);
            const outputLayer = activateLayer(hiddenLayer, weightsHiddenOutput, biasOutput, activationFunc);

            const outputError = target.map((t, idx) => t - outputLayer[idx]);
            const hiddenError = calculateHiddenError(outputError, weightsHiddenOutput, hiddenLayer, activationFunc);

            adjustWeights(weightsHiddenOutput, biasOutput, hiddenLayer, outputError, learningRate);
            adjustWeights(weightsInputHidden, biasHidden, input, hiddenError, learningRate);

            totalError += outputError.reduce((sum, err) => sum + Math.pow(err, 2), 0);
        }

        console.log(`Época ${epoch + 1}, Erro Total: ${totalError.toFixed(4)}`);
        errorByEpoch.push(totalError);
        if (totalError < minError) break;
    }

    const confusionMatrix = validateModel(testData, weightsInputHidden, weightsHiddenOutput, biasHidden, biasOutput, uniqueClasses, activationFunc);
    const accuracy = calculateAccuracy(confusionMatrix);

    return { confusionMatrix, accuracy, errorByEpoch };
}

app.post('/train', upload.fields([{ name: 'trainFile' }, { name: 'testFile' }]), async (req, res) => {
    try {
        const trainDataRaw = await parseCSV(req.files['trainFile'][0].path);
        const testDataRaw = await parseCSV(req.files['testFile'][0].path);

        if (!trainDataRaw.length || !testDataRaw.length) {
            throw new Error('Os arquivos CSV de treinamento ou teste estão vazios.');
        }

        const trainData = normalizeData(trainDataRaw);
        const testData = normalizeData(testDataRaw);

        const numInputs = Object.keys(trainData[0]).slice(0, -1).length;
        const uniqueClasses = [...new Set(trainData.map(row => row.classe))];
        const numOutputs = uniqueClasses.length;

        const hiddenLayerSize = parseInt(req.body.hiddenLayerSize) || 
        Math.ceil((numInputs + numOutputs) / 2);

        const { confusionMatrix, accuracy, errorByEpoch } = trainAndTestModel(trainData, testData, {
            numInputs,
            numOutputs,
            hiddenLayerSize,
            learningRate: parseFloat(req.body.learningRate),
            epochs: parseInt(req.body.epochs, 10),
            activation: req.body.activation,
            minError: parseFloat(req.body.minError || 0.01),
        });

        res.json({ confusionMatrix, accuracy, errorByEpoch });
    } catch (error) {
        console.error('Erro no treinamento:', error);
        res.status(500).json({ error: error.message });
    }
});

app.listen(3000, () => console.log('Servidor rodando em http://localhost:3000'));
