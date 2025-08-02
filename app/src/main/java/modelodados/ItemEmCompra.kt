package modelodados

data class ItemEmCompra(
    val nomeProduto: String = "",
    val quantidade: Int,
    var valorUnitario: Double = 0.0,
    var comprado: Boolean = false
)