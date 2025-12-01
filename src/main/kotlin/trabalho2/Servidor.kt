package trabalho2

import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.RemoteException

fun main() {
    val portaRMI = 1099
    val nomeDoServico = "CarrinhoService"

    try {

        LocateRegistry.createRegistry(portaRMI)
        println("Servidor: RMI Registry iniciado na porta $portaRMI.")

        val carrinho = CarrinhoServidor("cliente_default")

        Naming.rebind("//localhost:$portaRMI/$nomeDoServico", carrinho)

        println("Servidor: Objeto '$nomeDoServico' registrado com sucesso.")
        println("Servidor está pronto e aguardando invocações remotas...")

    } catch (e: RemoteException) {
        println("Servidor: Erro ao iniciar ou registrar o serviço.")
        e.printStackTrace()
    } catch (e: Exception) {
        println("Servidor: Erro inesperado.")
        e.printStackTrace()
    }
}