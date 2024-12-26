package org.kmm.airpurifier

import androidx.compose.runtime.Composable

@Composable
expect fun RequestPermissions(content: @Composable () -> Unit)