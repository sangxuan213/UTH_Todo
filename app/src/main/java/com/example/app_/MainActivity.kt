package com.example.app_

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_.ui.screen.*
import com.example.app_.ui.theme.AppPalette
import com.example.app_.ui.theme.App_Theme
import com.example.app_.ui.viewmodel.TaskViewModel
import com.example.app_.ui.viewmodel.headerDateText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPalette.init(this) // nạp giao diện/màu chủ đạo đã lưu
        setContent {
            App_Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(taskViewModel: TaskViewModel = viewModel()) {
    val navController = rememberNavController()

    // Bài 1: nạp danh sách từ API ngay khi app mở
    LaunchedEffect(Unit) { taskViewModel.loadTasks() }

    // Chuyển tab ở thanh dưới
    val navTab: (String) -> Unit = { route ->
        if (route == "home") {
            navController.navigate("home") { popUpTo("home") { inclusive = true } }
        } else {
            navController.navigate(route) {
                popUpTo("home")
                launchSingleTop = true
            }
        }
    }
    val openDetail: (Int, String) -> Unit = { id, from ->
        navController.navigate("task_detail/$id?from=${Uri.encode(from)}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "splash") {

            composable("splash") {
                SplashScreen(
                    onNavigateToOnboarding = {
                        if (Firebase.auth.currentUser != null) {
                            navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                        } else {
                            navController.navigate("onboarding_1") { popUpTo("splash") { inclusive = true } }
                        }
                    }
                )
            }

            composable("onboarding_1") {
                OnboardingScreen1(
                    onSkip = { navController.navigate("login") { popUpTo(0) } },
                    onNext = { navController.navigate("onboarding_2") }
                )
            }

            composable("onboarding_2") {
                OnboardingScreen2(
                    onSkip = { navController.navigate("login") { popUpTo(0) } },
                    onNext = { navController.navigate("onboarding_3") }
                )
            }

            composable("onboarding_3") {
                OnboardingScreen3(
                    onSkip = { navController.navigate("login") { popUpTo(0) } },
                    onStart = { navController.navigate("login") { popUpTo(0) } }
                )
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("home") { popUpTo(0) } },
                    onGuestLogin = { navController.navigate("home") { popUpTo(0) } }
                )
            }

            // Trang chủ: chỉ hiện việc của hôm nay, đếm theo nhóm trên toàn bộ việc
            composable("home") {
                val today = taskViewModel.todayStr
                HomeScreen(
                    tasks = taskViewModel.tasks.filter { it.date == today },
                    allTasks = taskViewModel.tasks,
                    dateText = headerDateText(today),
                    hasUnread = taskViewModel.hasUnreadNotifications,
                    onToggleTask = { taskViewModel.toggleTask(it) },
                    onProfileClick = { navController.navigate("profile") },
                    onBellClick = { navTab("notifications") },
                    onNavigate = navTab,
                    onTaskClick = { id -> openDetail(id, "Trang chủ") },
                    onAddClick = { navController.navigate("add_task") }
                )
            }

            composable("task_list") {
                TaskListScreen(
                    tasks = taskViewModel.tasks,
                    onToggleTask = { taskViewModel.toggleTask(it) },
                    onTaskClick = { id -> openDetail(id, "Danh sách") },
                    onNavigate = navTab,
                    onAddClick = { navController.navigate("add_task") }
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    hasUnread = taskViewModel.hasUnreadNotifications,
                    onMarkAllRead = { taskViewModel.markAllNotificationsRead() },
                    onNavigate = navTab,
                    onAddClick = { navController.navigate("add_task") }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onNavigate = navTab,
                    onAddClick = { navController.navigate("add_task") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }

            // Việc mới
            composable("add_task") {
                LaunchedEffect(Unit) { taskViewModel.resetAddTaskForm() }
                AddTaskScreen(
                    viewModel = taskViewModel,
                    onBack = { navController.popBackStack() },
                    // Thêm xong thì mở luôn chi tiết việc vừa tạo (giống app mẫu)
                    onSaveSuccess = { newId ->
                        navController.navigate("task_detail/$newId?from=${Uri.encode("Danh sách")}") {
                            popUpTo("add_task") { inclusive = true }
                        }
                    }
                )
            }

            // Sửa công việc
            composable(
                route = "edit_task/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
                LaunchedEffect(taskId) { taskViewModel.startEdit(taskId) }
                AddTaskScreen(
                    viewModel = taskViewModel,
                    editingTaskId = taskId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Chi tiết công việc
            composable(
                route = "task_detail/{taskId}?from={from}",
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType },
                    navArgument("from") {
                        type = NavType.StringType
                        defaultValue = "Danh sách"
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
                val from = backStackEntry.arguments?.getString("from") ?: "Danh sách"
                val task = taskViewModel.tasks.find { it.id == taskId }

                // Bài 2: mở chi tiết thì gọi GET /tasks/{id}
                LaunchedEffect(taskId) { taskViewModel.loadTask(taskId) }

                TaskDetailScreen(
                    task = task,
                    subtasks = taskViewModel.subtasks[taskId] ?: emptyList(),
                    backLabel = from,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("edit_task/$taskId") },
                    onToggleTask = { taskViewModel.toggleTask(it) },
                    onDeleteTask = { taskViewModel.deleteTask(it) },
                    onAddSubtask = { taskViewModel.addSubtask(taskId, it) },
                    onToggleSubtask = { taskViewModel.toggleSubtask(taskId, it) },
                    onDeleteSubtask = { taskViewModel.deleteSubtask(taskId, it) }
                )
            }

            // Hồ sơ
            composable("profile") {
                ProfileScreen(
                    tasks = taskViewModel.tasks,
                    todayStr = taskViewModel.todayStr,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        Firebase.auth.signOut()
                        navController.navigate("login") { popUpTo(0) }
                    }
                )
            }
        }

        // Lớp phủ trên cùng: đang tải, lỗi (kèm thử lại), toast
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            if (taskViewModel.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppPalette.primary
                )
            }
            taskViewModel.errorMessage?.let { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .background(Color(0xFFFFE5E5), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        color = Color(0xFFB00020),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { taskViewModel.dismissError(); taskViewModel.loadTasks() }) { Text("Thử lại") }
                    TextButton(onClick = { taskViewModel.dismissError() }) { Text("Đóng") }
                }
            }
            ToastHost(
                message = taskViewModel.toast,
                onDismiss = { taskViewModel.clearToast() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
