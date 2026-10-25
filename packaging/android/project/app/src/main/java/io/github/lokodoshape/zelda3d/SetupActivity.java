package io.github.lokodoshape.zelda3d;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * First screen: asks for the player's own Majora's Mask ROMs through Android's document picker, copies
 * them into app-private storage, unpacks the core data, then starts the game. Nothing here is
 * bundled with the app: both ROMs come from the player.
 */
public class SetupActivity extends Activity {
    private static final int PICK_MM = 1;
    private static final int PICK_MM3D = 2;

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = new TextView(this);
        status.setPadding(48, 48, 48, 48);
        status.setTextSize(18);
        setContentView(status);
        next();
    }

    private void next() {
        if (GameEnvironment.mmRom(this).length() == 0) {
            ask(PICK_MM, "Selecione a sua ROM do Majora's Mask de N64 (.z64, .n64 ou .v64).");
        } else if (GameEnvironment.mm3dRom(this).length() == 0) {
            ask(PICK_MM3D, "Selecione a sua ROM do Majora's Mask 3D (.3ds descriptografada).");
        } else {
            start();
        }
    }

    private void ask(int request, String message) {
        status.setText(message);
        new AlertDialog.Builder(this)
                .setTitle("Zelda3D: configuração inicial")
                .setMessage(message + "\n\nNenhum arquivo do jogo vem com o app. A ROM é copiada só para a pasta interna do app.")
                .setCancelable(false)
                .setPositiveButton("Escolher arquivo", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, request);
                })
                .setNegativeButton("Sair", (dialog, which) -> finish())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            next();
            return;
        }
        File target = requestCode == PICK_MM ? GameEnvironment.mmRom(this) : GameEnvironment.mm3dRom(this);
        Uri uri = data.getData();
        status.setText("Copiando a ROM, aguarde...");
        new Thread(() -> {
            String error = copyRom(uri, target);
            runOnUiThread(() -> {
                if (error != null) {
                    new AlertDialog.Builder(this)
                            .setTitle("Não foi possível copiar a ROM")
                            .setMessage(error)
                            .setPositiveButton("OK", (d, w) -> next())
                            .show();
                } else {
                    next();
                }
            });
        }).start();
    }

    private String copyRom(Uri uri, File target) {
        File partial = new File(target.getPath() + ".part");
        target.getParentFile().mkdirs();
        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(partial)) {
            if (in == null) {
                return "O arquivo escolhido não pôde ser aberto.";
            }
            GameEnvironment.copy(in, out);
        } catch (Exception e) {
            partial.delete();
            return e.toString();
        }
        if (!partial.renameTo(target)) {
            partial.delete();
            return "Não foi possível salvar a ROM no armazenamento do app.";
        }
        return null;
    }

    private void start() {
        status.setText("Preparando os arquivos do jogo...");
        new Thread(() -> {
            String error = null;
            try {
                GameEnvironment.unpackCoreData(this);
                GameEnvironment.export(this);
            } catch (Exception e) {
                error = e.toString();
            }
            final String failure = error;
            runOnUiThread(() -> {
                if (failure != null) {
                    status.setText("Erro ao preparar os arquivos: " + failure);
                    return;
                }
                startActivity(new Intent(this, GameActivity.class));
                finish();
            });
        }).start();
    }
}
