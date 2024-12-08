
# 🌟 Rede Neural Multicamadas (MLP)

Este projeto implementa uma **Rede Neural Multicamadas (MLP)** utilizando **JavaScript** para treinar e testar modelos baseados em dados fornecidos pelo usuário. A interface web permite configurar e executar treinamentos de forma intuitiva. 🧠✨

---

## 🚀 **Funcionalidades**
- 📂 **Upload de arquivos CSV**:
  - Carregue arquivos de **treinamento** e **teste** no formato `.csv`.
- ⚙️ **Configuração Personalizada**:
  - Taxa de aprendizado.
  - Número de épocas.
  - Escolha entre funções de ativação: Linear, Logística ou Tangente Hiperbólica.
  - Quantidade de neurônios na camada oculta.
  - Critério de parada baseado no erro mínimo.
- 📊 **Visualização dos Resultados**:
  - Matriz de confusão detalhada.
  - Acurácia do modelo.
  - Gráfico mostrando o erro por época.

---

## 🛠️ **Tecnologias Utilizadas**
### **Frontend**
- **HTML5**: Estrutura da interface.
- **CSS3**: Estilização responsiva e moderna.
- **JavaScript**: Manipulação do DOM e interação com o backend.
- **[Chart.js](https://www.chartjs.org/)**: Biblioteca para visualização de gráficos.

### **Backend**
- **[Node.js](https://nodejs.org/)**: Plataforma para execução do código backend.
- **[Express](https://expressjs.com/)**: Framework para criar o servidor HTTP.
- **[Multer](https://github.com/expressjs/multer)**: Middleware para upload de arquivos.
- **[csv-parser](https://www.npmjs.com/package/csv-parser)**: Biblioteca para leitura e parseamento de arquivos CSV.

---

## 📝 **Como Usar**
### 1️⃣ **Clone o Repositório**
Execute o seguinte comando no terminal:
```bash
git clone https://github.com/MrBElga/neural-network.git
```

### 2️⃣ **Instale as Dependências**
Navegue até o diretório do projeto e instale as dependências do Node.js:
```bash
npm install
```

### 3️⃣ **Inicie o Servidor**
Execute o servidor localmente:
```bash
node server.js
```

### 4️⃣ **Acesse o Frontend**
Abra o navegador e vá para:
```
http://localhost:3000
```

### 5️⃣ **Configure e Treine**
- Carregue os arquivos **treinamento.csv** e **teste.csv**.
- Ajuste os parâmetros conforme necessário.
- Clique em **Treinar** para executar o modelo.

---

## 📁 **Estrutura do Projeto**
- **`index.html`**: Interface do usuário para configuração e visualização dos resultados.
- **`style.css`**: Arquivo de estilos para uma aparência moderna e responsiva.
- **`server.js`**: Backend responsável por processar os arquivos CSV, treinar o modelo e retornar os resultados.
- **`MLP.js`**: Classe que implementa a Rede Neural Multicamadas.
- **`Neuronio.js`**: Classe para neurônios individuais usados na rede neural.
- **`base_treinamento.csv`**: Exemplo de dados para treinamento.
- **`base_teste.csv`**: Exemplo de dados para teste.

---

## 📊 **Resultados**
Os resultados exibidos incluem:
- **Matriz de Confusão**: Mostra como as previsões correspondem às classificações reais.
- **Acurácia (%)**: A porcentagem de previsões corretas feitas pelo modelo.
- **Gráfico de Erros por Época**: Demonstra a convergência do modelo ao longo do treinamento.



---







