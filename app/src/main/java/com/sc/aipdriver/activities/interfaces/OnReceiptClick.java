package com.sc.aipdriver.activities.interfaces;

import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.models.ReceiptDetails;
import com.sc.aipdriver.activities.models.ReceiptResponse;

public interface OnReceiptClick {

    void onReceiptClick(int position, ReceiptResponse receiptDetails, boolean isImage);


}
