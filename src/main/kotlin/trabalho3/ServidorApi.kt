package trabalho3

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import modelo.ProdutoFisico
import java.io.InputStreamReader
import java.net.InetSocketAddress

val carrinhoGlobal = mutableListOf<ProdutoFisico>()
val gson = Gson()

fun main() {
    val porta = 8080
    // 1. Cria o servidor usando ferramenta nativa do Java (Zero Ktor!)
    val servidor = HttpServer.create(InetSocketAddress(porta), 0)

    println("--- Servidor API (Java Nativo) rodando na porta $porta ---")

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

    servidor.executor = null
    servidor.start()
}

// Classe que controla as requisições para /carrinho
class CarrinhoHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            when (exchange.requestMethod) {
                "GET" -> {
                    // Listar Itens
                    val json = gson.toJson(carrinhoGlobal)
                    enviarResposta(exchange, 200, json)
                    println("API: Lista enviada.")
                }
                "POST" -> {
                    // Adicionar Item
                    // Lê o corpo da requisição (JSON vindo do Python/Node)
                    val leitor = InputStreamReader(exchange.requestBody, "UTF-8")
                    val novoProduto = gson.fromJson(leitor, ProdutoFisico::class.java)

                    carrinhoGlobal.add(novoProduto)

                    enviarResposta(exchange, 201, "Produto adicionado!")
                    println("API: Adicionado ${novoProduto.nome}")
                }
                else -> enviarResposta(exchange, 405, "Método não suportado")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            enviarResposta(exchange, 500, "Erro interno: ${e.message}")
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