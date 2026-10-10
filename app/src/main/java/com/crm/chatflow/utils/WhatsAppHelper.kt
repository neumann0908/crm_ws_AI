package com.crm.chatflow.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppHelper {
    fun sendMessage(context: Context, phone: String, message: String) {
        try {
            // Limpiar el número de teléfono de espacios o caracteres extraños
            val cleanPhone = phone.replace(Regex("[^0-9]"), "")
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
