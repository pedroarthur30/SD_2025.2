import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class ProdutoFisicoInputStream(
    private val origem: InputStream
) : InputStream() {

    private val dataIn = DataInputStream(origem)

    fun readProduto(): ProdutoFisico? {
        try {

            val bytesDePayload = dataIn.readInt()

            val nomeSize = dataIn.readInt()
            val nomeBytes = ByteArray(nomeSize)
            dataIn.readFully(nomeBytes)
            val nome = String(nomeBytes, StandardCharsets.UTF_8)

            val preco = dataIn.readDouble()

            val peso = dataIn.readDouble()

            return ProdutoFisico(
                id = "DESERIALIZED",
                nome = nome,
                preco = preco,
                peso = peso,
                sku = "DESERIALIZED"
            )

        } catch (e: EOFException) {
            return null
        } catch (e: IOException) {
            println("Erro ao deserializar o stream: ${e.message}")
            return null
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return origem.read()
    }

    @Throws(IOException::class)
    override fun close() {
        dataIn.close()
        super.close()
    }
}