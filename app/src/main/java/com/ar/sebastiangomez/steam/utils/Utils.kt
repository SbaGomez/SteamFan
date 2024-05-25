package com.ar.sebastiangomez.steam.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.ar.sebastiangomez.steam.model.PcRequirement
import com.ar.sebastiangomez.steam.model.PcRequirements

class Utils {
    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun parsePcRequirements(pcRequirementsString: String): PcRequirements? {
        // Elimina las etiquetas HTML y caracteres innecesarios
        val cleanString = pcRequirementsString
            .replace("<strong>", " ")
            .replace("<br><ul class=\"bb_ul\"><li>Requires a 64-bit processor and operating system<br></li><li>", " ")
            .replace("OS *:", "OS:")
            .replace("minimum=", "")
            .replace("recommended=", "")
            .replace(Regex("<.*?>"), "") // Elimina etiquetas HTML
            .replace(",  Recommended:Requires a 64-bit processor and operating system", " Recommended: OS: Requires a 64-bit processor and operating system")
            .replace(Regex("Sound Card:"), "Sound:") // Elimina etiquetas HTML
            .replace(Regex(" space,"), "") // Elimina etiquetas HTML
            .replace("\n", "") // Elimina saltos de línea

        // Divide la cadena en requisitos mínimos y recomendados
        val requirementParts = cleanString.split("Recommended:")

        // Función auxiliar para extraer un requisito específico de acuerdo a su etiqueta
        fun extractRequirement(requirementString: String, label: String): String {
            val regex =
                Regex("$label\\s*:?\\s*((?:(?!$label\\s*:?)[^:])*)", RegexOption.IGNORE_CASE)
            val matchResult = regex.find(requirementString)
            val content = matchResult?.groupValues?.getOrNull(1)?.trim() ?: ""

            // Eliminar la última palabra del contenido
            val words = content.split(" ")

            return if (words.size > 1) words.dropLast(1).joinToString(" ") else content
        }

        // Función auxiliar para crear un objeto PcRequirement a partir de una cadena de requisito
        fun createPcRequirement(requirementString: String): PcRequirement? {
            val os = extractRequirement(requirementString, "OS:")
            val processor = extractRequirement(requirementString, "Processor:")
            val memory = extractRequirement(requirementString, "Memory:")
            val graphics = extractRequirement(requirementString, "Graphics:")
            val directx = extractRequirement(requirementString, "DirectX:")
            val soundcard = extractRequirement(requirementString, "Sound:")
            val network = extractRequirement(requirementString, "Network:")
            val storage = extractRequirement(requirementString, "Storage:")

            return if (os.isNotEmpty() || processor.isNotEmpty() || memory.isNotEmpty() || graphics.isNotEmpty() ||
                directx.isNotEmpty() || soundcard.isNotEmpty() || network.isNotEmpty() || storage.isNotEmpty()) {
                PcRequirement(
                    os = os,
                    processor = processor,
                    memory = memory,
                    graphics = graphics,
                    directx = directx,
                    soundcard = soundcard,
                    network = network,
                    storage = storage
                )
            } else {
                null
            }
        }

        // Obtén los requisitos mínimos si están presentes
        val minimumRequirement = requirementParts.getOrNull(0)?.let { minimum ->
            createPcRequirement(minimum)
        }

        // Obtén los requisitos recomendados si están presentes
        val recommendedRequirement = if (requirementParts.size > 1)
        {
            requirementParts.getOrNull(1)?.let { recommended ->
                createPcRequirement(recommended)
            }
        } else {
            // Si no hay requisitos recomendados, utiliza los mismos requisitos mínimos
            minimumRequirement
        }

        if (minimumRequirement != null || recommendedRequirement != null) {
            // Crea el objeto PcRequirements con los requisitos mínimos y recomendados
            return PcRequirements(minimumRequirement, recommendedRequirement)
        }
        return null
    }
}