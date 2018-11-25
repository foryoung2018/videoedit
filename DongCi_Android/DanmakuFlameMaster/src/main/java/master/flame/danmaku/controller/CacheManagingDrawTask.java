/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
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

package master.flame.danmaku.controller;

import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDrawingCache;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.DanmakuContext.DanmakuConfigTag;
import master.flame.danmaku.danmaku.renderer.IRenderer.RenderingState;
import tv.cjump.jni.NativeBitmapFactory;

public class CacheManagingDrawTask extends DrawTask {

    private static final int MAX_CACHE_SCREEN_SIZE = 3;

    public int mMaxCacheSize = 2;

    public CacheManager mCacheManager;

    public DanmakuTimer mCacheTimer;

    public final Object mDrawingNotify = new Object();
    private int mRemaininCacheCount;

    public CacheManagingDrawTask(DanmakuTimer timer, DanmakuContext config, TaskListener taskListener) {
        super(timer, config, taskListener);
        NativeBitmapFactory.loadLibs();
        mMaxCacheSize = (int) Math.max(1024 * 1024 * 4, Runtime.getRuntime().maxMemory() * config.cachingPolicy.maxCachePoolSizeFactorPercentage);
        mCacheManager = new CacheManager(this, mMaxCacheSize, MAX_CACHE_SCREEN_SIZE);
        mRenderer.setCacheManager(mCacheManager);
    }

    @Override
    protected void initTimer(DanmakuTimer timer) {
        mTimer = timer;
        mCacheTimer = new DanmakuTimer();
        mCacheTimer.update(timer.currMillisecond);
    }

    @Override
    public void addDanmaku(BaseDanmaku danmaku) {
        super.addDanmaku(danmaku);
        if (mCacheManager == null)
            return;
        mCacheManager.addDanmaku(danmaku);
    }

    @Override
    public void invalidateDanmaku(BaseDanmaku item, boolean remeasure) {
        if (mCacheManager == null) {
            super.invalidateDanmaku(item, remeasure);
            return;
        }
        mCacheManager.invalidateDanmaku(item, remeasure);
    }

    @Override
    public void removeAllDanmakus(boolean isClearDanmakusOnScreen) {
        super.removeAllDanmakus(isClearDanmakusOnScreen);
        if (mCacheManager != null) {
            mCacheManager.requestClearAll();
        }
    }

    @Override
    protected void onDanmakuRemoved(BaseDanmaku danmaku) {
        super.onDanmakuRemoved(danmaku);
        if (mCacheManager != null) {
            if (++mRemaininCacheCount > 5) {  // control frequency (it does not require very precise
                mCacheManager.requestClearTimeout();
                mRemaininCacheCount = 0;
            }
        } else {
            IDrawingCache<?> cache = danmaku.getDrawingCache();
            if (cache != null) {
                if (cache.hasReferences()) {
                    cache.decreaseReference();
                } else {
                    cache.destroy();
                }
                danmaku.cache = null;
            }
        }
    }

    @Override
    public RenderingState draw(AbsDisplayer displayer) {
        RenderingState result = super.draw(displayer);
        synchronized (mDrawingNotify) {
            mDrawingNotify.notify();
        }
        if (result != null && mCacheManager != null) {
            if (result.totalDanmakuCount - result.lastTotalDanmakuCount < -20) {
                mCacheManager.requestClearTimeout();
                mCacheManager.requestBuild(-mContext.mDanmakuFactory.MAX_DANMAKU_DURATION);
            }
        }
        return result;
    }

    @Override
    public void seek(long mills) {
        super.seek(mills);
        if (mCacheManager == null) {
            start();
        }
        mCacheManager.seek(mills);
    }

    @Override
    public void start() {
        super.start();
        NativeBitmapFactory.loadLibs();
        if (mCacheManager == null) {
            mCacheManager = new CacheManager(this, mMaxCacheSize, MAX_CACHE_SCREEN_SIZE);
            mCacheManager.begin();
            mRenderer.setCacheManager(mCacheManager);
        } else {
            mCacheManager.resume();
        }
    }

    @Override
    public void quit() {
        super.quit();
        reset();
        mRenderer.setCacheManager(null);
        if (mCacheManager != null) {
            mCacheManager.end();
            mCacheManager = null;
        }
        NativeBitmapFactory.releaseLibs();
    }

    @Override
    public void prepare() {
        assert (mParser != null);
        loadDanmakus(mParser);
        mCacheManager.begin();
    }

    @Override
    public void onPlayStateChanged(int state) {
        super.onPlayStateChanged(state);
        if (mCacheManager != null) {
            mCacheManager.onPlayStateChanged(state);
        }
    }

    @Override
    public void requestSync(long fromTimeMills, long toTimeMills, long offsetMills) {
        super.requestSync(fromTimeMills, toTimeMills, offsetMills);
        if (mCacheManager != null) {
            mCacheManager.seek(toTimeMills);
        }
    }


    @Override
    public boolean onDanmakuConfigChanged(DanmakuContext config, DanmakuConfigTag tag,
                                          Object... values) {
        if (super.handleOnDanmakuConfigChanged(config, tag, values)) {
            // do nothing
        } else if (DanmakuConfigTag.SCROLL_SPEED_FACTOR.equals(tag)) {
            mDisp.resetSlopPixel(mContext.scaleTextSize);
            requestClear();
        } else if (tag.isVisibilityRelatedTag()) {
            if (values != null && values.length > 0) {
                if (values[0] != null && ((values[0] instanceof Boolean) == false || ((Boolean) values[0]).booleanValue())) {
                    if (mCacheManager != null) {
                        mCacheManager.requestBuild(0l);
                    }
                }
            }
            requestClear();
        } else if (DanmakuConfigTag.TRANSPARENCY.equals(tag) || DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag) || DanmakuConfigTag.DANMAKU_STYLE.equals(tag)) {
            if (DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag)) {
                mDisp.resetSlopPixel(mContext.scaleTextSize);
            }
            if (mCacheManager != null) {
                mCacheManager.requestClearAll();
                mCacheManager.requestBuild(-mContext.mDanmakuFactory.MAX_DANMAKU_DURATION);
            }
        } else {
            if (mCacheManager != null) {
                mCacheManager.requestClearUnused();
                mCacheManager.requestBuild(0l);
            }
        }

        if (mTaskListener != null && mCacheManager != null) {
            mCacheManager.post(new Runnable() {

                @Override
                public void run() {
                    mTaskListener.onDanmakuConfigChanged();
                }
            });
        }
        return true;
    }
}