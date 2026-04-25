plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

// android {} não está disponível diretamente aqui pois o AGP ainda não foi aplicado.
// Usamos pluginManager.withPlugin para aguardar o plugin correto ser aplicado.
pluginManager.withPlugin("com.android.base") {
    @Suppress("UNCHECKED_CAST")
    (extensions.getByName("android") as com.android.build.api.dsl.CommonExtension<*, *, *, *, *, *>)
        .buildFeatures {
            compose = true
        }
}
