package com.example.tasklist.ui

import com.example.tasklist.data.Task
import com.example.tasklist.data.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val repository = mockk<TaskRepository>(relaxed = true)
    private lateinit var viewModel: TaskViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.allTasks } returns flowOf(emptyList())
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTask calls repository insert`() = runTest {
        val title = "Test Task"
        viewModel.addTask(title)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.insert(match { it.title == title }) }
    }

    @Test
    fun `deleteTask calls repository delete`() = runTest {
        val task = Task(id = 1, title = "Delete me")
        viewModel.deleteTask(task)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.delete(task) }
    }
}
