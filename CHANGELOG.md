# Changelog do Zelda3D (fork lokodoshape-png)

Mudanças deste fork em relação ao Zelda3D original. O trabalho fica na branch `android-arm64`.
As builds de Android saem como prerelease na página [Releases](https://github.com/lokodoshape-png/zelda3d/releases).

O jogo não vem com ROM nem com assets da Nintendo. Você escolhe as suas próprias ROMs no aparelho.

O port principal de Majora's Mask 3D continua no fork
[2ship2harkinian-Android](https://github.com/lokodoshape-png/2ship2harkinian-Android/blob/mm3d-4.0/CHANGELOG.md).

## 2026-10-25: Android ARM64 (lado MM)
- **Primeira compilação cruzada do núcleo de Majora's Mask para Android ARM64.**
  - O `android.cmake` passa a ser incluído antes do `common.cmake`.
  - O SDL3 é encontrado antes de o libultraship ligar o ImGui a ele.
  - Os erros de compilação restantes do núcleo MM foram corrigidos.
  - O GLES é ligado para o `glReadPixels` do frame dump do libultraship.
- **APK do lado MM do Zelda3D.**
  - O launcher é gerado como `libmain.so` para a activity do SDL, abre o MM por padrão e lê a pasta de dados de `ZELDA3D_CORE_DATA_DIR`.
  - No Android a configuração só pede as ROMs do MM. A tela de configuração em Java escolhe as ROMs pelo seletor de documentos do sistema.
  - Projeto Gradle com as activities de configuração e de jogo em `packaging/android`.
  - Triplet do vcpkg com bibliotecas compartilhadas, para haver uma única instância do SDL entre os `.so`.
- **CI** (`android-arm64.yml`): compila as bibliotecas nativas, o `2ship.o2r` sem dados da ROM e o APK, e publica como prerelease.
- **Correções da build:**
  - o arquivo do OoT não é mais gerado na compilação cruzada;
  - a pasta de dados é encontrada também no caminho de execução em sequência;
  - o `versionCode` é lido sem o problema de precedência do cast no Groovy.
