package modelodados

data class ItemLista(
    val id: String = "",
    val nomeProduto: String = "",
    val quantidade: Int = 1,
    var selecionado: Boolean = false
)