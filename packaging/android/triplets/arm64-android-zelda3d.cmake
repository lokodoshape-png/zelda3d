# arm64-android with shared libraries. The engine (libultraship), the launcher (libmain) and the MM
# core are separate shared objects, as on desktop, so SDL and the other dependencies must be shared
# too: a static copy inside each .so would give every one of them its own SDL state.
set(VCPKG_TARGET_ARCHITECTURE arm64)
set(VCPKG_CRT_LINKAGE dynamic)
set(VCPKG_LIBRARY_LINKAGE dynamic)
set(VCPKG_CMAKE_SYSTEM_NAME Android)
set(VCPKG_MAKE_BUILD_TRIPLET "--host=aarch64-linux-android")
set(VCPKG_CMAKE_CONFIGURE_OPTIONS -DANDROID_ABI=arm64-v8a -DANDROID_STL=c++_shared)
