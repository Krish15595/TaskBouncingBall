package com.example.task

import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.chart
import kotlinx.android.synthetic.main.activity_main2.seekBar1
import kotlinx.android.synthetic.main.activity_main2.tvXMax


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {



    var H:ArrayList<Double>?=null
    var T:ArrayList<Double>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        Log.i("time", T!![T!!.size-1].toString())


        /*getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );*/
        setContentView(R.layout.activity_main);

        setTitle("Bouncing Ball");

        chart.setViewPortOffsets(10F, 10F, 10F, 10F);
        chart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300F);

        val x: XAxis = chart.getXAxis();
        x.setTextColor(Color.BLACK)
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setDrawGridLines(false);
        x.setAxisLineColor(Color.WHITE);

        val y: YAxis = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.BLUE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);

        // add data
//        seekBar2.setOnSeekBarChangeListener(this);
        seekBar1.setOnSeekBarChangeListener(this);

        // lower max, as cubic runs significantly slower than linear
        seekBar1.setMax(50);

        seekBar1.setProgress(20);
//        seekBar2.setProgress(100);

        chart.getLegend().setEnabled(false);


        chart.animateXY(2000, 0);
        // don't forget to refresh the drawing
    }

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        tvXMax.setText(seekBar1.getProgress().toString())
        //tvYMax.setText(seekBar2.getProgress().toString())
        setData(seekBar1.getProgress())
        // redraw


        chart.invalidate()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    private fun setData(height: Int) {
        var h0 = height.toDouble()         /*# m/s*/
        var v = 0.0          /*# m/s, current velocity*/
        var g = 10         /*# m/s/s*/
        var t = 0.0          /*# starting time*/
        var dt = 0.001     /*# time step*/
        var rho = 0.75     /*# coefficient of restitution*/
        var tau = 0.10     /*# contact time for bounce*/
        var hmax = h0      /*# keep track of the maximum height*/
        var h = h0
        var hstop = 0.01
        var freefall:Boolean = true
        val values:ArrayList<Entry> = arrayListOf()
        T= arrayListOf()
        var t_last = -Math.sqrt(2*h0/g)
        var vmax=Math.sqrt((2* hmax * g))
        var bounce:Int=0
        while(hmax > hstop) {
            if (freefall) {
                val hnew: Double = h + v * dt - 0.5 * g * dt * dt
                if (hnew < 0) {
                    bounce++
                    Log.i("Bounce", bounce.toString())
                    t = t_last + 2 * Math.sqrt((2 * hmax / g))
                    freefall = false
                    t_last = t + tau
                    h = 0.0
                }
                else {
                    t = (t + dt)
                    v = (v - g * dt)
                    h = hnew
                }
            } else {
                t = (t + tau)
                vmax = vmax * rho
                v = vmax
                freefall = true
                h = 0.0
                hmax = 0.5 * vmax * vmax / g
            }
            values.add(Entry(t.toFloat(), h.toFloat()))
            T!!.add(t)
        }
        val set1: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.setValues(values)
            tv_bounce.text=bounce.toString()
            tv_time.text= T!![T!!.size-1].toString()
            chart.data.notifyDataChanged()
            chart.animateXY(2000, 0);
            chart.notifyDataSetChanged()
        } else { // create a dataset and give it a type
            set1 = LineDataSet(values, "Bouncing Ball")
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.2f
            set1.setDrawFilled(true)
            set1.setDrawCircles(false)
            set1.lineWidth = 1.8f
            set1.circleRadius = 4f
            set1.setCircleColor(Color.WHITE)
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.color = Color.BLACK
            set1.fillColor = Color.WHITE
            set1.fillAlpha = 100
            set1.setDrawHorizontalHighlightIndicator(false)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTextSize(9f)
            data.setDrawValues(false)
            // set data
            tv_bounce.text=bounce.toString()
            tv_time.text= T!![T!!.size-1].toString()
            chart.data = data
            chart.invalidate()
        }
    }
}
