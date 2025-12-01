package trabalho2

import modelo.ProdutoFisico
import java.rmi.Naming
import java.rmi.RemoteException

fun main() {
    val hostDoServidor = "localhost"
    val portaRMI = 1099
    val nomeDoServico = "CarrinhoService"

    try {
        val url = "//${hostDoServidor}:${portaRMI}/${nomeDoServico}"

        val carrinhoRemoto = Naming.lookup(url) as CarrinhoDeCompras

        println("Cliente: Conectado ao servidor! Objeto remoto encontrado.")
        println("-----------------------------------------------------")


        // 3. INVOCANDO MÉTODOS REMOTOS

        val produto1 = ProdutoFisico("P01", "Notebook Gamer", 7500.00, 2.5, "NG-XPS-001")
        val produto2 = ProdutoFisico("P02", "Mouse Óptico", 150.00, 0.2, "MS-LOG-G502")

        // Invocação Remota 1: adicionarProdutoFisico
        // (O RMI serializa 'produto1' e envia para o servidor)
        println("Cliente: Enviando '${produto1.nome}' para o carrinho...")
        carrinhoRemoto.adicionarProdutoFisico(produto1)

        // Invocação Remota 2: adicionarProdutoFisico
        println("Cliente: Enviando '${produto2.nome}' para o carrinho...")
        carrinhoRemoto.adicionarProdutoFisico(produto2)
        println("Cliente: Produtos enviados.")

        // Invocação Remota 3: verItensNoCarrinho
        println("\nCliente: Pedindo lista de itens do carrinho...")
        val itensAtuais = carrinhoRemoto.verItensNoCarrinho()
        println("Cliente: Itens recebidos (${itensAtuais.size}):")
        itensAtuais.forEach { item ->
            println("  -> ${item.nome} (R$ ${item.preco})")
        }
        println("-----------------------------------------------------")

        println("\nCliente: Finalizando a compra...")
        val total = carrinhoRemoto.finalizarCompra()
        println("Cliente: Compra finalizada! Total recebido do servidor: R$ $total")

        val itensPosCompra = carrinhoRemoto.verItensNoCarrinho()
        println("Cliente: Verificando carrinho após compra. Total de itens: ${itensPosCompra.size}")


    } catch (e: RemoteException) {
        println("Cliente: Erro de RMI ao comunicar com o servidor.")
        println("Verifique se o servidor está rodando.")
        e.printStackTrace()
    } catch (e: Exception) {
        println("Cliente: Erro inesperado.")
        e.printStackTrace()
    }
}