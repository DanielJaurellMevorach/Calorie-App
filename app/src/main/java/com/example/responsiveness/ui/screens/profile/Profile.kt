package com.example.responsiveness.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.OctagonAlert
import com.composables.icons.lucide.ShieldQuestion
import com.example.anothercalorieapp.database.MealDatabase
import com.example.responsiveness.ui.components.general.rememberSafeContentPadding
import com.example.responsiveness.ui.screens.profile.components.SettingAdjustableWheel
import com.example.responsiveness.ui.screens.profile.viewmodel.ProfileViewModel
import com.example.responsiveness.ui.screens.profile.viewmodel.ProfileViewModelFactory
import com.example.responsiveness.ui.theme.DesignTokens
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel? = null
) {
    val context = LocalContext.current.applicationContext
    val mealDatabase = MealDatabase.getDatabase(context)
    val mealRepository = remember { com.example.responsiveness.database.repository.MealRepository(mealDatabase.mealDao()) }
    val factory = remember { ProfileViewModelFactory(mealRepository) }
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)

    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Expanded state for each wheel (persisted)
    val caloriesExpanded = rememberSaveable { mutableStateOf(true) }
    val proteinExpanded = rememberSaveable { mutableStateOf(false) }
    val carbsExpanded = rememberSaveable { mutableStateOf(false) }
    val fatExpanded = rememberSaveable { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalOverscrollFactory provides null
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            val tokens = DesignTokens.provideTokens(maxWidth, maxHeight)
            val userSettings by profileViewModel.userSettings.collectAsState()

            val calories = userSettings?.maxCalories?.toFloat() ?: 2000f
            val protein = userSettings?.maxProtein?.toFloat() ?: 150f
            val carbs = userSettings?.maxCarbs?.toFloat() ?: 250f
            val fat = userSettings?.maxFat?.toFloat() ?: 70f
            val apiKey = userSettings?.apiKey ?: ""

            // Local state for API key input to avoid real-time database updates
            var localApiKey by remember(apiKey) { mutableStateOf(apiKey) }

            // Get proper content padding that accounts for the status bar and floating navigation bar.
            val safePadding = rememberSafeContentPadding(
                includeStatusBar = true,
                includeNavigationBar = true,
                additionalBottomPadding = tokens.navContainerHeight + tokens.navHorizontalPadding * 2 + tokens.sDp(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(safePadding) // Use the calculated safe padding directly
                    .padding(horizontal = tokens.outerInset), // Apply horizontal padding separately
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SettingAdjustableWheel(
                    initialValue = calories,
                    unitName = "Calories",
                    tokens = tokens,
                    minValue = 0f,
                    maxValue = 3000f,
                    step = 1f,
                    onValueChange = { profileViewModel.updateMaxCalories(it.roundToInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    defaultExpanded = caloriesExpanded.value,
                    onExpandedChange = { caloriesExpanded.value = it },
                    fallbackValue = 2000f
                )
                SettingAdjustableWheel(
                    initialValue = protein,
                    unitName = "Protein",
                    tokens = tokens,
                    minValue = 0f,
                    maxValue = 300f,
                    step = 1f,
                    onValueChange = { profileViewModel.updateMaxProtein(it.roundToInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    defaultExpanded = proteinExpanded.value,
                    onExpandedChange = { proteinExpanded.value = it },
                    fallbackValue = 150f
                )
                SettingAdjustableWheel(
                    initialValue = carbs,
                    unitName = "Carbs",
                    tokens = tokens,
                    minValue = 0f,
                    maxValue = 500f,
                    step = 1f,
                    onValueChange = { profileViewModel.updateMaxCarbs(it.roundToInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    defaultExpanded = carbsExpanded.value,
                    onExpandedChange = { carbsExpanded.value = it },
                    fallbackValue = 250f
                )
                SettingAdjustableWheel(
                    initialValue = fat,
                    unitName = "Fat",
                    tokens = tokens,
                    minValue = 0f,
                    maxValue = 200f,
                    step = 1f,
                    onValueChange = { profileViewModel.updateMaxFat(it.roundToInt()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    defaultExpanded = fatExpanded.value,
                    onExpandedChange = { fatExpanded.value = it },
                    fallbackValue = 70f
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp))
                        .clip(RoundedCornerShape(tokens.corner))
                        .background(Color.Black)
                        .padding(tokens.sDp(16.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Using your own OpenAI API key is a requirement. Processing will be made at your own expense.",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            fontSize = tokens.calendarTextSize.times(1.2),
                        )
                        Spacer(modifier = Modifier.height(tokens.sDp(16.dp)))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(tokens.sDp(16.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = localApiKey,
                                onValueChange = { localApiKey = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(tokens.sDp(38.dp))
                                    )
                                    .padding(horizontal = tokens.sDp(16.dp)),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color.Black
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        Log.d("ProfileScreen", "Keyboard 'Done' clicked.")
                                        profileViewModel.submitApiKey(localApiKey)
                                        Log.d("ProfileScreen", "API key submitted.")
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        Log.d("ProfileScreen", "Focus clear requested.")
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (localApiKey.isEmpty()) {
                                            Text(
                                                text = "Insert key",
                                                color = Color.Gray.copy(alpha = 0.6f),
                                                fontSize = tokens.calendarTextSize.times(1.2),
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            Button(
                                onClick = {
                                    Log.d("ProfileScreen", "Paste button clicked.")
                                    val clipText = clipboardManager.getText()?.text ?: ""
                                    if (clipText.isNotBlank()) {
                                        localApiKey = clipText
                                        profileViewModel.submitApiKey(clipText)
                                        Log.d("ProfileScreen", "API key pasted and submitted.")
                                    }
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    Log.d("ProfileScreen", "Focus cleared on paste.")
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .size(48.dp),
                                colors = buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(tokens.sDp(38.dp)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ClipboardCopy,
                                    contentDescription = "Paste from clipboard",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp))
                        .clip(RoundedCornerShape(tokens.corner))
                        .background(Color.Black)
                        .padding(tokens.sDp(16.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.ShieldQuestion,
                            contentDescription = "Privacy Shield",
                            tint = Color.White,
                            modifier = Modifier.size(tokens.sDp(40.dp))
                        )
                        Text(
                            text = "No personal or identifiable data is collected. Your API key is stored locally in plain text.",
                            color = Color.White,
                            modifier = Modifier.padding(start = tokens.sDp(16.dp)),
                            fontSize = tokens.calendarTextSize.times(1.2),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp))
                        .clip(RoundedCornerShape(tokens.corner))
                        .background(Color.Black)
                        .padding(tokens.sDp(16.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.OctagonAlert,
                            contentDescription = "How to use disclaimer",
                            tint = Color.White,
                            modifier = Modifier.size(tokens.sDp(40.dp))
                        )
                        Text(
                            text = "The use of this app does not guarantee a complete or accurate nutritional analysis. At most, it should be used as a rough estimate to provide insight into your habits.",
                            color = Color.White,
                            modifier = Modifier.padding(start = tokens.sDp(16.dp)),
                            fontSize = tokens.calendarTextSize.times(1.2),
                        )
                    }
                }
            }
        }
    }
}