data class ProdutoFisico(
    override val id: String,
    override val nome: String,
    override val preco: Double,
    val peso: Double, // Atributo extra 1 (Double - 8 bytes)
    val sku: String   // Atributo extra 2 (String - tamanho variável)
) : ItemVenda(id, nome, preco)