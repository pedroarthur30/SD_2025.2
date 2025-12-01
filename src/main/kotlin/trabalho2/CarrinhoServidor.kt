package trabalho2

import modelo.ItemVenda
import modelo.ProdutoFisico
import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject

class CarrinhoServidor(
    private val idCliente: String
) : UnicastRemoteObject(), CarrinhoDeCompras {

    private val itens: MutableList<ItemVenda> = mutableListOf()

    init {
        println("Novo objeto trabalho2.CarrinhoServidor criado para o cliente '$idCliente'.")
    }

    @Throws(RemoteException::class)
    override fun adicionarProdutoFisico(produto: ProdutoFisico) {
        itens.add(produto)
        println("Servidor: Adicionado '${produto.nome}' ao carrinho de '$idCliente'.")
    }

    @Throws(RemoteException::class)
    override fun removerItem(itemId: String) {
        println("Servidor: Recebido pedido para remover item '$itemId'.")
        val itemParaRemover = itens.find { it.id == itemId }
        if (itemParaRemover != null) {
            itens.remove(itemParaRemover)
        }
    }

    @Throws(RemoteException::class)
    override fun verItensNoCarrinho(): List<ItemVenda> {
        println("Servidor: Enviando lista de ${itens.size} itens para '$idCliente'.")
        return itens.toList()
    }

    @Throws(RemoteException::class)
    override fun finalizarCompra(): Double {
        val total = itens.sumOf { it.preco }
        println("Servidor: Compra finalizada para '$idCliente'. Total: $total.")

        // Limpa o carrinho no servidor após a compra
        itens.clear()

        return total
    }
}