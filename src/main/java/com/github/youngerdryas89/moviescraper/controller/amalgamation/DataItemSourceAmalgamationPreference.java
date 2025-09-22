package com.github.youngerdryas89.moviescraper.controller.amalgamation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.youngerdryas89.moviescraper.model.dataitem.DataItemSource;
import com.github.youngerdryas89.moviescraper.model.dataitem.DefaultDataItemSource;

/**
 * Ranked list of what DataItemSources I would prefer to pick from when doing an amalgamation
 */
public class DataItemSourceAmalgamationPreference {


    /**
     * @param dataItemSources - the list of preferred items to use to amalgamate. the first parameter passed
     * in is the most preferred item, the second the second most preferred, and so on
     */
    public static List<DataItemSource> createPreferenceOrdering(DataItemSource... dataItemSources){

        var amalgamationPreferenceOrder = new ArrayList<DataItemSource>();
        amalgamationPreferenceOrder.addAll(Arrays.asList(dataItemSources));

        //Always put this at the end as a fall back for items which didn't get their data item source set another way
        //to allow us to still pick them in case no other item from a more preferred source was found first when amalgamating
        if (!amalgamationPreferenceOrder.contains(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE))
            amalgamationPreferenceOrder.add(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE);

        return amalgamationPreferenceOrder;
    }
}
