package hr.algebra.sverccommercefinal.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView item decoration for adding vertical spacing (margin) between items.
 *
 * @property amount: The amount of vertical spacing (in pixels) to add between items. Default is 30 pixels.
 */
class VerticalItemDecoration(private val amount: Int = 30) : RecyclerView.ItemDecoration() {

    /**
     * Adds vertical spacing (margin) to the bottom of each item in the RecyclerView.
     *
     * @param outRect: The Rect object representing the margins of the current item.
     * @param view: The View of the current item in the RecyclerView.
     * @param parent: The parent RecyclerView.
     * @param state: The current RecyclerView.State.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Set the bottom margin of the current item to the specified amount.
        outRect.bottom = amount
    }
}
