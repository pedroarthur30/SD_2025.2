data class ProdutoDigital(
    override val id: String,
    override val nome: String,
    override val preco: Double,
    val urlDownload: String
) : ItemVenda(id, nome, preco)