interface CarrinhoDeCompras {
    fun adicionarItem(item: ItemVenda)
    fun removerItem(itemId: String)
    fun calcularTotal(): Double
    fun finalizarCompra(): Boolean
}