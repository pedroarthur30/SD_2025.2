const http = require('http');
const readline = require('readline');

// Configuração da interface de leitura (Teclado)
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

const options = {
    hostname: 'localhost',
    port: 8080,
    headers: { 'Content-Type': 'application/json' }
};

// FUNÇÕES AUXILIARES

// Promessa para perguntar ao usuário (Simula o input() do Python)
function perguntar(pergunta) {
    return new Promise((resolve) => {
        rl.question(pergunta, (resposta) => {
            resolve(resposta);
        });
    });
}

// Função para enviar requisições e devolver os dados
function requisicao(metodo, caminho, dados = null) {
    return new Promise((resolve, reject) => {
        const reqOptions = { ...options, method: metodo, path: caminho };

        const req = http.request(reqOptions, (res) => {
            let corpo = '';
            res.on('data', (chunk) => { corpo += chunk; });
            res.on('end', () => {
                // Tenta converter a resposta para JSON
                try {
                    const json = corpo ? JSON.parse(corpo) : null;
                    resolve({ status: res.statusCode, dados: json });
                } catch (e) {
                    resolve({ status: res.statusCode, dados: corpo });
                }
            });
        });

        req.on('error', (e) => {
            console.error(`✗ Erro de conexão: ${e.message}`);
            reject(e);
        });

        if (dados) {
            req.write(JSON.stringify(dados));
        }
        req.end();
    });
}

// --- FUNÇÕES DO MENU (Igual ao Python) ---

async function listarProdutosDisponiveis() {
    console.log("\n=== PRODUTOS DISPONÍVEIS ===");
    try {
        const { dados } = await requisicao('GET', '/produtos');

        if (!dados || dados.length === 0) {
            console.log("Nenhum produto disponível!");
            return;
        }

        console.log(`\nTotal de produtos: ${dados.length}`);
        console.log("-".repeat(50));

        dados.forEach((p, index) => {
            console.log(`\n${index + 1}. [${p.id}] ${p.nome}`);
            console.log(`   Preço: R$ ${p.preco.toFixed(2)}`);

            if (p.peso) console.log(`   Tipo: Físico | Peso: ${p.peso}kg | SKU: ${p.sku}`);
            else if (p.urlDownload) console.log(`   Tipo: Digital | Download: ${p.urlDownload}`);
            else if (p.duracaoHoras) console.log(`   Tipo: Serviço | Duração: ${p.duracaoHoras}h`);
        });
    } catch (e) { console.error(e); }
}

async function buscarProdutoPorId() {
    console.log("\n=== BUSCAR PRODUTO ===");
    const id = await perguntar("Digite o ID do produto: ");

    const { status, dados } = await requisicao('GET', `/produtos/buscar?id=${id}`);

    if (status === 200) {
        console.log(`\n✓ Produto encontrado: ${dados.nome} - R$ ${dados.preco.toFixed(2)}`);
    } else {
        console.log("✗ Produto não encontrado!");
    }
}

async function filtrarPorTipo() {
    console.log("\n=== FILTRAR POR TIPO ===");
    console.log("1. Produtos Físicos");
    console.log("2. Produtos Digitais");
    console.log("3. Serviços");

    const op = await perguntar("Escolha o tipo: ");
    const mapa = { "1": "fisico", "2": "digital", "3": "servico" };
    const tipo = mapa[op];

    if (!tipo) {
        console.log("Opção inválida!");
        return;
    }

    const { dados } = await requisicao('GET', `/produtos/tipo?tipo=${tipo}`);
    console.log(`\n✓ Encontrados ${dados.length} produto(s):`);
    dados.forEach(p => console.log(`   - [${p.id}] ${p.nome}`));
}

async function adicionarDoCatalogo() {
    console.log("\n=== ADICIONAR DO CATÁLOGO ===");
    const id = await perguntar("Digite o ID do produto: ");

    const { status, dados } = await requisicao('POST', `/carrinho/adicionar?id=${id}`);

    if (status === 201) console.log("✓ Produto adicionado ao carrinho!");
    else if (status === 404) console.log("✗ Produto não encontrado!");
    else console.log(`✗ Erro: ${JSON.stringify(dados)}`);
}

async function verCarrinho() {
    console.log("\n=== MEU CARRINHO ===");
    const { dados } = await requisicao('GET', '/carrinho');

    if (!dados || dados.length === 0) {
        console.log("Carrinho vazio!");
        return;
    }

    let total = 0;
    dados.forEach((item, i) => {
        console.log(`${i + 1}. ${item.nome} - R$ ${item.preco.toFixed(2)}`);
        total += item.preco;
    });

    console.log("=".repeat(30));
    console.log(`TOTAL: R$ ${total.toFixed(2)}`);
}

async function adicionarCustomizado() {
    console.log("\n=== ADICIONAR PRODUTO CUSTOMIZADO ===");
    console.log("1 - Físico, 2 - Digital, 3 - Serviço");
    const tipo = await perguntar("Escolha: ");

    const id = await perguntar("ID: ");
    const nome = await perguntar("Nome: ");
    const preco = parseFloat(await perguntar("Preço: "));

    let produto = { id, nome, preco };

    if (tipo === "1") {
        produto.peso = parseFloat(await perguntar("Peso (kg): "));
        produto.sku = await perguntar("SKU: ");
    } else if (tipo === "2") {
        produto.urlDownload = await perguntar("URL: ");
    } else if (tipo === "3") {
        produto.duracaoHoras = parseInt(await perguntar("Duração (h): "));
    }

    const { status } = await requisicao('POST', '/carrinho', produto);
    if (status === 201) console.log("✓ Produto adicionado!");
    else console.log("✗ Erro ao adicionar.");
}

async function limparCarrinho() {
    const conf = await perguntar("Tem certeza? (s/n): ");
    if (conf.toLowerCase() === 's') {
        await requisicao('DELETE', '/carrinho');
        console.log("✓ Carrinho limpo.");
    }
}

async function finalizarCompra() {
    const conf = await perguntar("Finalizar compra? (s/n): ");
    if (conf.toLowerCase() === 's') {
        const { dados } = await requisicao('POST', '/carrinho/finalizar');
        console.log(`\n✓ ${dados.mensagem}`);
        console.log(`✓ Total pago: R$ ${dados.total.toFixed(2)}`);
    } else {
        console.log("Cancelado.");
    }
}

async function main() {
    console.log("--- [NODE.JS] Cliente Iniciado ---");

    while (true) {
        console.log("\n" + "=".repeat(40));
        console.log("1. Listar catálogo");
        console.log("2. Buscar por ID");
        console.log("3. Filtrar por Tipo");
        console.log("4. Adicionar do Catálogo (pelo ID)");
        console.log("5. Ver Carrinho");
        console.log("6. Adicionar Customizado");
        console.log("7. Limpar Carrinho");
        console.log("8. Finalizar Compra");
        console.log("9. Sair");
        console.log("=".repeat(40));

        const opcao = await perguntar("\nEscolha: ");

        switch (opcao) {
            case "1": await listarProdutosDisponiveis(); break;
            case "2": await buscarProdutoPorId(); break;
            case "3": await filtrarPorTipo(); break;
            case "4": await adicionarDoCatalogo(); break;
            case "5": await verCarrinho(); break;
            case "6": await adicionarCustomizado(); break;
            case "7": await limparCarrinho(); break;
            case "8": await finalizarCompra(); break;
            case "9":
                console.log("Saindo...");
                rl.close();
                return;
            default: console.log("Opção inválida!");
        }
    }
}

main();