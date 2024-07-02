package presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import util.TestTag.ACTIVE_LOADING_INDICATOR
import util.TestTag.COMPLETED_LOADING_INDICATOR

@Composable
fun LoadingScreen(activeSection: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(28.dp)
                .testTag(
                    tag = if (activeSection) ACTIVE_LOADING_INDICATOR
                    else COMPLETED_LOADING_INDICATOR
                )
        )
    }
}