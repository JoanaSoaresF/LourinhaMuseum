package com.example.lourinhamuseum.custom_view

import androidx.lifecycle.LiveData

interface PopupInfoController {
    /**
     * Defines what happen when the popup view is closed
     */
    fun onClosePopup()

    /**
     * Indicates if the popup should be visible or not
     */
    val isPopupVisible: LiveData<Boolean>


    /**
     * Defines the text in the popup
     */
    val popupText: String

    /**
     * Defines the title of the popup
     */
    val popupTitle: String
}