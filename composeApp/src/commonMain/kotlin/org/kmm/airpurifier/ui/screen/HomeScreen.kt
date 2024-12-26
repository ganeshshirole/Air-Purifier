package org.kmm.airpurifier.ui.screen

import airpurifier.composeapp.generated.resources.Res
import airpurifier.composeapp.generated.resources.bluetooth_b_brands_solid
import airpurifier.composeapp.generated.resources.fan_icon
import airpurifier.composeapp.generated.resources.heart_solid
import airpurifier.composeapp.generated.resources.leaf_solid
import airpurifier.composeapp.generated.resources.lightbulb_regular
import airpurifier.composeapp.generated.resources.lightbulb_solid
import airpurifier.composeapp.generated.resources.moon_solid
import airpurifier.composeapp.generated.resources.power_off_solid
import airpurifier.composeapp.generated.resources.right_arrow
import airpurifier.composeapp.generated.resources.shop_icon
import airpurifier.composeapp.generated.resources.starburst_icon
import airpurifier.composeapp.generated.resources.windspeed
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.kmm.airpurifier.dependencies.HomeViewModel
import org.kmm.airpurifier.util.OstrichSansFontFamily
import org.kmm.airpurifier.util.PrimaryColor
import org.kmm.airpurifier.util.RobotoFontFamily
import org.kmm.airpurifier.util.SecondaryColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(navController: NavController) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val viewModel = koinViewModel<HomeViewModel>()
//    val scannerViewModel = koinViewModel<ScannerViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.connectToDevice()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .border(
                        1.dp,
                        Color.Black,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .height(50.dp),
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                title = {
                    // Centered Title
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (state.value.isConnected) "Connected" else "Disconnected",
                            fontWeight = FontWeight.Light,
                            fontSize = 18.sp,
                            fontFamily = RobotoFontFamily(),
                            color = Color.DarkGray,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(end = 16.dp)
                        )
                    }
                },
                navigationIcon = {
                    // Left Burger Icon
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Menu",
                            modifier = Modifier.size(28.dp),
                            tint = PrimaryColor
                        )
                    }
                },
                actions = {
                    // Right Bluetooth Icon
                    IconButton(onClick = { /* Handle Bluetooth action */ }) {
                        BlinkingIcon(isConnected = state.value.isConnected)
                    }
                }
            )
        },
        content = { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize().padding(paddingValues),
            ) {
                val smallScreen = if (maxHeight.value < 600) true else false
                Column(
                    modifier = Modifier.then(
                        if (smallScreen) {
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                                .fillMaxSize()
                        }
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AQICircularButton(
                        smallScreen = smallScreen,
                        modifier = Modifier.then(
                            if (smallScreen) {
                                Modifier
                            } else {
                                Modifier.weight(1f)
                            }
                        ))
                    Spacer(modifier = Modifier.size(8.dp))
                    FanSpeedBox()
                    Spacer(modifier = Modifier.size(8.dp))
                    AmbientLightBox()
                    Spacer(modifier = Modifier.size(8.dp))
                    UVBox()
                    Spacer(modifier = Modifier.size(8.dp))
                    CartridgeLifeBox()
                    Spacer(modifier = Modifier.size(8.dp))
                    BottomButtonBox()
                    Spacer(modifier = Modifier.size(8.dp))
//                    Button(onClick = { navController.navigate("second") }) {
//                        Text("Go to Second Screen")
//                    }
                }
                ScanBleDeviceDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    maxHeight
                ) {
                    showDialog = false
                    viewModel.connectToDevice(it.name, it.address)
                }
            }
        }
    )
}

@Composable
fun BlinkingIcon(isConnected: Boolean) {
    // State to control blinking when connected
    var isBlinking by rememberSaveable { mutableStateOf(false) }

    // Start blinking if connected, stop if disconnected
    LaunchedEffect(isConnected) {
        isBlinking = isConnected
    }

    // Animate the color change between two colors (e.g., Red and LightGray)
    val animatedColor by animateColorAsState(
        targetValue = if (isBlinking) PrimaryColor else Color.LightGray,
        animationSpec = infiniteRepeatable(
            animation = tween(500), // 500ms blink duration
            repeatMode = RepeatMode.Reverse
        ),
        finishedListener = { /* optional: handle when animation ends */ }
    )

    // The icon that will blink when connected
    Icon(
        painter = painterResource(Res.drawable.bluetooth_b_brands_solid),
        contentDescription = "Bluetooth",
        modifier = Modifier.size(28.dp),
        tint = if (isConnected) animatedColor else Color.Gray // Gray when disconnected
    )
}

