package modelo

import java.io.Serializable

class ProdutoFisico(
    id: String,
    nome: String,
    preco: Double,
    val peso: Double, // Atributo extra 1 (Double - 8 bytes)
    val sku: String   // Atributo extra 2 (String - tamanho variável)
) : ItemVenda(id, nome, preco), Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}