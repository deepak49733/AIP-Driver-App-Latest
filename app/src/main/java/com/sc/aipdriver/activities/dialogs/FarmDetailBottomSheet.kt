package com.sc.aipdriver.activities.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.models.FarmInfo

class FarmDetailBottomSheet(
    private val farmInfo: FarmInfo?,
    private val listener: FarmDetailListener
) : BottomSheetDialogFragment() {
    lateinit var ivLargeView: ImageView
    lateinit var ivClose: ImageView
    lateinit var clLargeView: ConstraintLayout


    interface FarmDetailListener {
        fun onStartRide()
        fun onPauseRide()
        fun onResumeRide()
        fun onEndRide()
        fun onFinishRide()
        fun onBack()
        fun showImageLarge(url: String?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_farm_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val farmName = view.findViewById<TextView>(R.id.farmName)
        val farmMobile = view.findViewById<TextView>(R.id.farmMobile)
        val emergencyMobile = view.findViewById<TextView>(R.id.emergencyMobile)
        val emergencyAlternateMobile = view.findViewById<TextView>(R.id.emergencyAlternateMobile)
        val farmAddress = view.findViewById<TextView>(R.id.farmAddress)
        val instruction = view.findViewById<TextView>(R.id.instruction)
        val ivFarm = view.findViewById<ImageView>(R.id.iv_farm)
         ivLargeView = view.findViewById<ImageView>(R.id.iv_largeview)
         ivClose = view.findViewById<ImageView>(R.id.ivClose)
         clLargeView = view.findViewById<ConstraintLayout>(R.id.cl_largeImage)
        val ivFarmDrop = view.findViewById<ImageView>(R.id.iv_farmdrop)
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        val btnStart = view.findViewById<Button>(R.id.btnStart)
        val pauseBtn = view.findViewById<Button>(R.id.pause)
        val resumeBtn = view.findViewById<Button>(R.id.resume)
        val endBtn = view.findViewById<Button>(R.id.end)
        val finishBtn = view.findViewById<Button>(R.id.finish)
        val llButtons = view.findViewById<View>(R.id.ll_buttons)

        farmInfo?.let {
            farmName.text = it.firmName
            farmMobile.text = it.officePhone
            emergencyMobile.text = it.farmEmergencyNo
            emergencyAlternateMobile.text = it.farmEmergencyNo2nd
            farmAddress.text = "${it.address}, ${it.city}, ${it.state} ${it.zipCode}"
            instruction.text = it.callAheadInstructions

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launcher_)

            Glide.with(requireContext())
                .load(it.userImage1)
                .apply(requestOptions)
                .into(ivFarm)

            Glide.with(requireContext())
                .load(it.semenDropLocationImg1)
                .apply(requestOptions)
                .into(ivFarmDrop)

            ivFarm.setOnClickListener { _ ->
                showImageLarge(it.userImage1) }
            ivFarmDrop.setOnClickListener { _ ->
                showImageLarge(it.semenDropLocationImg1) }
            ivClose.setOnClickListener { clLargeView.visibility=GONE }
        }

        arguments?.let {
            val actionn = it.getInt("actionn", 0)
            if (actionn == 1) {
                llButtons.isVisible = true
                btnStart.isVisible = false
            } else {
                llButtons.isVisible = false
                btnStart.isVisible = false // Matching Activity logic line 97
            }
        }


        ivBack.setOnClickListener {
            dismiss()
            listener.onBack()
        }

        // Buttons actions can be extended or tied to Activity directly via callback
        btnStart.setOnClickListener { listener.onStartRide(); dismiss() }
        pauseBtn.setOnClickListener { listener.onPauseRide(); dismiss() }
        resumeBtn.setOnClickListener { listener.onResumeRide(); dismiss() }
        endBtn.setOnClickListener { listener.onEndRide(); dismiss() }
        finishBtn.setOnClickListener { listener.onFinishRide(); dismiss() }

    }

    private fun showImageLarge(userImage1: String) {
        clLargeView.visibility=VISIBLE
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_launcher_)
        Glide.with(requireContext())
            .load(userImage1)
            .apply(requestOptions)
            .into(ivLargeView)
    }
}