package modelodados

data class ItemEmCompra(
    val nomeProduto: String = "",
    val quantidade: Int,
    var valorUnitario: Double,
    var comprado: Boolean = false
)