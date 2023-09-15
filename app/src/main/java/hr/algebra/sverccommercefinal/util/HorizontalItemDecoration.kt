package hr.algebra.sverccommercefinal.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView item decoration for adding horizontal spacing (margin) between items.
 *
 * @property amount: The amount of horizontal spacing (in pixels) to add between items. Default is 15 pixels.
 */
class HorizontalItemDecoration(private val amount: Int = 15) : RecyclerView.ItemDecoration() {

    /**
     * Adds horizontal spacing (margin) to the right of each item in the RecyclerView.
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
        outRect.right = amount
    }
}
