package com.mmw.activity.tripDetail

import android.support.v7.widget.RecyclerView
import android.view.View
import com.mmw.databinding.ItemStageBinding
import com.mmw.model.Stage

/**
 * Created by Mathias on 27/08/2017.
 *
 */
class StageItemViewHolder(var binding: ItemStageBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var stageActionListener: StageActionListener

    override fun onClick(view: View?) {
        stageActionListener.onClickStage(adapterPosition)
    }

    fun bind(stage: Stage, stageActionListener: StageActionListener) {
        this.stageActionListener = stageActionListener
        binding.stage = stage
        binding.handler = this
        binding.executePendingBindings()
    }
}