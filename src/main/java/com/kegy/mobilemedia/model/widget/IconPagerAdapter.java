package com.kegy.mobilemedia.model.widget;


public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
//    int getCount();
    
    boolean useStroke(int index);
    
//    Boolean getIconView(int index);
}
