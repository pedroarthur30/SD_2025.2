package modelo

import java.io.Serializable

class Servico(
    id: String,
    nome: String,
    preco: Double,
    val duracaoHoras: Int
) : ItemVenda(id, nome, preco), Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}