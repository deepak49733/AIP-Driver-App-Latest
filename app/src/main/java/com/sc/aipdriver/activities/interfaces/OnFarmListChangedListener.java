package com.sc.aipdriver.activities.interfaces;


import com.sc.aipdriver.activities.models.PriorityFarmData;

import java.util.List;

public interface OnFarmListChangedListener {

    void onNoteListChanged(List<PriorityFarmData> farmData);

}
