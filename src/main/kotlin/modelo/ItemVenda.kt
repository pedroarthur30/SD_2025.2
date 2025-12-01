package modelo

import java.io.Serializable

abstract class ItemVenda(
    open val id: String,
    open val nome: String,
    open val preco: Double
) : Serializable
