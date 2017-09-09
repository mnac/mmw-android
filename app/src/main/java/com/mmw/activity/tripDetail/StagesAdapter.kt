package com.mmw.activity.tripDetail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmw.R
import com.mmw.activity.pictureSwipe.PictureSwipeActivity
import com.mmw.databinding.ItemStageBinding
import com.mmw.model.Stage

/**
 * Created by Mathias on 27/08/2017.
 *
 */
class StagesAdapter(val context: Context, private var stages: List<Stage>)
    : RecyclerView.Adapter<StageItemViewHolder>(), StageActionListener {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StageItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val binding = DataBindingUtil.inflate<ItemStageBinding>(layoutInflater, R.layout.item_stage, parent, false)
        return StageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StageItemViewHolder?, position: Int) {
        val stage = stages[position]
        stage.setCurrentDisplayDatetime()
        stage.setCurrentPicturePath()
        holder?.bind(stage, this)
    }

    override fun getItemCount() = stages.size

    fun setList(stages: List<Stage>) {
        this.stages = stages
        notifyDataSetChanged()
    }

    override fun onClickStage(position: Int) {
        val picturesPaths: ArrayList<String> = ArrayList()
        for (stage in stages) {
            picturesPaths.add(stage.picturePath)
        }

        val intent = Intent(context, PictureSwipeActivity::class.java)
        intent.putStringArrayListExtra(PictureSwipeActivity.PICTURE_PATHS_KEY, picturesPaths)
        intent.putExtra(PictureSwipeActivity.PICTURE_POSITION_KEY, position)
        context.startActivity(intent)
    }
}