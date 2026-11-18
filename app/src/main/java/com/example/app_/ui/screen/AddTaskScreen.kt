package com.example.app_.ui.screen

import com.example.app_.ui.theme.AppPalette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_.ui.theme.BeVietnamProFontFamily
import com.example.app_.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onSaveSuccess: (Int) -> Unit,
    editingTaskId: Int? = null
) {
    val state = viewModel.addTaskUiState
    val isFormValid = state.title.isNotBlank()
    val isEditing = editingTaskId != null
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = BeVietnamProFontFamily)
    ) {
        Scaffold(
            containerColor = AppPalette.bg
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header bar: Hủy  ...  Việc mới  ...  Lưu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hủy",
                        fontSize = 16.sp,
                        color = AppPalette.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            viewModel.resetAddTaskForm()
                            onBack()
                        }
                    )

                    Text(
                        text = if (isEditing) "Sửa công việc" else "Việc mới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPalette.text
                    )

                    Text(
                        text = "Lưu",
                        fontSize = 16.sp,
                        color = if (isFormValid) AppPalette.primary else AppPalette.placeholder,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = isFormValid) {
                            viewModel.saveForm(editingTaskId) { onSaveSuccess(it) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tên công việc
                Text(text = "Tên công việc", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = { Text("Ví dụ: Ôn chương 4 – Giải thuật", fontSize = 15.sp, color = AppPalette.textTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppPalette.surface,
                        unfocusedContainerColor = AppPalette.surface,
                        focusedBorderColor = AppPalette.primary,
                        unfocusedBorderColor = AppPalette.border
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Ghi chú
                Text(text = "Ghi chú", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.updateNote(it) },
                    placeholder = { Text("Thêm ghi chú (không bắt buộc)", fontSize = 14.sp, color = AppPalette.textTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppPalette.surface,
                        unfocusedContainerColor = AppPalette.surface,
                        focusedBorderColor = AppPalette.primary,
                        unfocusedBorderColor = AppPalette.border
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nhóm (Category Chips)
                Text(text = "Nhóm", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Học tập", "Cuộc sống").forEach { cat ->
                        val isSelected = state.category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    if (isSelected) AppPalette.categoryBlueBg else AppPalette.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AppPalette.categoryBlue else AppPalette.border,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.updateCategory(cat) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) AppPalette.categoryBlue else AppPalette.text
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ngày (Date Chips)
                Text(text = "Ngày", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Hôm nay", "Ngày mai", "Tuần này").forEach { dateOpt ->
                        val isSelected = state.dateOption == dateOpt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    if (isSelected) AppPalette.primarySoft else AppPalette.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AppPalette.primary else AppPalette.border,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.updateDateOption(dateOpt) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dateOpt,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) AppPalette.primary else AppPalette.text
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Giờ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, AppPalette.border, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AccessTime, contentDescription = "Giờ", tint = AppPalette.textSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Giờ", fontSize = 15.sp, color = AppPalette.text, modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .background(AppPalette.fill, RoundedCornerShape(8.dp))
                            .clickable {
                                val parts = state.time.split(":")
                                val h = parts.getOrNull(0)?.toIntOrNull() ?: 19
                                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        viewModel.updateTime(String.format(java.util.Locale.US, "%02d:%02d", hour, minute))
                                    },
                                    h, m, false
                                ).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = formatTimeVi(state.time), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppPalette.text)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Outlined.AccessTime, contentDescription = "Pick", tint = AppPalette.textSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nhắc tôi (Toggle Switch)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, AppPalette.border, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Nhắc nhở", tint = AppPalette.textSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Nhắc tôi", fontSize = 15.sp, color = AppPalette.text)
                        Text(text = state.reminderOption, fontSize = 12.sp, color = AppPalette.textTertiary)
                    }

                    Switch(
                        checked = state.isReminderEnabled,
                        onCheckedChange = { viewModel.updateReminderEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF34C759)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mức ưu tiên (Thấp, Vừa, Cao)
                Text(text = "Mức ưu tiên", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppPalette.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.fill, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    listOf("Thấp", "Vừa", "Cao").forEach { prio ->
                        val isSelected = state.priority == prio
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(
                                    if (isSelected) AppPalette.surface else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updatePriority(prio) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prio,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AppPalette.text else AppPalette.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Nút Lưu công việc
                Button(
                    onClick = {
                        viewModel.saveForm(editingTaskId) { onSaveSuccess(it) }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPalette.primary,
                        disabledContainerColor = AppPalette.border
                    )
                ) {
                    Text(
                        text = if (isEditing) "Lưu thay đổi" else "Lưu công việc",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFormValid) Color.White else AppPalette.textTertiary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/** "19:00" -> "07:00 CH", "06:30" -> "06:30 SA" (định dạng giờ kiểu Việt Nam như app mẫu) */
private fun formatTimeVi(time: String): String {
    val parts = time.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return time
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val suffix = if (h < 12) "SA" else "CH"
    val h12 = if (h % 12 == 0) 12 else h % 12
    return String.format(java.util.Locale.US, "%02d:%02d %s", h12, m, suffix)
}