@Composable
fun AQICircularButton(
    smallScreen: Boolean,
    modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.padding(10.dp),
        contentAlignment = Alignment.Center // Center the inner circle
    ) {
        Box(
            modifier = Modifier
                .size(if (smallScreen) 180.dp else 220.dp)
                .aspectRatio(1f)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                )
                .border(1.dp, SecondaryColor, CircleShape)
                .padding(if (smallScreen) 18.dp else 22.dp),
            contentAlignment = Alignment.Center // Align content to the center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()  // Size of the circle
                    .background(
                        color = PrimaryColor,
                        shape = CircleShape
                    )  // Circle shape with red background
                    .border(6.dp, SecondaryColor, CircleShape),
                contentAlignment = Alignment.Center // Align content to the center
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "AQI",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = if (smallScreen) 18.sp else 22.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = RobotoFontFamily()
                        ),
                    )
                    Text(
                        text = state.value.aiq.toString(),
                        color = Color.White,
                        fontSize = if (smallScreen) 76.sp else 90.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = OstrichSansFontFamily(),
                    )
                }
            }
        }
    }
}

@Composable
fun FanSpeedBox() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(Res.drawable.windspeed), // Change to your image resource
            contentDescription = "fan back design", // Description for accessibility
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentScale = ContentScale.FillWidth
        )
        // Column inside the box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    1.dp,
                    PrimaryColor,
                    shape = RoundedCornerShape(16.dp)
                ) // Rounded border
                .background(Color.Transparent)
                .padding(vertical = 10.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Fan Speed",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                FANButton(modifier = Modifier.size(40.dp),
                    enabled = !state.value.power && state.value.isConnected,
                    isSelected = state.value.motorSpeed == 1,
                    onClick = {
                        viewModel.motorSpeed(1)
                    })
                Spacer(modifier = Modifier.size(38.dp))
                FANButton(
                    modifier = Modifier.size(50.dp),
                    isSelected = state.value.motorSpeed == 2,
                    onClick = {
                        viewModel.motorSpeed(2)
                    },
                    enabled = !state.value.power && state.value.isConnected
                )
                Spacer(modifier = Modifier.size(38.dp))
                FANButton(
                    modifier = Modifier.size(65.dp),
                    isSelected = state.value.motorSpeed == 3,
                    onClick = {
                        viewModel.motorSpeed(3)
                    },
                    enabled = !state.value.power && state.value.isConnected
                )
            }
        }
    }
}

@Composable
fun AmbientLightBox() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        // Column inside the box
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .border(
                    1.dp,
                    PrimaryColor,
                    shape = RoundedCornerShape(16.dp)
                ) // Rounded border
                .background(Color.Transparent)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ambient Light", color = Color.Black)
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, PrimaryColor),
                            RoundedCornerShape(16.dp)
                        ) // Rounded border
                        .background(Color(0x40CCCCCC), shape = CircleShape) // Background color
                        .padding(
                            horizontal = 31.dp,
                            vertical = 4.dp
                        ) // Padding inside the box
                ) {
                    Text(
                        text = "${state.value.ambientLight}%",
                        color = PrimaryColor, // Gray text color
                        fontSize = 14.sp,
                        style = TextStyle(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Spacer for some space between texts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.lightbulb_solid),
                    contentDescription = "Bulb",
                    modifier = Modifier.size(30.dp),
                    tint = PrimaryColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Slider for selecting values from 0 to 100
                Slider(
                    enabled = !state.value.power && state.value.isConnected,
                    value = state.value.ambientLight.toFloat(),
                    onValueChange = { newValue ->
                        viewModel.ambientLightValue(newValue.toInt())
                    },
                    onValueChangeFinished = {
                        viewModel.ambientLight()
                    },
                    valueRange = 0f..100f, // Range from 0 to 100
                    steps = 100, // Number of discrete steps (99 steps to get 0-100 range)
                    modifier = Modifier.weight(1f), // Fill the width of the parent
                    // Custom color combination for the slider
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryColor, // Thumb (handle) color
                        activeTrackColor = Color.Red, // Active track (progressed part) color
                        inactiveTrackColor = Color.Gray, // Inactive track color
                        activeTickColor = Color.Red, // Active tick color
                        inactiveTickColor = Color.LightGray // Inactive tick color
                    )

                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(Res.drawable.lightbulb_regular),
                    contentDescription = "Bulb",
                    modifier = Modifier.size(30.dp),
                    tint = PrimaryColor
                )
            }
        }
    }
}

@Composable
fun UVBox() {

    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        // Column inside the box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    1.dp,
                    PrimaryColor,
                    shape = RoundedCornerShape(16.dp)
                ) // Rounded border
                .background(Color.Transparent)
                .padding(vertical = 4.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.starburst_icon),
                contentDescription = "Bulb",
                modifier = Modifier.size(30.dp),
                tint = PrimaryColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Ultra Violet (UV)", color = Color.Black, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Text label next to the switch
                Text(
                    text = if (state.value.uv) "ON" else "OFF",
                    style = MaterialTheme.typography.body1
                )
                // Switch component
                Switch(
                    enabled = !state.value.power && state.value.isConnected,
                    checked = state.value.uv,
                    onCheckedChange = {
                        viewModel.uvLight(it)
                    } // Update state when toggled
                )
            }
        }
    }
}

