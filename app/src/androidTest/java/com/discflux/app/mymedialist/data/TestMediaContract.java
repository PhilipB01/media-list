/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.discflux.app.mymedialist.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Students: This is NOT a complete test for the MediaContract --- just for the functions
    that we expect you to write.
 */
public class TestMediaContract extends AndroidTestCase {

    private static final String TEST_TITLE = "TheBigDeep";
    private static final int TEST_TYPE = 200;  // TV SERIES

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildMediaTitle() {
        Uri mediaUri = MediaContract.MediaEntry.buildMediaTitle(TEST_TYPE, TEST_TITLE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMediaLocation in " +
                        "MediaContract.",
                mediaUri);
        assertEquals("Error: Media location not properly appended to the end of the Uri",
                TEST_TITLE, mediaUri.getLastPathSegment());
        assertEquals("Error: Media location Uri doesn't match our expected result",
                mediaUri.toString(),
                "content://com.discflux.app.mymedialist/media/200/TheBigDeep");
    }
}