package com.example.cookingmasterclass.presentation.addrecipe

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cookingmasterclass.domain.models.Difficulty
import com.example.cookingmasterclass.domain.models.Recipe
import com.example.cookingmasterclass.presentation.recipe.cookingClick
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(navController: NavController) {
    val viewModel: AddRecipeViewModel = hiltViewModel()

    val recipe = remember {
        mutableStateOf(
            Recipe()
        )
    }

    val showIngredientDialog = remember { mutableStateOf(false) }
    val showStepDialog = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        getFileFromUri(viewModel.context, uri!!).also {
            val bitmap = BitmapFactory.decodeFile(it?.absolutePath)
            recipe.value = recipe.value.copy(photo = saveBitmapToFile(viewModel.context, bitmap))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Создание рецепта") }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }

    ) {
        LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            item {
                CookingOutlinedTextBox(
                    "Введите название блюда",
                    value = recipe.value.title!!,
                    onValueChange = {
                        recipe.value =
                            recipe.value.copy(title = it)
                    },
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                )


                if (recipe.value.photo == null || recipe.value.photo == "") {
                    EmptyPhoto(onClick = {
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    })
                } else {
//                    val file = getFileFromUri(viewModel.context, Uri.parse(recipe.value.photo))
                    val file = File(recipe.value.photo)
                    val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
                    Image(
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(5.dp)),
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "recipe photo"
                    )

                }
                SegmentedButton(modifier = Modifier.padding(horizontal = 15.dp),
                    recipe.value.difficulty!!.id,
                    Difficulty.entries.toList().map { it.displayName },
                    onItemSelection = {
                        recipe.value.difficulty = Difficulty.entries.first { dif -> dif.id == it }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Время приготовления ${recipe.value.cookingTime!! / 60}:${recipe.value.cookingTime!! % 60}",
                        modifier = Modifier.cookingClick {
                            showTimePicker.value = true
                        })
                }

            }
            item {
                FlowRow(
                    maxLines = 3,
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ингредиенты: ", modifier = Modifier.padding(vertical = 4.dp))
                    recipe.value.ingredients?.forEach {
                        Card(modifier = Modifier.padding(horizontal = 3.dp)) {
                            Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)) {
                                Text(it)
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "delete $it",
                                    modifier = Modifier.cookingClick {
                                        recipe.value =
                                            recipe.value.copy(ingredients = recipe.value.ingredients!!.filter { ingr -> ingr != it })
                                    })
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add ingredien",
                        modifier = Modifier
                            .padding(4.dp)
                            .cookingClick {
                                showIngredientDialog.value = true
                            })

                }
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .cookingClick { showStepDialog.value = true }) {
                    Text("Добавить шаг  ")
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Добавить шаг"
                    )
                }
            }
            itemsIndexed(recipe.value.step!!) { index, step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 15.dp)
                ) {
                    Text(
                        "  $index. $step  ",
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                    )
                }
            }

            item {
                if (showStepDialog.value)
                    AddDialog(
                        cancelText = "Отмена",
                        okText = "Добавить",
                        text = "Введите шаг",
                        onCancel = { showStepDialog.value = false },
                        onOk = {
                            recipe.value = recipe.value.copy(step = recipe.value.step!! + it)

                            Log.e(TAG, recipe.value.step.toString())
                            showStepDialog.value = false
                        },
                        onDismiss = { showStepDialog.value = false }
                    )
                if (showIngredientDialog.value)
                    AddDialog(
                        cancelText = "Отмена",
                        okText = "Добавить",
                        text = "Введите ингредиет",
                        onCancel = { showIngredientDialog.value = false },
                        onOk = {
                            recipe.value =
                                recipe.value.copy(ingredients = recipe.value.ingredients!! + it)

                            showIngredientDialog.value = false
                        },
                        onDismiss = { showIngredientDialog.value = false }
                    )


                if (showTimePicker.value)
                    CookingTimePicker(
                        onConfirm = {
                            recipe.value = recipe.value.copy(cookingTime = it)
                            showTimePicker.value = false
                        },
                        onDismiss = { showTimePicker.value = false }
                    )
            }
            item {
                CookingButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .navigationBarsPadding(),
                    text = "Сохранить",
                    isEnabled = (
                            recipe.value.photo != "" && recipe.value.photo != null &&
                                    recipe.value.title != "" && recipe.value.title != null &&
                                    !recipe.value.step!!.isEmpty() && recipe.value.step != null &&
                                    recipe.value.cookingTime != 0 && recipe.value.cookingTime != null &&
                                    !recipe.value.ingredients!!.isEmpty() && recipe.value.ingredients != null
                            )
                ) {
                    viewModel.saveRecipe(recipe.value)
                    navController.navigateUp()
                }
            }
        }
    }
}