@Composable
fun CartridgeLifeBox() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        // Column inside the box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    1.dp,
                    PrimaryColor,
                    shape = RoundedCornerShape(16.dp)
                ) // Rounded border
                .background(Color.Transparent)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.heart_solid),
                contentDescription = "Heart",
                modifier = Modifier.size(30.dp),
                tint = PrimaryColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Cartridge Life", color = Color.Black)
                Text("${state.value.filterLife} Hrs", color = Color.Red)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.shop_icon),
                    contentDescription = "shop_icon",
                    modifier = Modifier.height(28.dp).width(30.dp),
                    tint = PrimaryColor
                )
                Spacer(modifier = Modifier.size(6.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Buy", color = Color.Black)
                    Text("Cartridge", color = Color.Black)
                }
                Icon(
                    painter = painterResource(Res.drawable.right_arrow),
                    contentDescription = "right_arrow",
                    modifier = Modifier.size(32.dp),
                    tint = PrimaryColor
                )
            }
        }
    }
}

@Composable
fun BottomButtonBox() {

    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        // Column inside the box
        Row {
            CircleIconButton(
                size = 64.dp,
                selectedColor = PrimaryColor,
                isSelected = state.value.isLedOn,
                drawableResource = Res.drawable.moon_solid,
                onClick = {
                    viewModel.indicatorLED(!state.value.isLedOn)
                },
                enabled = !state.value.power && state.value.isConnected
            )
            Spacer(modifier = Modifier.size(20.dp))
            CircleIconButton(
                size = 78.dp,
                selectedColor = Color.Red,
                isSelected = state.value.power,
                drawableResource = Res.drawable.power_off_solid,
                enabled = state.value.isConnected,
                onClick = {
                    viewModel.power(!state.value.power)
                }
            )
            Spacer(modifier = Modifier.size(20.dp))
            CircleIconButton(
                size = 64.dp,
                selectedColor = PrimaryColor,
                isSelected = state.value.echo,
                drawableResource = Res.drawable.leaf_solid,
                onClick = {
                    viewModel.echo(!state.value.echo)
                },
                enabled = !state.value.power && state.value.isConnected
            )
        }
    }
}

@Composable
fun CircleIconButton(
    size: Dp = 60.dp,
    selectedColor: Color = Color.Red,
    isSelected: Boolean,
    drawableResource: DrawableResource,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    IconButton(
        enabled = enabled,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .size(size) // Size of the button
            .border(
                BorderStroke(1.dp, Color.Gray), // Circular border
                shape = CircleShape // Apply circular shape
            )
            .background(Color.White, shape = CircleShape) // Background color with circular shape
    ) {
        // Icon inside the button
        Icon(
            painter = painterResource(drawableResource),
            contentDescription = "icon_description",
            tint = if (isSelected) {
                selectedColor
            } else {
                Color.LightGray
            }, // Toggle between red and gray
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
fun FANButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,    // State indicating if the button is selected
    onClick: () -> Unit,
    enabled: Boolean
) {
    // State to trigger the spinning animation
    var isSpinning by rememberSaveable() { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val rotation = rememberSaveable { Animatable(0f) }

    // Trigger animation on click
    BoxWithConstraints(
        modifier = modifier
            .size(80.dp) // Size of the button
            .clip(CircleShape) // Circular shape
            .border(
                BorderStroke(
                    6.dp, if (isSelected && enabled) {
                        Color.Red
                    } else {
                        Color.LightGray
                    }
                ), // Border color and thickness
                CircleShape
            )
            .background(Color.White)
            .clickable {
                if (enabled) {
                    onClick()
                    if (!isSpinning) {
                        isSpinning = true
                        scope.launch {
                            // Start spinning fast and gradually slow down
                            rotation.animateTo(
                                targetValue = rotation.value + 3600f,  // Spin multiple times
                                animationSpec = tween(
                                    durationMillis = 2000,
                                    easing = FastOutSlowInEasing  // Start fast, end slow
                                )
                            )
                            isSpinning = false // Reset after animation ends
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val width = maxWidth * 0.7f
        val height = maxHeight * 0.7f
        // Icon inside the circular button
        Icon(
            painter = painterResource(Res.drawable.fan_icon), // Your icon resource
            contentDescription = "Spinning Icon",
            tint = PrimaryColor,
            modifier = Modifier
                .rotate(rotation.value)
                .width(width)
                .height(height)
        )
    }
}