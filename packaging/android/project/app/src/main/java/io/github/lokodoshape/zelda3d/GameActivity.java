package io.github.lokodoshape.zelda3d;

import android.os.Bundle;
import android.util.Log;

import org.libsdl.app.SDLActivity;

/** Runs the native launcher (libmain.so), which loads libmm_core.so. */
public class GameActivity extends SDLActivity {
    @Override
    protected String[] getLibraries() {
        return new String[] { "SDL3", "main" };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Exported again here because Android may restart this activity in a fresh process.
        try {
            GameEnvironment.export(this);
        } catch (Exception e) {
            Log.e("Zelda3D", "Could not export the game environment", e);
        }
        super.onCreate(savedInstanceState);
    }
}
