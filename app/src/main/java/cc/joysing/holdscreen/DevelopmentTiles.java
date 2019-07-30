package cc.joysing.holdscreen;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.PowerManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

public abstract class DevelopmentTiles extends TileService {
    private static final String TAG = "DevelopmentTiles";
    public PowerManager.WakeLock mWakeLock;
    protected abstract boolean isEnabled();

    protected abstract void setIsEnabled(boolean isEnabled);

    @Override
    public void onStartListening() {
        super.onStartListening();
        refresh();

//        PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯有可能是关闭的。
//        SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
//        SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
//        FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
            mWakeLock.setReferenceCounted(false);
        }
    }

    public void refresh() {
        Log.i(TAG,"DevelopmentTiles refresh");
        final int state;
        state = isEnabled() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
        getQsTile().setState(state);
        getQsTile().updateTile();
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onClick() {

        Intent intent1 = new Intent();
        ComponentName componentName = new ComponentName("cc.joysing.holdscreen", "cc.joysing.holdscreen.MainActivity");
        intent1.setComponent(componentName);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }else {
            mWakeLock.acquire();
        }
        Log.i(TAG,"DevelopmentTiles onClick");
//        setIsEnabled(getQsTile().getState() == Tile.STATE_INACTIVE);
        setIsEnabled(mWakeLock.isHeld());
        refresh();
    }

    /**
     * Tile to control the "Show layout bounds" developer setting
     */
    public static class ShowLayout extends DevelopmentTiles {
        private boolean enable =false;

        @Override
        protected boolean isEnabled() {
            return enable;
        }

        @Override
        protected void setIsEnabled(boolean isEnabled) {
            enable = isEnabled;
        }

        @Override
        public void refresh() {
            Log.i(TAG,"ShowLayout refresh");
            super.refresh();
        }
    }
}