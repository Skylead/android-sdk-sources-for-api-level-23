/*
 * Copyright (C) 2015 The Android Open Source Project
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

package android.support.v7.util;

import android.support.test.runner.AndroidJUnit4;
import android.support.v7.util.TileList;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TileListTest {
    int mTileSize = 3;
    TileList<Integer> mTileList;

    @Before
    public void setUp() throws Exception {
        mTileList = new TileList<>(mTileSize);
    }

    @Test
    public void testEmptyGet() {
        assertThat(mTileList.getItemAt(3), nullValue());
        assertThat(mTileList.getItemAt(100), nullValue());
    }

    @Test
    public void testGetItemAt() {
        assertThat(mTileList.addOrReplace(createTile(0, 1, 2, 3)), nullValue());
        assertThat(mTileList.getItemAt(0).intValue(), is(1));
        assertThat(mTileList.getItemAt(1).intValue(), is(2));
        assertThat(mTileList.getItemAt(2).intValue(), is(3));
        assertThat(mTileList.getItemAt(3), nullValue());
    }

    @Test
    public void testSize() {
        assertThat(mTileList.size(), is(0));
        assertThat(mTileList.addOrReplace(createTile(0, 1, 2, 3)), nullValue());
        assertThat(mTileList.size(), is(1));
        assertThat(mTileList.addOrReplace(createTile(0, 3, 4, 5)), notNullValue());
        assertThat(mTileList.size(), is(1));
        assertThat(mTileList.addOrReplace(createTile(3, 1, 2, 3)), nullValue());
        assertThat(mTileList.size(), is(2));

        mTileList.clear();
        assertThat(mTileList.size(), is(0));
    }

    @Test
    public void testGetAtIndex() {
        assertThat(mTileList.addOrReplace(createTile(0, 1, 2, 3)), nullValue());
        assertThat(mTileList.addOrReplace(createTile(3, 1, 2, 3)), nullValue());
        assertThat(mTileList.addOrReplace(createTile(6, 1, 2, 3)), nullValue());

        assertThat(mTileList.getAtIndex(0).mStartPosition, is(0));
        assertThat(mTileList.getAtIndex(1).mStartPosition, is(3));
        assertThat(mTileList.getAtIndex(2).mStartPosition, is(6));
        assertThat(mTileList.getAtIndex(3), nullValue());
    }

    public void testAddShortTileAndGet() {
        assertThat(mTileList.addOrReplace(createTile(0, 1)), nullValue());
        assertThat(mTileList.getItemAt(0).intValue(), is(1));
        assertThat(mTileList.getItemAt(1).intValue(), nullValue());
        assertThat(mTileList.getItemAt(2).intValue(), nullValue());
    }

    @Test
    public void testAddToReplaceAndGet() {
        TileList.Tile<Integer> prev = createTile(0, 1, 2, 3);
        mTileList.addOrReplace(prev);
        assertThat(mTileList.addOrReplace(createTile(0, 4, 5, 6)), sameInstance(prev));
        assertThat(mTileList.getItemAt(0).intValue(), is(4));
        assertThat(mTileList.getItemAt(1).intValue(), is(5));
        assertThat(mTileList.getItemAt(2).intValue(), is(6));
        assertThat(mTileList.getItemAt(3), nullValue());
    }

    @Test
    public void testAddRangeWithGapAndGet() {
        mTileList.addOrReplace(createTile(0, 1, 2, 3));
        assertThat(mTileList.addOrReplace(createTile(mTileSize * 2, 4, 5, 6)), nullValue());
        assertThat(mTileList.getItemAt(0).intValue(), is(1));
        assertThat(mTileList.getItemAt(1).intValue(), is(2));
        assertThat(mTileList.getItemAt(2).intValue(), is(3));
        assertThat(mTileList.getItemAt(mTileSize), nullValue());
        assertThat(mTileList.getItemAt(mTileSize + 1), nullValue());
        assertThat(mTileList.getItemAt(mTileSize + 2), nullValue());
        assertThat(mTileList.getItemAt(mTileSize * 2).intValue(), is(4));
        assertThat(mTileList.getItemAt(mTileSize * 2 + 1).intValue(), is(5));
        assertThat(mTileList.getItemAt(mTileSize * 2 + 2).intValue(), is(6));
        assertThat(mTileList.addOrReplace(createTile(mTileSize, 7, 8, 9)), nullValue());
        assertThat(mTileList.getItemAt(mTileSize).intValue(), is(7));
        assertThat(mTileList.getItemAt(mTileSize + 1).intValue(), is(8));
        assertThat(mTileList.getItemAt(mTileSize + 2).intValue(), is(9));
    }

    @Test
    public void testRemove() {
        mTileList.addOrReplace(createTile(0, 1, 2, 3));
        mTileList.addOrReplace(createTile(3, 4, 5, 6));
        mTileList.addOrReplace(createTile(6, 7, 8, 9));
        mTileList.addOrReplace(createTile(9, 10, 11, 12));

        assertThat(mTileList.removeAtPos(0).mStartPosition, is(0));
        assertThat(mTileList.size(), is(3));

        assertThat(mTileList.removeAtPos(6).mStartPosition, is(6));
        assertThat(mTileList.size(), is(2));

        assertThat(mTileList.removeAtPos(9).mStartPosition, is(9));
        assertThat(mTileList.size(), is(1));
    }

    private TileList.Tile<Integer> createTile(int startPosition, int... items) {
        TileList.Tile<Integer> window = new TileList.Tile<>(Integer.class, mTileSize);
        window.mStartPosition = startPosition;
        window.mItemCount = items.length;
        for (int i = 0; i < items.length; i ++) {
            window.mItems[i] = items[i];
        }
        return window;
    }
}
