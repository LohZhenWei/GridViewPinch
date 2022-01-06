package com.loh.gridviewpinch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loh.gridviewpinch.databinding.ActivityMainBinding
import android.view.ScaleGestureDetector
import com.loh.gridviewpinch.databinding.ItemGridBinding
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val gridAdapter = GridAdapter()

    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private val gridLayoutManager by lazy { GridLayoutManager(this, 3) }
    private var spanCount = 3
    private val maxCount = 7
    private val minCount = 3

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        scaleGestureDetector =
            ScaleGestureDetector(this,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector?): Boolean {
                        detector?.let {
                            if (it.timeDelta < 100) return false
                            if (it.currentSpan > 400) {
                                if (it.currentSpan - it.previousSpan < -1) {
                                    return if (spanCount < maxCount) {
                                        spanCount++
                                        gridLayoutManager.spanCount = spanCount
                                        true
                                    } else false
                                } else if (it.currentSpan - it.previousSpan > 1) {
                                    return if (spanCount > minCount) {
                                        spanCount--
                                        gridLayoutManager.spanCount = spanCount
                                        true
                                    } else false
                                }
                            }
                        }
                        return false
                    }
                })
        initRecycleView()
        binding.rvList.setOnTouchListener { p0, p1 ->
            scaleGestureDetector.onTouchEvent(p1)
            false
        }
    }

    private fun initRecycleView() {
        binding.rvList.apply {
            layoutManager = gridLayoutManager
            adapter = gridAdapter
        }
        gridAdapter.setData(getData())

    }

    private fun getData(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0..1000) {
            list.add(i.toString())
        }
        return list
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}

class GridAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GridViewHolder(ItemGridBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GridViewHolder) {
            holder.bind(data[position])
        }
    }

    fun setData(newData: List<String>) {
        val diffCallBack = GridDiff(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = data.size

    inner class GridViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String) {
            binding.root.context?.let {
                val imageBuilder = Glide.with(it)
                    .load(it.resources.getIdentifier("img", "drawable", it.packageName))
                    .fitCenter()
                    .dontAnimate()
                    .centerCrop()

                imageBuilder.into(binding.ivImage)
            }
        }
    }

    class GridDiff(private val oldList: List<String>, private val newList: List<String>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}