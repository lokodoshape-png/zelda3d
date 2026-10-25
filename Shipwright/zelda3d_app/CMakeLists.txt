# The launcher executable. Links libultraship only -- for the core ABI header and so the engine is
# already mapped when a core loads -- and never a game target: the whole point is that game code
# arrives by dlopen(RTLD_LOCAL), not by linking. A target_link_libraries line naming soh_lib or
# mm_lib here would defeat the design silently, by making one game's symbols global again.
if (CMAKE_SYSTEM_NAME STREQUAL "Android")
    # SDL's Android activity loads the program as libmain.so and calls SDL_main in it.
    add_library(zelda3d_app SHARED zelda3d_main.cpp)
else()
    add_executable(zelda3d_app zelda3d_main.cpp)
endif()

if (CMAKE_SYSTEM_NAME STREQUAL "Android")
    set_target_properties(zelda3d_app PROPERTIES OUTPUT_NAME "main")
else()
    set_target_properties(zelda3d_app PROPERTIES OUTPUT_NAME "zelda3d")
endif()

target_link_libraries(zelda3d_app PRIVATE libultraship zelda3d_shared ${CMAKE_DL_LIBS})

# The cores are found relative to this binary (../soh/, ../mm/), so it has to be built one level
# above them rather than into the top of the build tree.
set_target_properties(zelda3d_app PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/zelda3d)

# Not a link dependency, but building the launcher alone and finding no cores is a useless artifact.
# Android ships the MM core only.
if (CMAKE_SYSTEM_NAME STREQUAL "Android")
    add_dependencies(zelda3d_app mm_core)
else()
    add_dependencies(zelda3d_app soh_core mm_core)
endif()
