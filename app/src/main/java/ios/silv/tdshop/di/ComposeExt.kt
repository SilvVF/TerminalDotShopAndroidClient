package ios.silv.tdshop.di

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import ios.silv.tdshop.App

@Composable
fun requireAppComponent() = (requireNotNull(LocalActivity.current) {
    "can create presenter outside of an activity"
}.application as App).appComponent