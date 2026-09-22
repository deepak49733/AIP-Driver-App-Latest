package com.sc.aipdriver.activities.interfaces

import com.sc.aipdriver.activities.models.LiRoutePlannerDetail

interface OnClickSubmit {
    fun onClickSubmit(clicked: Boolean, liRoutePlannerDetails: List<LiRoutePlannerDetail?>?, comments: String,sendMail: Boolean)
    fun onPhotoUpload(boolean: Boolean, sendmail: Boolean)
}