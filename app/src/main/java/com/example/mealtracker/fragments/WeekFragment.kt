package com.example.mealtracker.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mealtracker.R
import com.example.mealtracker.databinding.FragmentWeekBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class WeekFragment : Fragment() {

    private lateinit var db: DatabaseReference
    private lateinit var binding: FragmentWeekBinding
    private lateinit var userId: String
    private lateinit var authenticaion: FirebaseAuth
    lateinit var pieChart: PieChart
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        authenticaion = FirebaseAuth.getInstance()
        userId = authenticaion.currentUser?.uid.toString()
        binding = FragmentWeekBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_week, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromDataBase("10-12-2022")
    }


    private fun getDataFromDataBase(date: String) {
        db = Firebase.database.reference
        val uid = userId
        val myRef = db.child("Users").child(uid)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var fiber = 0
                var protein = 0
                var fat = 0
                var calories = 0
                var cholestrol = 0
//                calendar.add(Calendar.DAY_OF_MONTH, -1)
                var weekdate = SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH).format(calendar.time)
                val seta: MutableSet<String> = mutableSetOf()
                for (count in 1..7) {
                    seta.add(weekdate)
                    Log.d("Inside TIMES", weekdate)
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    weekdate = SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH).format(calendar.time)
                }
                for (count in 1..7) {
                    Log.d("Inside TIMES", weekdate)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                for (dates in snapshot.children) {
                    Log.d("Inside TIMES", dates.key.toString())
                    if (seta.contains(dates.key)) {
                        for (times in dates.children) {
                            Log.d("Inside TIMES", "${times.value}")
                            val nutrients = times.child("foodNutrients")
                            Log.d("Inside week Nutrients", "${nutrients.value}")
                            cholestrol += nutrients.child("chocdf").getValue<Int>()!!
                            calories += nutrients.child("enerc_KCAL").getValue<Int>()!!
                            fat += nutrients.child("fat").getValue<Int>()!!
                            fiber += nutrients.child("fibtg").getValue<Int>()!!
                            protein += nutrients.child("procnt").getValue<Int>()!!
                        }
                    }
                    Log.d(
                        "TOTAL NUTRIENTS",
                        "onDataChange: $calories $fiber $protein $fat $cholestrol"
                    )
                }
                if (isAdded)// This {@link androidx.fragment.app.Fragment} class method is responsible to check if the your view is attached to the Activity or not
                {
                    pieChart = binding.pieChart
//                    getDataFromFireBase(todaysDate)
                    // on below line we are setting user percent value,
                    // setting description as enabled and offset for pie chart
                    pieChart.setUsePercentValues(true)
                    pieChart.description.isEnabled = false
                    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

                    // on below line we are setting drag for our pie chart
                    pieChart.dragDecelerationFrictionCoef = 0.95f

                    // on below line we are setting hole
                    // and hole color for pie chart
                    pieChart.isDrawHoleEnabled = true
                    pieChart.setHoleColor(Color.WHITE)

                    // on below line we are setting circle color and alpha
                    pieChart.setTransparentCircleColor(Color.WHITE)
                    pieChart.setTransparentCircleAlpha(110)

                    // on  below line we are setting hole radius
                    pieChart.holeRadius = 58f
                    pieChart.transparentCircleRadius = 61f

                    // on below line we are setting center text
                    pieChart.setDrawCenterText(true)

                    // on below line we are setting
                    // rotation for our pie chart
                    pieChart.rotationAngle = 0f

                    // enable rotation of the pieChart by touch
                    pieChart.isRotationEnabled = true
                    pieChart.isHighlightPerTapEnabled = true

                    // on below line we are setting animation for our pie chart
//                    pieChart.animateY(1400, Easing.EaseInOutQuad)

                    // on below line we are disabling our legend for pie chart
                    pieChart.legend.isEnabled = false
                    pieChart.setEntryLabelColor(Color.WHITE)
                    pieChart.setEntryLabelTextSize(12f)

                    pieChart.animateY(1400, Easing.EaseInOutQuad)
                    val entries: ArrayList<PieEntry> = ArrayList()
                    entries.add(PieEntry(fiber.toFloat()))
                    entries.add(PieEntry(protein.toFloat()))
                    entries.add(PieEntry(fat.toFloat()))
                    entries.add(PieEntry(calories.toFloat()))
                    entries.add(PieEntry(cholestrol.toFloat()))

                    val dataSet = PieDataSet(entries, "DAILY Intake")
                    dataSet.setDrawIcons(false)

                    // on below line we are setting slice for pie
                    dataSet.sliceSpace = 6f
                    dataSet.iconsOffset = MPPointF(0f, 40f)
                    dataSet.selectionShift = 10f

                    // add a lot of colors to list
                    val colors: ArrayList<Int> = ArrayList()
                    colors.add(resources.getColor(R.color.teal_200))
                    colors.add(resources.getColor(R.color.yellow))
                    colors.add(resources.getColor(R.color.red))
                    colors.add(resources.getColor(R.color.material_dynamic_primary70))
                    colors.add(resources.getColor(R.color.green_c))

                    // on below line we are setting colors.
                    dataSet.colors = colors

                    // on below line we are setting pie data set
                    val data = PieData(dataSet)
                    data.setValueFormatter(PercentFormatter())
                    data.setValueTextSize(15f)
                    data.setValueTypeface(Typeface.DEFAULT_BOLD)
                    data.setValueTextColor(Color.WHITE)
                    pieChart.data = data

                    // undo all highlights
                    pieChart.highlightValues(null)

                    // loading chart
                    pieChart.invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}