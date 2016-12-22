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

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String TITLE_QUERY = "London, UK";
    private static final int TYPE_QUERY = 100;

    // content://com.example.android.sunshine.app/media"
    private static final Uri TEST_MEDIA_DIR = MediaContract.MediaEntry.CONTENT_URI;
    // content://com.example.android.sunshine.app/media/100/London, UK"
    private static final Uri TEST_MEDIA_WITH_TITLE = MediaContract.MediaEntry.buildMediaTitle(TYPE_QUERY, TITLE_QUERY);
    // content://com.example.android.sunshine.app/media/100"
    private static final Uri TEST_MEDIA_WITH_TYPE = MediaContract.MediaEntry.buildMediaType(TYPE_QUERY);


    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MediaProvider.buildUriMatcher();

        assertEquals("Error: The MEDIA URI was matched incorrectly.",
                testMatcher.match(TEST_MEDIA_DIR), MediaProvider.MEDIA); // 100
        assertEquals("Error: The MEDIA WITH TYPE URI was matched incorrectly.",
                testMatcher.match(TEST_MEDIA_WITH_TITLE), MediaProvider.MEDIA_WITH_TYPE_AND_TITLE); // 101
        assertEquals("Error: The MEDIA WITH TYPE was matched incorrectly.",
                testMatcher.match(TEST_MEDIA_WITH_TYPE), MediaProvider.MEDIA_WITH_TYPE); // 102


    }
}