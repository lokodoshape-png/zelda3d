# Android dependencies come from vcpkg (arm64-android triplet), the same way Windows uses
# windows-vcpkg.cmake. The old FetchContent path here pulled SDL2, which this tree no longer uses:
# the engine, ImGui backends and RmlUi integration are all SDL3. common.cmake and src/CMakeLists.txt
# find_package() everything they need, so nothing extra is required here.
