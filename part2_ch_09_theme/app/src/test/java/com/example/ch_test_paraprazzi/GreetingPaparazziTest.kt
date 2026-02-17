package com.example.ch_test_paparazzi

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class GreetingPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(softButtons = false),
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun greetingCard_default() {
        paparazzi.snapshot {
            Greeting(
                name = "Android"
            )
        }
    }

//    @Test
//    fun greetingCard_longName() {
//        paparazzi.snapshot(name = "long_name") {
//            GreetingCard(name = "This is a very very long name")
//        }
//    }
}