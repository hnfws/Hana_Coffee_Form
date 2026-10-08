package id.ac.stmi.pertemuan5

data class OrderModel(
    val namaPemesan: String,
    val varianMinuman: String,
    val ukuran: String,
    val suhu: String,
    val toppings: List<String>,
    val catatan: String,
    val totalHarga: Int
)
