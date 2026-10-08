package id.ac.stmi.pertemuan5

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(
    context: Context,
    private val orders: List<OrderModel>
) : ArrayAdapter<OrderModel>(context, 0, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_rekap_pesanan, parent, false
        )

        val order = getItem(position) ?: return view

        val tvItemJudul = view.findViewById<TextView>(R.id.tvItemJudul)
        val tvItemDetail = view.findViewById<TextView>(R.id.tvItemDetail)
        val tvItemTopping = view.findViewById<TextView>(R.id.tvItemTopping)
        val tvItemCatatan = view.findViewById<TextView>(R.id.tvItemCatatan)
        val tvItemHarga = view.findViewById<TextView>(R.id.tvItemHarga)

        tvItemJudul.text = "${order.namaPemesan} • ${order.varianMinuman}"
        tvItemDetail.text = "Ukuran: ${order.ukuran} | Suhu: ${order.suhu}"

        if (order.toppings.isNotEmpty()) {
            tvItemTopping.visibility = View.VISIBLE
            tvItemTopping.text = "Topping: " + order.toppings.joinToString(", ")
        } else {
            tvItemTopping.visibility = View.VISIBLE
            tvItemTopping.text = "Topping: Tanpa Topping"
        }

        if (order.catatan.isNotBlank()) {
            tvItemCatatan.visibility = View.VISIBLE
            tvItemCatatan.text = "Catatan: ${order.catatan}"
        } else {
            tvItemCatatan.visibility = View.GONE
        }

        val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvItemHarga.text = rupiahFormat.format(order.totalHarga).replace(",00", "")

        return view
    }
}
