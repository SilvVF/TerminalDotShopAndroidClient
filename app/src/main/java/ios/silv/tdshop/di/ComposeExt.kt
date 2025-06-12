package ios.silv.tdshop.di

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import ios.silv.tdshop.MainActivity

@Composable
fun requireActivityComponent(
    activity: Activity = requireNotNull(LocalActivity.current) { "can create presenter outside of an activity" }
): MainActivityComponent = (activity as MainActivity).mainComponent