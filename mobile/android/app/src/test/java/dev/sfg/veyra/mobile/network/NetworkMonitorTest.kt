/*
 * Copyright (C) 2026 AKASHI DEV 
 *
 * This file is part of Veyra Music and is licensed under the GNU Affero General
 * Public License, version 3 or later.
 */

package dev.sfg.veyra.mobile.network

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkMonitorTest {
    @Test
    fun `internet capability without validation is treated as offline`() {
        assertFalse(hasValidatedInternet(hasInternetCapability = true, isValidated = false))
    }

    @Test
    fun `validated internet is online`() {
        assertTrue(hasValidatedInternet(hasInternetCapability = true, isValidated = true))
    }

    @Test
    fun `validation without internet capability is offline`() {
        assertFalse(hasValidatedInternet(hasInternetCapability = false, isValidated = true))
    }
}
