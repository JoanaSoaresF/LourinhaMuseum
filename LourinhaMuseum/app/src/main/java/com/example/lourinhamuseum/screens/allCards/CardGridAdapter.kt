package com.example.lourinhamuseum.screens.allCards


import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchfromserver.domain.MuseumRoom
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.data.domain.Point
import com.example.lourinhamuseum.databinding.GridCardItemBinding
import com.example.lourinhamuseum.databinding.HeaderBinding
import com.example.lourinhamuseum.screens.allCards.AllCardsFragment.Companion.SPAN_WIDTH_SIZE
import com.example.lourinhamuseum.screens.allCards.DataItem.Header
import com.example.lourinhamuseum.screens.allCards.DataItem.PointItem


private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to
 * present [List] data, including computing diffs between lists
 * @param onClickListener
 */
class CardGridAdapter(val onClickListener: OnCardClicked) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(DiffCallback()) {

    /**
     * Takes the binding variable from the associated GridViewItem, which gives access
     * to the full [Point] information
     */
    class PointViewHolder private constructor(private var binding: GridCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(point: Point, onClickListener: OnCardClicked) {
            binding.point = point
            binding.clickListener = onClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): PointViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridCardItemBinding.inflate(layoutInflater, parent, false)
                return PointViewHolder(binding)
            }
        }
    }

    /**
     * Holder for the header data type. Receives the binding to the [HeaderBinding] and
     * allows the binding of the [MuseumRoom] yo the header
     */
    class HeaderViewHolder(private var binding: HeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: MuseumRoom) {
            binding.room = room
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PointViewHolder -> { // Data Item representing a point
                val point = getItem(position) as PointItem
                holder.bind(point.point, onClickListener)
            }
            is HeaderViewHolder -> { //Data Item representing a Header
                val room = getItem(position) as Header
                holder.itemView
                holder.bind(room.room)
            }
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> PointViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Header -> ITEM_VIEW_TYPE_HEADER
            is PointItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items. Passes the [Point]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Point]
     */
    class OnCardClicked(val clickListener: (point: Point) -> Unit) {
        fun onClick(point: Point) = clickListener(point)
    }
}


/**
 * Allows the [RecyclerView] to determinate which items have changed when the [List]
 * of [Point] has been updated
 */
class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

/**
 * Data item to hold the data to display in the [RecyclerView]. The data can be a
 * [Point] store in the [PointItem] or a [MuseumRoom] stored in the [Header].
 * This allows the division of points by room, displaying a room
 */
sealed class DataItem {
    /**
     * Holds a [Point], to display the card image
     */
    data class PointItem(val point: Point) : DataItem() {
        override val id = point.id
    }

    /**
     * Holds a [MuseumRoom] to display a header
     */
    data class Header(val room: MuseumRoom) : DataItem() {
        override val id = Int.MIN_VALUE
    }

    abstract val id: Int
}


class DividerItemDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable = AppCompatResources.getDrawable(
        context, R.drawable.cards_background
    )!!
    private var mDividerMirror: Drawable = AppCompatResources.getDrawable(
        context, R.drawable.cards_background_mirror
    )!!
    private var separator: Drawable = AppCompatResources.getDrawable(
        context, R.drawable.separator
    )!!

    companion object {
        const val FIRST_IN_ROW_EVEN = 0
        const val FIRST_IN_ROW_ODD = SPAN_WIDTH_SIZE
        const val ROW_IDENTIFIER = SPAN_WIDTH_SIZE * 2
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = 90
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        val dividerLeft = 0
        val dividerRight = parent.width
        val childCount = parent.childCount


        for (i in 0 until childCount) {

            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.top - 40/*- params.topMargin*/
            val dividerBottom = child.bottom + 5/* + child.paddingBottom*/

            if (i % ROW_IDENTIFIER == FIRST_IN_ROW_EVEN) {
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider.draw(canvas)
                drawSeparator(dividerBottom, dividerLeft, dividerRight, canvas)
            } else if (i % ROW_IDENTIFIER == FIRST_IN_ROW_ODD) {
                mDividerMirror.setBounds(
                    dividerLeft,
                    dividerTop,
                    dividerRight,
                    dividerBottom
                )
                mDividerMirror.draw(canvas)
                drawSeparator(dividerBottom, dividerLeft, dividerRight, canvas)
            }
        }
    }

    private fun drawSeparator(
        dividerBottom: Int,
        dividerLeft: Int,
        dividerRight: Int,
        canvas: Canvas
    ) {
        val separatorTop = dividerBottom + 16
        val separatorBottom = separatorTop + 20
        separator.setBounds(
            dividerLeft + 60, separatorTop, dividerRight - 60,
            separatorBottom
        )
        separator.draw(canvas)
    }
}