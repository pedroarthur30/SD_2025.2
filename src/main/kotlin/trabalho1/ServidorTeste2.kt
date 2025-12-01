package trabalho1

import java.net.ServerSocket
import java.io.IOException

fun main() {
    val porta = 9999
    println("--- Servidor de ECO rodando na porta $porta ---")

    try {
        ServerSocket(porta).use { servidorSocket ->
            while (true) {
                println("\nAguardando conexão do cliente...")

                // 2. Aguarda (bloqueia) até que um cliente se conecte
                val clienteSocket = servidorSocket.accept()

                clienteSocket.use { cliente ->
                    println("Cliente conectado: ${cliente.inetAddress.hostAddress}")

                    // 4. Pega o InputStream
                    val inputStream = cliente.inputStream

                    // 5. Lê TODOS os bytes que o cliente enviar
                    val bytesRecebidos = inputStream.readAllBytes()

                    println("\n--- DADOS RECEBIDOS ---")
                    println("Total de bytes recebidos: ${bytesRecebidos.size}")
                    println("Bytes (hex): ${bytesRecebidos.joinToString(" ") { "%02X".format(it) }}")

                    try {
                        println("Enviando ${bytesRecebidos.size} bytes de volta para o cliente...")

                        val outputStream = cliente.outputStream

                        outputStream.write(bytesRecebidos)
                        outputStream.flush()

                        println("Eco enviado com sucesso.")

                    } catch (e: IOException) {
                        println("Erro ao enviar eco para o cliente: ${e.message}")
                    }

                    println("--- Conexão do cliente fechada ---")
                }
            }
        }
    } catch (e: IOException) {
        println("Erro no servidor: ${e.message}")
    }

    println("--- Servidor desligado ---")
}