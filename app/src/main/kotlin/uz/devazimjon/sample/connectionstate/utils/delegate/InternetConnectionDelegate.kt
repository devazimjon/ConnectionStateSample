package uz.devazimjon.sample.connectionstate.utils.delegate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import uz.devazimjon.sample.connectionstate.R

private const val LAYOUT_ID = Int.MAX_VALUE

object InternetConnectionDelegate {

    fun updateConnectivityState(activity: Activity, isConnected: Boolean) {
        val parent: ViewGroup =
            activity.window.decorView.findViewById(android.R.id.content) as ViewGroup

        if (isConnected) {
            val root = parent.findViewById<FrameLayout>(LAYOUT_ID)
            root?.let(parent::removeView)
        } else if (parent.findViewById<View>(LAYOUT_ID) == null) {
            val noConnectionView = getNoConnectionViewOrNull(activity)
            parent.addView(noConnectionView)
        }
    }

    @Suppress("DEPRECATION")
    private fun getNoConnectionViewOrNull(activity: Context): View {
        val frameLayout = FrameLayout(activity)
        frameLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.surfaceColor))
        frameLayout.setOnClickListener { /*Stub method*/ }

        val linearLayout = LinearLayout(activity)
        linearLayout.layoutParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER

        val image = ImageView(activity)
        image.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        image.setImageResource(R.drawable.ic_no_connection)

        val title = TextView(activity)
        title.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setText(R.string.no_connection)
        title.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.resources.getDimension(R.dimen.dp_14))

        val message = TextView(activity)
        message.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        (message.layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin = activity.resources.getDimensionPixelOffset(R.dimen.dp_14)
            marginStart = activity.resources.getDimensionPixelOffset(R.dimen.dp_22)
            marginEnd = activity.resources.getDimensionPixelOffset(R.dimen.dp_22)
        }
        message.gravity = Gravity.CENTER
        message.setText(R.string.settings_message)
        message.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
        message.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.resources.getDimension(R.dimen.dp_16))

        linearLayout.addView(image)
        linearLayout.addView(title)
        linearLayout.addView(message)

        val button = Button(activity)
        button.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        )
        (button.layoutParams as ViewGroup.MarginLayoutParams).apply {
            bottomMargin = activity.resources.getDimensionPixelOffset(R.dimen.dp_22)
            marginStart = activity.resources.getDimensionPixelOffset(R.dimen.dp_16)
            marginEnd = activity.resources.getDimensionPixelOffset(R.dimen.dp_16)
        }
        button.text = activity.getString(R.string.go_settings)
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.resources.getDimension(R.dimen.sp_14))
        button.setTextColor(ContextCompat.getColor(activity, R.color.white))

        button.setOnClickListener {
            val packageManager = it.context.packageManager
            intentList.firstNotNullOfOrNull { intent ->
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    .firstOrNull()
                    ?.toIntent()
            }
                ?.let(it.context::startActivity)
        }

        frameLayout.addView(linearLayout)
        frameLayout.addView(button)

        return frameLayout.apply { id = LAYOUT_ID }
    }

    private fun ResolveInfo.toIntent(): Intent {
        return Intent().apply {
            setClassName(
                activityInfo.applicationInfo.packageName,
                activityInfo.name
            )
        }
    }

    private val intentList
        get() = listOf(
            Intent(Settings.ACTION_DATA_USAGE_SETTINGS),
            Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
        )
}