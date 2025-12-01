package modelo

class Loja(
    val nome: String,
    private val catalogo: MutableList<ItemVenda> = mutableListOf()
) {
    fun adicionarProdutoAoCatalogo(item: ItemVenda) {
        catalogo.add(item)
    }

    fun getCatalogo(): List<ItemVenda> = catalogo.toList()
}