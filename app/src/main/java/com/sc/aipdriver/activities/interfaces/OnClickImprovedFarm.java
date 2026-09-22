package com.sc.aipdriver.activities.interfaces;

import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.PriorityFarmData;

public interface OnClickImprovedFarm {

    void onFarmClick(ImprovedPriorityFarmData model, String name);
    void onFarmNameClick(ImprovedPriorityFarmData model, String name);
    void onStartRide(ImprovedPriorityFarmData model, String name);
    void onCompleteRide(ImprovedPriorityFarmData model, String name);
    void onPauseRide(ImprovedPriorityFarmData model, String name);
    void onResumeRide(ImprovedPriorityFarmData model, String name);
    void onParentClick(ImprovedPriorityFarmData model, String name);
    void onMapClick(ImprovedPriorityFarmData model, String name);

}
