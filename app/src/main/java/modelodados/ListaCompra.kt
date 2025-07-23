package modelodados

class ListaCompra(val nome: CharSequence, val id: CharSequence, val numeroProdutos: CharSequence) {

    data class ListaCompra(
        val id: String,
        val nome: String,
        val numeroProdutos: Int = 0
    )

}