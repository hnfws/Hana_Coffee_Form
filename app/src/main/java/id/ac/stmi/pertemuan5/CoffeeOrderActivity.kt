package id.ac.stmi.pertemuan5

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class CoffeeOrderActivity : AppCompatActivity() {

    private val listPesanan = mutableListOf<OrderModel>()
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var lvRekapPesanan: ListView
    private lateinit var tvTotalCalculated: TextView

    private val varianMap = mapOf(
        "Cappuccino" to 22000,
        "Caffè Latte" to 20000,
        "Americano" to 18000,
        "Espresso" to 15000,
        "Mocha Macchiato" to 24000,
        "Dalgona Coffee" to 22000,
        "Chocolate Coffee" to 23000
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coffee_order)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCoffeeLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNamaPemesan = findViewById<EditText>(R.id.etNamaPemesan)
        val spVarianMinuman = findViewById<Spinner>(R.id.spVarianMinuman)
        val rbSmall = findViewById<RadioButton>(R.id.rbSmall)
        val rbMedium = findViewById<RadioButton>(R.id.rbMedium)
        val rbLarge = findViewById<RadioButton>(R.id.rbLarge)
        val containerSmall = findViewById<View>(R.id.containerSmall)
        val containerMedium = findViewById<View>(R.id.containerMedium)
        val containerLarge = findViewById<View>(R.id.containerLarge)

        val rbHot = findViewById<RadioButton>(R.id.rbHot)
        val rbIce = findViewById<RadioButton>(R.id.rbIce)
        val containerHot = findViewById<View>(R.id.containerHot)
        val containerIce = findViewById<View>(R.id.containerIce)

        val cbExtraShot = findViewById<CheckBox>(R.id.cbExtraShot)
        val cbBoba = findViewById<CheckBox>(R.id.cbBoba)
        val cbWhippedCream = findViewById<CheckBox>(R.id.cbWhippedCream)
        val cbOatMilk = findViewById<CheckBox>(R.id.cbOatMilk)
        val etCatatan = findViewById<EditText>(R.id.etCatatan)
        val btnProsesPesanan = findViewById<Button>(R.id.btnProsesPesanan)
        tvTotalCalculated = findViewById(R.id.tvTotalCalculated)
        lvRekapPesanan = findViewById(R.id.lvRekapPesanan)

        val daftarVarian = varianMap.keys.toTypedArray()
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item_coffee,
            daftarVarian
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_coffee)
        spVarianMinuman.adapter = spinnerAdapter

        val updateLivePrice = {
            val varianDipilih = spVarianMinuman.selectedItem?.toString() ?: "Cappuccino"
            val hargaDasar = varianMap[varianDipilih] ?: 22000

            val textUkuran = when {
                rbSmall.isChecked -> "Small (S)"
                rbLarge.isChecked -> "Large (L)"
                else -> "Medium (M)"
            }
            val hargaUkuran = when {
                textUkuran.contains("Small") || textUkuran == "S" -> 0
                textUkuran.contains("Large") || textUkuran == "L" -> 8000
                else -> 4000
            }

            var hargaTopping = 0
            if (cbExtraShot.isChecked) hargaTopping += 5000
            if (cbBoba.isChecked) hargaTopping += 4000
            if (cbWhippedCream.isChecked) hargaTopping += 3000
            if (cbOatMilk.isChecked) hargaTopping += 5000

            val total = hargaDasar + hargaUkuran + hargaTopping
            val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            tvTotalCalculated.text = rupiahFormat.format(total).replace(",00", "")
        }

        spVarianMinuman.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updateLivePrice()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val selectUkuran = { selected: RadioButton ->
            rbSmall.isChecked = (selected == rbSmall)
            rbMedium.isChecked = (selected == rbMedium)
            rbLarge.isChecked = (selected == rbLarge)
            updateLivePrice()
        }

        val selectSuhu = { selected: RadioButton ->
            rbHot.isChecked = (selected == rbHot)
            rbIce.isChecked = (selected == rbIce)
            updateLivePrice()
        }

        containerSmall.setOnClickListener { selectUkuran(rbSmall) }
        rbSmall.setOnClickListener { selectUkuran(rbSmall) }
        containerMedium.setOnClickListener { selectUkuran(rbMedium) }
        rbMedium.setOnClickListener { selectUkuran(rbMedium) }
        containerLarge.setOnClickListener { selectUkuran(rbLarge) }
        rbLarge.setOnClickListener { selectUkuran(rbLarge) }

        containerHot.setOnClickListener { selectSuhu(rbHot) }
        rbHot.setOnClickListener { selectSuhu(rbHot) }
        containerIce.setOnClickListener { selectSuhu(rbIce) }
        rbIce.setOnClickListener { selectSuhu(rbIce) }

        cbExtraShot.setOnCheckedChangeListener { _, _ -> updateLivePrice() }
        cbBoba.setOnCheckedChangeListener { _, _ -> updateLivePrice() }
        cbWhippedCream.setOnCheckedChangeListener { _, _ -> updateLivePrice() }
        cbOatMilk.setOnCheckedChangeListener { _, _ -> updateLivePrice() }

        updateLivePrice()

        orderAdapter = OrderAdapter(this, listPesanan)
        lvRekapPesanan.adapter = orderAdapter

        tambahSamplePesanan()

        btnProsesPesanan.setOnClickListener {
            val nama = etNamaPemesan.text.toString().trim()
            if (nama.isEmpty()) {
                etNamaPemesan.error = "Nama pemesan tidak boleh kosong"
                etNamaPemesan.requestFocus()
                Toast.makeText(this, "Silakan masukkan nama pemesan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val varianDipilih = spVarianMinuman.selectedItem.toString()
            val hargaDasar = varianMap[varianDipilih] ?: 22000

            val textUkuran = when {
                rbSmall.isChecked -> "Small (S)"
                rbLarge.isChecked -> "Large (L)"
                else -> "Medium (M)"
            }

            val (labelUkuran, hargaUkuran) = when {
                textUkuran.contains("Small") || textUkuran == "S" -> "Small (S)" to 0
                textUkuran.contains("Large") || textUkuran == "L" -> "Large (L)" to 8000
                else -> "Medium (M)" to 4000
            }

            val labelSuhu = if (rbIce.isChecked) "Ice (Dingin)" else "Hot (Panas)"

            val listTopping = mutableListOf<String>()
            var hargaTopping = 0

            if (cbExtraShot.isChecked) {
                listTopping.add("Extra Shot")
                hargaTopping += 5000
            }
            if (cbBoba.isChecked) {
                listTopping.add("Boba")
                hargaTopping += 4000
            }
            if (cbWhippedCream.isChecked) {
                listTopping.add("Whipped Cream")
                hargaTopping += 3000
            }
            if (cbOatMilk.isChecked) {
                listTopping.add("Oat Milk")
                hargaTopping += 5000
            }

            val catatan = etCatatan.text.toString().trim()
            val totalHarga = hargaDasar + hargaUkuran + hargaTopping

            val pesananBaru = OrderModel(
                namaPemesan = nama,
                varianMinuman = varianDipilih,
                ukuran = labelUkuran,
                suhu = labelSuhu,
                toppings = listTopping,
                catatan = catatan,
                totalHarga = totalHarga
            )

            listPesanan.add(0, pesananBaru)
            orderAdapter.notifyDataSetChanged()
            updateListViewHeight()

            Toast.makeText(this, "Pesanan $nama berhasil diproses!", Toast.LENGTH_SHORT).show()

            etNamaPemesan.text.clear()
            etCatatan.text.clear()
            cbExtraShot.isChecked = false
            cbBoba.isChecked = false
            cbWhippedCream.isChecked = false
            cbOatMilk.isChecked = false
            spVarianMinuman.setSelection(0)
            findViewById<RadioButton>(R.id.rbMedium).isChecked = true
            findViewById<RadioButton>(R.id.rbHot).isChecked = true
            etNamaPemesan.clearFocus()
            updateLivePrice()
        }
    }

    private fun tambahSamplePesanan() {
        listPesanan.add(
            OrderModel(
                namaPemesan = "Budi Santoso",
                varianMinuman = "Cappuccino",
                ukuran = "Medium (M)",
                suhu = "Hot (Panas)",
                toppings = listOf("Extra Shot", "Boba"),
                catatan = "Less sugar",
                totalHarga = 31000
            )
        )
        listPesanan.add(
            OrderModel(
                namaPemesan = "Siti Rahma",
                varianMinuman = "Caffè Latte",
                ukuran = "Large (L)",
                suhu = "Ice (Dingin)",
                toppings = listOf("Oat Milk"),
                catatan = "Extra ice",
                totalHarga = 33000
            )
        )
        orderAdapter.notifyDataSetChanged()
        updateListViewHeight()
    }

    private fun updateListViewHeight() {
        val adapter = lvRekapPesanan.adapter ?: return
        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, lvRekapPesanan)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(lvRekapPesanan.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }

        val params = lvRekapPesanan.layoutParams
        params.height = totalHeight + (lvRekapPesanan.dividerHeight * (adapter.count - 1)) + 32
        lvRekapPesanan.layoutParams = params
        lvRekapPesanan.requestLayout()
    }
}
