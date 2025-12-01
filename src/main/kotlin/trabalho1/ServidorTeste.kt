package trabalho1

import java.net.ServerSocket
import java.io.IOException

fun main() {
    val porta = 9999

    try {
        // 1. Cria um socket de servidor na porta 9999
        ServerSocket(porta).use { servidorSocket ->
            println("--- Servidor rodando na porta $porta ---")
            println("Aguardando conexão do cliente...")

            // 2. Aguarda (bloqueia) até que um cliente se conecte
            val clienteSocket = servidorSocket.accept()

            // 3. Cliente conectou. O .use garante que a conexão dele será fechada
            clienteSocket.use { cliente ->
                println("Cliente conectado: ${cliente.inetAddress.hostAddress}")

                // 4. Pega o InputStream (de onde LER os dados)
                val inputStream = cliente.inputStream

                // 5. Lê TODOS os bytes que o cliente enviar
                val bytesRecebidos = inputStream.readAllBytes()

                println("\n--- DADOS RECEBIDOS ---")
                println("Total de bytes recebidos: ${bytesRecebidos.size}")
                // Imprime os bytes em hexadecimal, igual ao nosso teste original
                println("Bytes (hex): ${bytesRecebidos.joinToString(" ") { "%02X".format(it) }}")
                println("--- Conexão do cliente fechada ---")
            }
        }
    } catch (e: IOException) {
        println("Erro no servidor: ${e.message}")
    }

    println("--- Servidor desligado ---")
}