package tech.kood.parallaxrecyclerview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import tech.kood.parallaxrecyclerview.view.ParallaxImageView;
import tech.kood.parallaxrecyclerview.view.ParallaxRecyclerView;

// class java version is used here to avoid a leak in kotlin version
public class ParallaxViewHolder extends RecyclerView.ViewHolder implements ParallaxImageListener {

    public ParallaxImageView backgroundImage;
    private int orientation;

    public ParallaxViewHolder(View itemView, int orientation) {
        super(itemView);

        this.backgroundImage = itemView.findViewById(getParallaxImageId());
        this.orientation = orientation;
        this.backgroundImage.setListener(this);
    }

    @Override
    public int[] requireValuesForTranslate() {
        if (itemView.getParent() == null) {
            // Not added to parent yet!
            return null;
        } else {
            int[] itemAxisPosition = new int[2];
            itemView.getLocationOnScreen(itemAxisPosition);

            int[] recyclerAxisPosition = new int[2];
            ((RecyclerView) itemView.getParent()).getLocationOnScreen(recyclerAxisPosition);

            if (orientation == ParallaxRecyclerView.VERTICAL)
                return new int[]{itemAxisPosition[1], ((RecyclerView) itemView.getParent()).getMeasuredHeight(), recyclerAxisPosition[1], itemView.getMeasuredHeight(), orientation};
            else
                return new int[]{itemAxisPosition[0], ((RecyclerView) itemView.getParent()).getMeasuredWidth(), recyclerAxisPosition[0], itemView.getMeasuredWidth(), orientation};
        }
    }

    public void animateImage() {
        getBackgroundImage().doTranslate();
    }

    private ParallaxImageView getBackgroundImage() {
        return backgroundImage;
    }

    public int getParallaxImageId(){
        return 0;
    }
}