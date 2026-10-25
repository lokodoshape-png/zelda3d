package io.github.lokodoshape.zelda3d;

import android.content.Context;
import android.content.pm.PackageManager;

final class BuildConfigVersion {
    private BuildConfigVersion() {}

    static long code(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
