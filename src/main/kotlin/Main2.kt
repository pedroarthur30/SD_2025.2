import java.io.*


fun main() {
    println("=====================================================")
    println("INICIANDO TODOS OS TESTES DA QUESTÃO 3 (InputStream)")
    println("=====================================================")

    testeComMemoria()

    println("\n-----------------------------------------------------")

    testeComArquivo()
}

fun testeComMemoria() {
    println("--- Teste Q3 (Memória): Lendo de ByteArrayInputStream ---")

    val produtosOriginais = arrayOf(
        ProdutoFisico("P01", "Notebook Gamer", 7500.00, 2.5, "NG-XPS-001"),
        ProdutoFisico("P02", "Mouse Óptico", 150.00, 0.2, "MS-LOG-G502")
    )
    val buffer = ByteArrayOutputStream()

    // ESCRITA (Memória)
    println("--- (Memória.1) Escrevendo objetos no stream... ---")
    try {
        ProdutoFisicoOutputStream(
            dados = produtosOriginais,
            numeroDeObjetos = 2,
            destino = buffer,
        ).use { }
    } catch (e: IOException) {
        println("Erro na etapa de ESCRITA: ${e.message}")
        return
    }

    // LEITURA (Memória)
    val inputStream = ByteArrayInputStream(buffer.toByteArray())
    println("\n--- (Memória.2) Lendo objetos do stream... ---")
    val produtosLidos = mutableListOf<ProdutoFisico>()
    try {
        ProdutoFisicoInputStream(inputStream).use { pojoReader ->
            while (true) {
                val produtoLido = pojoReader.readProduto() ?: break
                println("-> Objeto lido: ${produtoLido.nome} (Preço: ${produtoLido.preco})")
                produtosLidos.add(produtoLido)
            }
        }
    } catch (e: IOException) {
        println("Erro na etapa de LEITURA: ${e.message}")
    }

    println("\n--- (Memória.3) Verificação ---")
    println("Objetos escritos: ${produtosOriginais.size}")
    println("Objetos lidos: ${produtosLidos.size}")
    if (produtosOriginais.size == produtosLidos.size &&
        produtosOriginais[0].nome == produtosLidos[0].nome &&
        produtosOriginais[1].preco == produtosLidos[1].preco) {
        println("\nSUCESSO! Os dados foram lidos da MEMÓRIA corretamente.")
    } else {
        println("\nFALHA! (Memória)")
    }
}

fun testeComArquivo() {
    println("--- Teste: Lendo de FileInputStream ---")

    val NOME_ARQUIVO = "q3_teste_arquivo.bin"

    val produtosOriginais = arrayOf(
        ProdutoFisico("P03", "Teclado Mecânico", 450.00, 1.1, "TK-RED-K550"),
        ProdutoFisico("P01", "Notebook Gamer", 7500.00, 2.5, "NG-XPS-001")
    )

    // ESCRITA (Arquivo)

    println("--- (Arquivo.1) Escrevendo objetos no arquivo '$NOME_ARQUIVO'... ---")
    try {
        ProdutoFisicoOutputStream(
            dados = produtosOriginais,
            numeroDeObjetos = 2,
            destino = FileOutputStream(NOME_ARQUIVO), // Escreve no arquivo
        ).use {  }
    } catch (e: IOException) {
        println("Erro na etapa de ESCRITA em ARQUIVO: ${e.message}")
        return
    }

    // LEITURA (Arquivo)

    println("\n--- (Arquivo.2) Lendo objetos do arquivo '$NOME_ARQUIVO'... ---")
    val produtosLidos = mutableListOf<ProdutoFisico>()
    try {

        val fileOrigem = FileInputStream(NOME_ARQUIVO)

        // Passa o arquivo de origem para o leitor
        ProdutoFisicoInputStream(fileOrigem).use { pojoReader ->
            while (true) {
                val produtoLido = pojoReader.readProduto() ?: break
                println("-> Objeto lido: ${produtoLido.nome} (Preço: ${produtoLido.preco})")
                produtosLidos.add(produtoLido)
            }
        }
    } catch (e: IOException) {
        println("Erro na etapa de LEITURA de ARQUIVO: ${e.message}")
    }

    println("\n--- (Arquivo.3) Verificação ---")
    println("Objetos escritos: ${produtosOriginais.size}")
    println("Objetos lidos: ${produtosLidos.size}")

    if (produtosOriginais.size == produtosLidos.size &&
        produtosOriginais[0].nome == produtosLidos[0].nome &&
        produtosOriginais[1].preco == produtosLidos[1].preco) {
        println("\nSUCESSO! Os dados foram lidos do ARQUIVO corretamente.")
    } else {
        println("\nFALHA! (Arquivo)")
    }

    File(NOME_ARQUIVO).delete()
    println("\nArquivo de teste '$NOME_ARQUIVO' removido.")
}