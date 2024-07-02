import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import data.MongoDBTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import navigation.SetupNavGraph
import util.TestTag.ACTIVE_TASK_ICON
import util.TestTag.ALERT_NEGATIVE_BUTTON
import util.TestTag.ALERT_POSITIVE_BUTTON
import util.TestTag.CHECKBOX
import util.TestTag.COMPLETED_TASK_ICON
import util.TestTag.DESC_TEXT_FIELD
import util.TestTag.HOME_FAB
import util.TestTag.HOME_SCREEN
import util.TestTag.LAZY_COLUMN
import util.TestTag.TASK_FAB
import util.TestTag.TASK_SCREEN
import util.TestTag.TASK_SCREEN_BACK_ARROW
import util.TestTag.TASK_VIEW
import util.TestTag.TEST_TITLE
import util.TestTag.TEST_TITLE2
import util.TestTag.TITLE_TEXT_FIELD
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class NavigationTest {
    private var mongoDB = MongoDBTest()

    @BeforeTest
    fun setUp() {
        mongoDB = MongoDBTest()
    }

    private fun runWithContent(block: ComposeUiTest.() -> Unit) =
        runComposeUiTest {
            setContent {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    mongoDB = mongoDB
                )
            }
            block()
        }

    @Test
    fun navigateToTaskScreen_assertTaskScreenDisplayed() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                onNodeWithTag(TASK_SCREEN).assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_assertTaskFabDisplayed() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                onNodeWithTag(TASK_FAB).assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_goBack_assertHomeScreenDisplayed() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                onNodeWithTag(TASK_SCREEN).assertExists()
                delay(1000)
                onNodeWithTag(TASK_SCREEN_BACK_ARROW).performClick()
                onNodeWithTag(HOME_SCREEN).assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_clearTitleTextField_assertTaskFabDoesNotExist() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TASK_FAB).assertDoesNotExist()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_clearDescriptionTextField_assertTaskFabDoesNotExist() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(DESC_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TASK_FAB).assertDoesNotExist()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_clearTitleAndDescriptionTextFields_assertTaskFabDoesNotExist() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(DESC_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TASK_FAB).assertDoesNotExist()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_writeTaskInfo_addNewTask_goBack_assertTaskWithSameInfoAddedSuccessfully() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_addNewTask_goBack_clickOnNewTask_updateItsTitle_goBack_assertTaskWithUpdatedTitle() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE2)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE2}").assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_addNewTask_goBack_markTheNewTaskAsCompleted_clickDeleteIcon_confirmRemoval_assertTaskRemoved() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertExists()
                delay(1000)
                onNodeWithTag("${CHECKBOX}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag("${COMPLETED_TASK_ICON}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag(ALERT_POSITIVE_BUTTON).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertDoesNotExist()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_addNewTask_goBack_markTheNewTaskAsCompleted_clickDeleteIcon_cancelRemoval_assertTaskStillExists() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertExists()
                delay(1000)
                onNodeWithTag("${CHECKBOX}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag("${COMPLETED_TASK_ICON}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag(ALERT_NEGATIVE_BUTTON).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_addNewTask_goBack_markTheNewTaskAsCompleted_clickCheckboxAgain_assertTaskIsPlacesBackInActiveTasksSection() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag("${TASK_VIEW}${TEST_TITLE}").assertExists()
                delay(1000)
                onNodeWithTag("${CHECKBOX}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag("${COMPLETED_TASK_ICON}${TEST_TITLE}").assertExists()
                delay(1000)
                onNodeWithTag("${CHECKBOX}${TEST_TITLE}").performClick()
                delay(1000)
                onNodeWithTag("${ACTIVE_TASK_ICON}${TEST_TITLE}").assertExists()
                delay(2000)
            }
        }

    @Test
    fun navigateToTaskScreen_addNewTask_goBack_navigateToTaskScreenAgain_addAnotherTask_goBack_markTheSecondTaskAsFavorite_assertThatSecondTaskIsNowOnTopOfTheList() =
        runWithContent {
            runBlocking {
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag(HOME_FAB).performClick()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextClearance()
                delay(1000)
                onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(TEST_TITLE2)
                delay(1000)
                onNodeWithTag(TASK_FAB).performClick()
                delay(1000)
                onNodeWithTag(LAZY_COLUMN).onChildAt(1).assertTextEquals(TEST_TITLE2)
                delay(1000)
                onNodeWithTag("${ACTIVE_TASK_ICON}${TEST_TITLE2}").performClick()
                delay(1000)
                onNodeWithTag(LAZY_COLUMN).onChildAt(0).assertTextEquals(TEST_TITLE2)
                delay(2000)
            }
        }
}