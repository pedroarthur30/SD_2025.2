package modelo

import java.io.Serializable

class ProdutoDigital(
    id: String,
    nome: String,
    preco: Double,
    val urlDownload: String
) : ItemVenda(id, nome, preco), Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}