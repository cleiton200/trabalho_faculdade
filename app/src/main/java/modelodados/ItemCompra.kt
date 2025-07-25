package modelodados

class ItemCompra {
    data class ItemCompra(
        val nomeProduto: String,
        val quantidade: Int,
        var valor: Double = 0.0,
        var marcado: Boolean = false
    )
}