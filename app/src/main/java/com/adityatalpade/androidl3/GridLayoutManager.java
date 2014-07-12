package com.adityatalpade.androidl3;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class GridLayoutManager extends RecyclerView.LayoutManager {

    private Context mContext;

    GridLayoutManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        RenderState renderState = new RenderState();
        layoutDecorated(renderState.next(recycler), 10, 100, 10, 100);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * Helper class that keeps temporary state while {LayoutManager} is filling out the empty
     * space.
     */
    private static class RenderState {

        final static String TAG = "LinearLayoutManager#RenderState";

        final static int LAYOUT_START = -1;

        final static int LAYOUT_END = 1;

        final static int INVALID_LAYOUT = Integer.MIN_VALUE;

        final static int ITEM_DIRECTION_HEAD = -1;

        final static int ITEM_DIRECTION_TAIL = 1;

        final static int SCOLLING_OFFSET_NaN = Integer.MIN_VALUE;

        /**
         * Pixel offset where rendering should start
         */
        int mOffset;

        /**
         * Number of pixels that we should fill, in the layout direction.
         */
        int mAvailable;

        /**
         * Current position on the adapter to get the next item.
         */
        int mCurrentPosition;

        /**
         * Defines the direction in which the data adapter is traversed.
         * Should be {@link #ITEM_DIRECTION_HEAD} or {@link #ITEM_DIRECTION_TAIL}
         */
        int mItemDirection;

        /**
         * Defines the direction in which the layout is filled.
         * Should be {@link #LAYOUT_START} or {@link #LAYOUT_END}
         */
        int mLayoutDirection;

        /**
         * Used when RenderState is constructed in a scrolling state.
         * It should be set the amount of scrolling we can make without creating a new view.
         * Settings this is required for efficient view recycling.
         */
        int mScrollingOffset;

        /**
         * Used if you want to pre-layout items that are not yet visible.
         * The difference with {@link #mAvailable} is that, when recycling, distance rendered for
         * {@link #mExtra} is not considered to avoid recycling visible children.
         */
        int mExtra = 0;

        /**
         * When LLM needs to layout particular views, it sets this list in which case, RenderState
         * will only return views from this list and return null if it cannot find an item.
         */
        List<RecyclerView.ViewHolder> mScrapList = null;

        /**
         * @return true if there are more items in the data adapter
         */
        boolean hasMore(RecyclerView.State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        /**
         * Gets the view for the next element that we should render.
         * Also updates current item index to the next item, based on {@link #mItemDirection}
         *
         * @return The next element that we should render.
         */
        View next(RecyclerView.Recycler recycler) {
            if (mScrapList != null) {
                return nextFromLimitedList();
            }
            final View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mItemDirection;
            return view;
        }

        /**
         * Returns next item from limited list.
         * <p>
         * Upon finding a valid VH, sets current item position to VH.itemPosition + mItemDirection
         *
         * @return View if an item in the current position or direction exists if not null.
         */
        private View nextFromLimitedList() {
            int size = mScrapList.size();
            RecyclerView.ViewHolder closest = null;
            int closestDistance = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder viewHolder = mScrapList.get(i);
                final int distance = (viewHolder.getPosition() - mCurrentPosition) * mItemDirection;
                if (distance < 0) {
                    continue; // item is not in current direction
                }
                if (distance < closestDistance) {
                    closest = viewHolder;
                    closestDistance = distance;
                    if (distance == 0) {
                        break;
                    }
                }
            }
            if (1==1) {
                Log.d(TAG, "layout from scrap. found view:?" + (closest != null));
            }
            if (closest != null) {
                mCurrentPosition = closest.getPosition() + mItemDirection;
                return closest.itemView;
            }
            return null;
        }

        void log() {
            Log.d(TAG, "avail:" + mAvailable + ", ind:" + mCurrentPosition + ", dir:" +
                    mItemDirection + ", offset:" + mOffset + ", layoutDir:" + mLayoutDirection);
        }
    }

    OrientationHelper createVerticalOrientationHelper() {
        return new OrientationHelper() {
            @Override
            public int getEndAfterPadding() {
                return getHeight() - getPaddingBottom();
            }

            @Override
            public void offsetChildren(int amount) {
                offsetChildrenVertical(amount);
            }

            @Override
            public int getStartAfterPadding() {
                return getPaddingTop();
            }

            @Override
            public int getDecoratedMeasurement(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
            }

            @Override
            public int getDecoratedMeasurementInOther(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
            }

            @Override
            public int getDecoratedEnd(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedBottom(view) + params.bottomMargin;
            }

            @Override
            public int getDecoratedStart(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedTop(view) - params.topMargin;
            }

            @Override
            public int getTotalSpace() {
                return getHeight() - getPaddingTop() - getPaddingBottom();
            }
        };
    }

    OrientationHelper createHorizontalOrientationHelper() {
        return new OrientationHelper() {
            @Override
            public int getEndAfterPadding() {
                return getWidth() - getPaddingRight();
            }

            @Override
            public void offsetChildren(int amount) {
                offsetChildrenHorizontal(amount);
            }

            @Override
            public int getStartAfterPadding() {
                return getPaddingLeft();
            }

            @Override
            public int getDecoratedMeasurement(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
            }

            @Override
            public int getDecoratedMeasurementInOther(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
            }

            @Override
            public int getDecoratedEnd(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedRight(view) + params.rightMargin;
            }

            @Override
            public int getDecoratedStart(View view) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        view.getLayoutParams();
                return getDecoratedLeft(view) - params.leftMargin;
            }

            @Override
            public int getTotalSpace() {
                return getWidth() - getPaddingLeft() - getPaddingRight();
            }
        };
    }


    /**
     * Helper interface to offload orientation based decisions
     */
    static interface OrientationHelper {

        /**
         * @param view The view element to check
         * @return The first pixel of the element
         * @see #getDecoratedEnd(android.view.View)
         */
        int getDecoratedStart(View view);

        /**
         * @param view The view element to check
         * @return The last pixel of the element
         * @see #getDecoratedStart(android.view.View)
         */
        int getDecoratedEnd(View view);

        /**
         * @param view The view element to check
         * @return Total space occupied by this view
         */
        int getDecoratedMeasurement(View view);

        /**
         * @param view The view element to check
         * @return Total space occupied by this view in the perpendicular orientation to current one
         */
        int getDecoratedMeasurementInOther(View view);

        /**
         * @return The very first pixel we can draw.
         */
        int getStartAfterPadding();

        /**
         * @return The last pixel we can draw
         */
        int getEndAfterPadding();

        /**
         * Offsets all children's positions by the given amount
         *
         * @param amount Value to add to each child's layout parameters
         */
        void offsetChildren(int amount);

        /**
         * Returns the total space to layout.
         *
         * @return Total space to layout children
         */
        int getTotalSpace();
    }

    static class SavedState implements Parcelable {

        int mOrientation;

        int mAnchorPosition;

        int mAnchorOffset;

        boolean mReverseLayout;

        boolean mStackFromEnd;

        boolean mAnchorLayoutFromEnd;


        public SavedState() {

        }

        SavedState(Parcel in) {
            mOrientation = in.readInt();
            mAnchorPosition = in.readInt();
            mAnchorOffset = in.readInt();
            mReverseLayout = in.readInt() == 1;
            mStackFromEnd = in.readInt() == 1;
            mAnchorLayoutFromEnd = in.readInt() == 1;
        }

        public SavedState(SavedState other) {
            mOrientation = other.mOrientation;
            mAnchorPosition = other.mAnchorPosition;
            mAnchorOffset = other.mAnchorOffset;
            mReverseLayout = other.mReverseLayout;
            mStackFromEnd = other.mStackFromEnd;
            mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mOrientation);
            dest.writeInt(mAnchorPosition);
            dest.writeInt(mAnchorOffset);
            dest.writeInt(mReverseLayout ? 1 : 0);
            dest.writeInt(mStackFromEnd ? 1 : 0);
            dest.writeInt(mAnchorLayoutFromEnd ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}