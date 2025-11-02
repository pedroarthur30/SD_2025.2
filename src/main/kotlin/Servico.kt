data class Servico(
    override val id: String,
    override val nome: String,
    override val preco: Double,
    val duracaoHoras: Int
) : ItemVenda(id, nome, preco)