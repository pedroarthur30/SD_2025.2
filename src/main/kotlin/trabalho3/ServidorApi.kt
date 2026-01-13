package trabalho3

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import modelo.*
import java.io.File
import java.io.InputStreamReader
import java.net.InetSocketAddress

val carrinhoGlobal = mutableListOf<ItemVenda>()
val produtosDisponiveis = mutableListOf<ItemVenda>()
val gson = Gson()

fun main() {
    val porta = 8080
    
    // Inicializa produtos disponíveis na loja
    inicializarProdutos()
    
    // 1. Cria o servidor usando ferramenta nativa do Java (Zero Ktor!)
    val servidor = HttpServer.create(InetSocketAddress(porta), 0)

    println("--- Servidor API (Java Nativo) rodando na porta $porta ---")
    println("Produtos disponíveis cadastrados: ${produtosDisponiveis.size}")

    // 2. Define o que acontece quando acessar "/carrinho"
    servidor.createContext("/carrinho", CarrinhoHandler())

    // 3. Define o que acontece quando acessar "/carrinho/finalizar"
    servidor.createContext("/carrinho/finalizar") { exchange ->
        if (exchange.requestMethod == "POST") {
            val total = carrinhoGlobal.sumOf { it.preco }
            carrinhoGlobal.clear()

            val respostaMap = mapOf("mensagem" to "Compra finalizada", "total" to total)
            val json = gson.toJson(respostaMap)

            enviarResposta(exchange, 200, json)
            println("API: Compra finalizada. Total: $total")
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 4. Lista todos os produtos disponíveis
    servidor.createContext("/produtos") { exchange ->
        if (exchange.requestMethod == "GET") {
            val json = gson.toJson(produtosDisponiveis)
            enviarResposta(exchange, 200, json)
            println("API: Lista de produtos disponíveis enviada.")
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 5. Busca produto por ID
    servidor.createContext("/produtos/buscar") { exchange ->
        if (exchange.requestMethod == "GET") {
            val query = exchange.requestURI.query
            val id = query?.split("=")?.get(1)
            
            if (id != null) {
                val produto = produtosDisponiveis.find { it.id == id }
                if (produto != null) {
                    val json = gson.toJson(produto)
                    enviarResposta(exchange, 200, json)
                    println("API: Produto $id encontrado.")
                } else {
                    enviarResposta(exchange, 404, "{\"erro\": \"Produto não encontrado\"}")
                }
            } else {
                enviarResposta(exchange, 400, "{\"erro\": \"ID não fornecido\"}")
            }
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 6. Filtrar produtos por tipo
    servidor.createContext("/produtos/tipo") { exchange ->
        if (exchange.requestMethod == "GET") {
            val query = exchange.requestURI.query
            val tipo = query?.split("=")?.get(1)
            
            val produtosFiltrados = when (tipo) {
                "fisico" -> produtosDisponiveis.filterIsInstance<ProdutoFisico>()
                "digital" -> produtosDisponiveis.filterIsInstance<ProdutoDigital>()
                "servico" -> produtosDisponiveis.filterIsInstance<Servico>()
                else -> produtosDisponiveis
            }
            
            val json = gson.toJson(produtosFiltrados)
            enviarResposta(exchange, 200, json)
            println("API: Produtos filtrados por tipo '$tipo': ${produtosFiltrados.size}")
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 7. Adicionar produto do catálogo ao carrinho
    servidor.createContext("/carrinho/adicionar") { exchange ->
        if (exchange.requestMethod == "POST") {
            val query = exchange.requestURI.query
            val id = query?.split("=")?.get(1)
            
            if (id != null) {
                val produto = produtosDisponiveis.find { it.id == id }
                if (produto != null) {
                    carrinhoGlobal.add(produto)
                    enviarResposta(exchange, 201, "{\"mensagem\": \"Produto adicionado ao carrinho\"}")
                    println("API: Produto ${produto.nome} adicionado ao carrinho.")
                } else {
                    enviarResposta(exchange, 404, "{\"erro\": \"Produto não encontrado\"}")
                }
            } else {
                enviarResposta(exchange, 400, "{\"erro\": \"ID não fornecido\"}")
            }
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 8. Documentação Swagger/OpenAPI
    servidor.createContext("/swagger") { exchange ->
        if (exchange.requestMethod == "GET") {
            try {
                val swaggerFile = File("swagger.yaml")
                if (swaggerFile.exists()) {
                    val conteudo = swaggerFile.readText()
                    exchange.responseHeaders.set("Content-Type", "application/x-yaml; charset=UTF-8")
                    exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
                    exchange.sendResponseHeaders(200, conteudo.toByteArray(Charsets.UTF_8).size.toLong())
                    val os = exchange.responseBody
                    os.write(conteudo.toByteArray(Charsets.UTF_8))
                    os.close()
                    println("API: Documentação Swagger enviada.")
                } else {
                    enviarResposta(exchange, 404, "{\"erro\": \"Arquivo swagger.yaml não encontrado\"}")
                }
            } catch (e: Exception) {
                enviarResposta(exchange, 500, "{\"erro\": \"Erro ao ler documentação: ${e.message}\"}")
            }
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 9. Swagger UI - Interface web para visualizar a documentação
    servidor.createContext("/swagger-ui") { exchange ->
        if (exchange.requestMethod == "GET") {
            val swaggerHtml = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Carrinho de Compras API - Documentação</title>
                    <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5.10.0/swagger-ui.css" />
                    <style>
                        body { margin: 0; padding: 0; }
                    </style>
                </head>
                <body>
                    <div id="swagger-ui"></div>
                    <script src="https://unpkg.com/swagger-ui-dist@5.10.0/swagger-ui-bundle.js"></script>
                    <script src="https://unpkg.com/swagger-ui-dist@5.10.0/swagger-ui-standalone-preset.js"></script>
                    <script>
                        window.onload = function() {
                            SwaggerUIBundle({
                                url: '/swagger',
                                dom_id: '#swagger-ui',
                                deepLinking: true,
                                presets: [
                                    SwaggerUIBundle.presets.apis,
                                    SwaggerUIStandalonePreset
                                ],
                                plugins: [
                                    SwaggerUIBundle.plugins.DownloadUrl
                                ],
                                layout: "StandaloneLayout"
                            });
                        };
                    </script>
                </body>
                </html>
            """.trimIndent()
            
            exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
            val bytes = swaggerHtml.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            val os = exchange.responseBody
            os.write(bytes)
            os.close()
            println("API: Swagger UI acessado.")
        } else {
            enviarResposta(exchange, 405, "Método não permitido")
        }
    }

    // 10. Rota raiz - informações da API
    servidor.createContext("/") { exchange ->
        if (exchange.requestMethod == "GET" && exchange.requestURI.path == "/") {
            val info = """
                {
                    "nome": "Carrinho de Compras API",
                    "versao": "1.0.0",
                    "descricao": "API REST para gerenciamento de carrinho de compras",
                    "documentacao": "/swagger-ui",
                    "endpoints": {
                        "produtos": "/produtos",
                        "carrinho": "/carrinho",
                        "swagger": "/swagger",
                        "swagger-ui": "/swagger-ui"
                    }
                }
            """.trimIndent()
            enviarResposta(exchange, 200, info)
            println("API: Informações da API enviadas.")
        } else {
            enviarResposta(exchange, 404, "{\"erro\": \"Endpoint não encontrado\"}")
        }
    }

    println("Documentação Swagger disponível em: http://localhost:$porta/swagger-ui")
    servidor.executor = null
    servidor.start()
}

fun inicializarProdutos() {
    // Produtos Físicos
    produtosDisponiveis.add(ProdutoFisico("PF-001", "Notebook Dell", 3500.0, 2.5, "NB-DELL-001"))
    produtosDisponiveis.add(ProdutoFisico("PF-002", "Mouse Logitech", 150.0, 0.2, "MS-LOG-002"))
    produtosDisponiveis.add(ProdutoFisico("PF-003", "Teclado Mecânico", 450.0, 0.8, "KB-MEC-003"))
    produtosDisponiveis.add(ProdutoFisico("PF-004", "Monitor LG 27\"", 1200.0, 5.0, "MON-LG-004"))
    produtosDisponiveis.add(ProdutoFisico("PF-005", "Webcam HD", 280.0, 0.3, "WC-HD-005"))
    
    // Produtos Digitais
    produtosDisponiveis.add(ProdutoDigital("PD-001", "Curso de Kotlin", 299.0, "https://cursos.com/kotlin"))
    produtosDisponiveis.add(ProdutoDigital("PD-002", "E-book Python Avançado", 89.0, "https://ebooks.com/python"))
    produtosDisponiveis.add(ProdutoDigital("PD-003", "Software de Edição", 599.0, "https://software.com/editor"))
    produtosDisponiveis.add(ProdutoDigital("PD-004", "Jogo Indie", 45.0, "https://games.com/indie"))
    produtosDisponiveis.add(ProdutoDigital("PD-005", "Pacote de Plugins", 199.0, "https://plugins.com/pack"))
    
    // Serviços
    produtosDisponiveis.add(Servico("SV-001", "Consultoria em TI", 500.0, 4))
    produtosDisponiveis.add(Servico("SV-002", "Manutenção de PC", 150.0, 2))
    produtosDisponiveis.add(Servico("SV-003", "Desenvolvimento Web", 2500.0, 40))
    produtosDisponiveis.add(Servico("SV-004", "Suporte Técnico", 100.0, 1))
    produtosDisponiveis.add(Servico("SV-005", "Instalação de Software", 80.0, 1))
}

// Classe que controla as requisições para /carrinho
class CarrinhoHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            when (exchange.requestMethod) {
                "GET" -> {
                    // Listar Itens do Carrinho
                    val json = gson.toJson(carrinhoGlobal)
                    enviarResposta(exchange, 200, json)
                    println("API: Lista do carrinho enviada (${carrinhoGlobal.size} itens).")
                }
                "POST" -> {
                    // Adicionar Item customizado ao carrinho
                    // Lê o corpo da requisição (JSON vindo do Python/Node)
                    val leitor = InputStreamReader(exchange.requestBody, "UTF-8")
                    val body = leitor.readText()
                    
                    // Tenta identificar o tipo do produto pelo conteúdo do JSON
                    val item = when {
                        body.contains("peso") && body.contains("sku") -> 
                            gson.fromJson(body, ProdutoFisico::class.java)
                        body.contains("urlDownload") -> 
                            gson.fromJson(body, ProdutoDigital::class.java)
                        body.contains("duracaoHoras") -> 
                            gson.fromJson(body, Servico::class.java)
                        else -> 
                            gson.fromJson(body, ProdutoFisico::class.java) // Default
                    }

                    carrinhoGlobal.add(item)

                    enviarResposta(exchange, 201, "{\"mensagem\": \"Produto adicionado!\"}")
                    println("API: Adicionado ${item.nome} ao carrinho")
                }
                "DELETE" -> {
                    // Limpar carrinho
                    carrinhoGlobal.clear()
                    enviarResposta(exchange, 200, "{\"mensagem\": \"Carrinho limpo\"}")
                    println("API: Carrinho limpo.")
                }
                else -> enviarResposta(exchange, 405, "Método não suportado")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            enviarResposta(exchange, 500, "{\"erro\": \"Erro interno: ${e.message}\"}")
        }
    }
}

fun enviarResposta(exchange: HttpExchange, codigo: Int, texto: String) {
    val bytes = texto.toByteArray(Charsets.UTF_8)
    exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
    exchange.sendResponseHeaders(codigo, bytes.size.toLong())
    val os = exchange.responseBody
    os.write(bytes)
    os.close()
}