@Throws(IOException::class)
fun createImageFile(context: Context, onSave: (String) -> Unit): File {
    val fileName = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(fileName, ".jpg", storageDir).apply {
        onSave(absolutePath)
    }
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap): String {
    var fileName = ""
    val file = createImageFile(context, onSave = { fileName = it })
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    outputStream.flush()
    outputStream.close()
    Log.d("BitmapSave", "Bitmap saved successfully: ${file.absolutePath}")
    return fileName
}


fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val cursor =
        context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        it.moveToFirst()
        return it.getString(columnIndex)
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingTimePicker(
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {

    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 30,
        is24Hour = true,
    )
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimePicker(
                state = timePickerState,
            )
            CookingOutlinedButton(
                text = "Отмена",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = onDismiss
            )
            CookingButton(
                text = "Сохранить",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
                onClick = { onConfirm(timePickerState.hour * 60 + timePickerState.minute) }
            )
        }
    }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    // Получаем имя файла
    val fileName = getFileName(context, uri) ?: "temp_file"

    // Создаем временный файл в кэше приложения
    val tempFile = File(context.cacheDir, fileName)

    // Открываем InputStream из URI
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        // Записываем данные из InputStream в файл
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return tempFile
}

fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

@Composable
fun EmptyPhoto(onClick: () -> Unit) {
    Card(modifier = Modifier
        .padding(15.dp)

        .cookingClick {
            onClick()
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "add photo")
        }
    }
}


@Composable
fun SegmentedButton(
    modifier: Modifier = Modifier,
    index: Int,
    items: List<String>,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(index) }
    var itemIndex by remember { mutableIntStateOf(index) }

    Card(
        modifier = Modifier.then(modifier),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 3.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                itemIndex = index
                val isSelected = selectedIndex == index
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                    animationSpec = tween(400),
                    label = ""
                )
                Card(
                    modifier = Modifier
                        .heightIn(42.dp)
                        .weight(1f)
                        .padding(0.dp),
                    onClick = {
                        selectedIndex = index
                        onItemSelection(selectedIndex)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor,
                        contentColor = MaterialTheme.colorScheme.scrim
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .heightIn(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            color = if (selectedIndex == index) Color.Black else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AddDialog(
    title: String = "",
    text: String = "",
    cancelText: String,
    okText: String,
    onCancel: () -> Unit,
    onOk: (String) -> Unit,
    onDismiss: () -> Unit = { }
) {
    val inputText: MutableState<String> = remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (title != "")
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
//                if (text != "")
//                    Text(
//                        text = text,
//                        style = MaterialTheme.typography.labelMedium,
//                        modifier = Modifier.fillMaxWidth()
//                    )
                CookingOutlinedTextBox(
                    text,
                    value = inputText.value,
                    onValueChange = { inputText.value = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    CookingOutlinedButton(
                        text = cancelText,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        onClick = onCancel
                    )
                    CookingButton(
                        text = okText,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        onClick = { onOk(inputText.value) }
                    )
                }
            }
        }
    }
}


@Composable
fun CookingButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.then(modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CookingOutlinedButton(
    text: String,
    isEnabled: Boolean = true,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        enabled = isEnabled,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.then(modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CookingOutlinedTextBox(
    label: String,
    value: String,
    fontSize: Int = 16,
    onValueChange: (String) -> Unit,
    keyboard: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    errorValue: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    hideKeyboard: Boolean = false,
    placeholderText: String = "",
    isEnabled: Boolean = true,
    onFocusClear: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    if (hideKeyboard) {
        focusManager.clearFocus()
        onFocusClear()
    }

    OutlinedTextField(
        label = { Text(text = label, fontSize = fontSize.sp) },
        value = value,
        placeholder = { Text(text = placeholderText) },
        onValueChange = onValueChange,
        modifier = Modifier.run {
            if (errorValue != "") {
                then(modifier)
                    .padding(bottom = 13.dp)
            } else {
                then(modifier)
            }
        },
        shape = RoundedCornerShape(16.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboard,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { focusManager.clearFocus() }
        ),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = if (singleLine) 1 else maxLines,
        supportingText = {
            if (errorValue.isNotBlank() && errorValue != "") {
                Text(text = errorValue, style = MaterialTheme.typography.bodyMedium)
            }
        },
        isError = errorValue.isNotBlank() && errorValue != "",
        textStyle = MaterialTheme.typography.bodyMedium,
        enabled = isEnabled
    )
}
