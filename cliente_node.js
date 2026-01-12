const http = require('http');

// Configuração do Servidor
const options = {
    hostname: 'localhost',
    port: 8080,
    headers: { 'Content-Type': 'application/json' }
};

function enviar(metodo, caminho, dados = null) {
    return new Promise((resolve, reject) => {
        const reqOptions = { ...options, method: metodo, path: caminho };

        const req = http.request(reqOptions, (res) => {
            let corpo = '';
            res.on('data', (chunk) => { corpo += chunk; });
            res.on('end', () => {
                console.log(`[${metodo}] Status: ${res.statusCode}`);
                if (corpo) console.log(`   Resposta: ${corpo}`);
                resolve();
            });
        });

        req.on('error', (e) => {
            console.error(`Erro: ${e.message}`);
            reject(e);
        });

        if (dados) {
            req.write(JSON.stringify(dados));
        }
        req.end();
    });
}

async function main() {
    console.log("--- [NODE.JS] Iniciando Cliente ---");

    // 1. Adicionar Produto
    const produtoNode = {
        id: "JS-2026",
        nome: "Servidor em Node.js",
        preco: 300.50,
        peso: 1.5,
        sku: "NODE-BOX"
    };

    console.log(`\n-> Enviando: ${produtoNode.nome}`);
    await enviar('POST', '/carrinho', produtoNode);

    // 2. Listar Carrinho
    console.log("\n-> Consultando carrinho...");
    await enviar('GET', '/carrinho');

    // 3. Finalizar
    console.log("\n-> Finalizando compra...");
    await enviar('POST', '/carrinho/finalizar');
}

main();