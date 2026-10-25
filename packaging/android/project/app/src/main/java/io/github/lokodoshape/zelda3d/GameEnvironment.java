package io.github.lokodoshape.zelda3d;

import android.content.Context;
import android.content.res.AssetManager;
import android.system.ErrnoException;
import android.system.Os;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** File locations the native launcher reads from the environment, and the data they point at. */
final class GameEnvironment {
    private GameEnvironment() {}

    static File romDir(Context context) {
        return new File(context.getFilesDir(), "roms");
    }

    static File mmRom(Context context) {
        return new File(romDir(context), "mm.z64");
    }

    static File mm3dRom(Context context) {
        return new File(romDir(context), "mm3d.3ds");
    }

    static File coreDataDir(Context context) {
        return new File(context.getFilesDir(), "mm");
    }

    static boolean romsReady(Context context) {
        return mmRom(context).length() > 0 && mm3dRom(context).length() > 0;
    }

    /** Exports the paths the launcher and the ROM setup read; see zelda3d_main.cpp and rom_identity.cpp. */
    static void export(Context context) throws ErrnoException {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        Os.setenv("ZELDA3D_GAME", "mm", true);
        Os.setenv("ZELDA3D_CORE_MM", nativeDir + "/libmm_core.so", true);
        Os.setenv("ZELDA3D_CORE_DATA_DIR", coreDataDir(context).getAbsolutePath(), true);
        Os.setenv("ZELDA3D_MM_ROM", mmRom(context).getAbsolutePath(), true);
        Os.setenv("ZELDA3D_MM3D_ROM", mm3dRom(context).getAbsolutePath(), true);
    }

    /**
     * Unpacks the core's read-only data (asset xmls, RmlUi documents, 2ship.o2r) from the APK. Native
     * code reads them as plain files, which APK assets are not. Redone once per app version.
     */
    static void unpackCoreData(Context context) throws IOException {
        File target = coreDataDir(context);
        File stamp = new File(target, ".version");
        String version = String.valueOf(BuildConfigVersion.code(context));
        if (stamp.isFile() && version.equals(readSmall(stamp))) {
            return;
        }
        deleteRecursively(target);
        copyAssetTree(context.getAssets(), "mm", target);
        try (OutputStream out = new FileOutputStream(stamp)) {
            out.write(version.getBytes());
        }
    }

    private static void copyAssetTree(AssetManager assets, String path, File target) throws IOException {
        String[] children = assets.list(path);
        if (children == null || children.length == 0) {
            target.getParentFile().mkdirs();
            try (InputStream in = assets.open(path); OutputStream out = new FileOutputStream(target)) {
                copy(in, out);
            }
            return;
        }
        target.mkdirs();
        for (String child : children) {
            copyAssetTree(assets, path + "/" + child, new File(target, child));
        }
    }

    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1 << 20];
        int read;
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }

    private static String readSmall(File file) {
        try (InputStream in = new java.io.FileInputStream(file)) {
            byte[] data = new byte[64];
            int n = in.read(data);
            return n > 0 ? new String(data, 0, n) : "";
        } catch (IOException e) {
            return "";
        }
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }
}
