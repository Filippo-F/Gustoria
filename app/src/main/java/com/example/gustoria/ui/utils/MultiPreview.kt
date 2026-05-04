package com.example.gustoria.ui.utils

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Small Phone", showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Standard Phone", showSystemUi = true, device = Devices.PHONE)
@Preview(name = "Big Tall Phone", showSystemUi = true, device = "spec:width=412dp,height=915dp,dpi=420")
@Preview(name = "Long Scroll View", showBackground = true, heightDp = 1500)
@Preview(name = "Tablet 4:3", showSystemUi = true, device = Devices.TABLET)
@Preview(name = "Foldable Inner", showSystemUi = true, device = Devices.FOLDABLE)
@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
annotation class MultiPreview
