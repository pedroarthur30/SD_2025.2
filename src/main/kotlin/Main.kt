import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.Socket

fun main() {

    val produtos = arrayOf(
        ProdutoFisico("P01", "Notebook Gamer", 7500.00, 2.5, "NG-XPS-001"),
        ProdutoFisico("P02", "Mouse Óptico", 150.00, 0.2, "MS-LOG-G502"),
        ProdutoFisico("P03", "Teclado Mecânico", 450.00, 1.1, "TK-RED-K550")
    )

    println("Iniciando a escrita no stream...")

    try {

        val destino = ByteArrayOutputStream()

        ProdutoFisicoOutputStream(
            dados = produtos,
            numeroDeObjetos = 3,
            destino = destino
        ).use {

        }

        println("Escrita concluída.")

        val bytesResultado = destino.toByteArray()

        println("Total de bytes escritos: ${bytesResultado.size}")
        println("Bytes (hex): ${bytesResultado.joinToString(" ") { "%02X".format(it) }}")

    } catch (e: IOException) {
        println("Ocorreu um erro ao escrever no stream: ${e.message}")
    }


    // --- Teste (ii): Arquivo (FileOutputStream) ---

    try {

        val fileDestino = FileOutputStream("produtos.bin")

        ProdutoFisicoOutputStream(
            dados = produtos,
            numeroDeObjetos = 3,
            destino = fileDestino
        ).use { pojoStream ->
        }

        println("Arquivo 'produtos.bin' criado com sucesso.")

    } catch (e: IOException) {
        println("Erro no Teste (ii): ${e.message}")
    }


    // --- Teste (iii): Servidor Remoto (TCP) ---


    try {

        Socket("localhost", 9999).use { socketDestino ->

            println("Conectado ao servidor...")

            val destinoRede = socketDestino.getOutputStream()

            val pojoStreamRede = ProdutoFisicoOutputStream(
                dados = produtos,
                numeroDeObjetos = 2,
                destino = destinoRede
            )

            println("Dados enviados ao servidor.")

        }

    } catch (e: ConnectException) {
        println("!!! ERRO NO TESTE (iii): Não foi possível conectar.")
        println("!!! VOCÊ RODOU O ServidorTeste.kt PRIMEIRO?")
    } catch (e: IOException) {
        println("Erro no Teste (iii): ${e.message}")
    }
}