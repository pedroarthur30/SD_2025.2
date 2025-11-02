import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class ProdutoFisicoOutputStream(
    private val dados: Array<ProdutoFisico>,
    private val numeroDeObjetos: Int,
    private val destino: OutputStream
) : OutputStream() {

    private val dataOut = DataOutputStream(destino)

    init {
        if (numeroDeObjetos > dados.size || numeroDeObjetos < 0) {
            throw IllegalArgumentException(
                "O número de objetos a enviar ($numeroDeObjetos) é inválido " +
                        "para o tamanho do array (${dados.size})."
            )
        }

        try {
            enviarArrayDeDados()
        } catch (e: IOException) {
            println("Erro ao escrever dados no stream: ${e.message}")
        }
    }

    private fun enviarArrayDeDados() {

        var bytesEnviadosAcumulados = 0L
        for (i in 0 until numeroDeObjetos) {
            val produto = dados[i]

            // Atributo 1: nome (String)
            val nomeBytes = produto.nome.toByteArray(StandardCharsets.UTF_8)
            // Atributo 2: preco (Double - 8 bytes)
            val precoBytes = ByteBuffer.allocate(8).putDouble(produto.preco).array()
            // Atributo 3: peso (Double - 8 bytes)
            val pesoBytes = ByteBuffer.allocate(8).putDouble(produto.peso).array()

            val bytesParaGravarNome = 4 + nomeBytes.size
            val bytesParaGravarPreco = 8
            val bytesParaGravarPeso = 8

            val bytesDePayload = bytesParaGravarNome + bytesParaGravarPreco + bytesParaGravarPeso

            val bytesDeCabecalho = 4

            val totalBytesEsteObjeto = bytesDeCabecalho + bytesDePayload
            dataOut.writeInt(bytesDePayload)


            dataOut.writeInt(nomeBytes.size)
            dataOut.write(nomeBytes)
            dataOut.write(precoBytes)
            dataOut.write(pesoBytes)

            bytesEnviadosAcumulados += totalBytesEsteObjeto
            println("-> [${produto.nome}]: Total de bytes enviados = $totalBytesEsteObjeto")
        }

        dataOut.flush()
        println("--------------------------------------------------")
        println("-> TOTAL GERAL DE BYTES ENVIADOS: $bytesEnviadosAcumulados")
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        destino.write(b)
    }

    @Throws(IOException::class)
    override fun close() {
        dataOut.close()
        super.close()
    }

    @Throws(IOException::class)
    override fun flush() {
        dataOut.flush()
    }
}