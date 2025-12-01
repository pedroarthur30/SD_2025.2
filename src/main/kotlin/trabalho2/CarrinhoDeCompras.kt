package trabalho2

import modelo.ItemVenda
import modelo.ProdutoFisico
import java.rmi.Remote
import java.rmi.RemoteException

interface CarrinhoDeCompras : Remote {

    @Throws(RemoteException::class)
    fun adicionarProdutoFisico(produto: ProdutoFisico)

    @Throws(RemoteException::class)
    fun removerItem(itemId: String)

    @Throws(RemoteException::class)
    fun verItensNoCarrinho(): List<ItemVenda>

    @Throws(RemoteException::class)
    fun finalizarCompra(): Double
}