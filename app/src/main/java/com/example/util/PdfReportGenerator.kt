package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Creates paginated A4 PDF reports and saves them under Downloads/Apple Area Shop. */
object PdfReportGenerator {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT = 40f
    private const val RIGHT = 555f
    private const val BOTTOM = 790f

    fun export(
        context: Context,
        shopName: String,
        title: String,
        summary: List<Pair<String, String>>,
        rows: List<String>
    ) {
        val document = PdfDocument()
        val body = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(30, 41, 59)
            textSize = 10f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        val heading = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(15, 23, 42)
            textSize = 17f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }
        val accent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(2, 132, 199)
            strokeWidth = 2f
        }
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null
        var y = 0f

        fun startPage() {
            pageNumber++
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(info)
            canvas = page!!.canvas
            canvas!!.drawColor(android.graphics.Color.WHITE)
            canvas!!.drawText(shopName.take(70), LEFT, 42f, heading)
            heading.textSize = 14f
            canvas!!.drawText(title.take(90), LEFT, 68f, heading)
            heading.textSize = 17f
            canvas!!.drawLine(LEFT, 82f, RIGHT, 82f, accent)
            body.textSize = 8f
            canvas!!.drawText(
                "Generated: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}",
                LEFT, 99f, body
            )
            body.textSize = 10f
            y = 124f
        }

        fun finishPage() {
            val c = canvas ?: return
            body.textSize = 8f
            c.drawText("Page $pageNumber", RIGHT - 42f, 820f, body)
            document.finishPage(page!!)
            page = null
            canvas = null
        }

        fun drawWrapped(text: String, paint: Paint) {
            val words = text.ifBlank { "—" }.split(Regex("\\s+"))
            var line = ""
            for (word in words) {
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (line.isNotEmpty() && paint.measureText(candidate) > RIGHT - LEFT) {
                    if (y > BOTTOM) { finishPage(); startPage() }
                    canvas!!.drawText(line, LEFT, y, paint)
                    y += 15f
                    line = word
                } else line = candidate
            }
            if (line.isNotEmpty()) {
                if (y > BOTTOM) { finishPage(); startPage() }
                canvas!!.drawText(line, LEFT, y, paint)
                y += 15f
            }
        }

        try {
            startPage()
            if (summary.isNotEmpty()) {
                heading.textSize = 11f
                canvas!!.drawText("SUMMARY", LEFT, y, heading)
                heading.textSize = 17f
                y += 20f
                summary.forEach { (label, value) -> drawWrapped("$label: $value", body) }
                y += 8f
            }
            if (rows.isNotEmpty()) {
                if (y > BOTTOM - 20f) { finishPage(); startPage() }
                heading.textSize = 11f
                canvas!!.drawText("DETAILS (${rows.size})", LEFT, y, heading)
                heading.textSize = 17f
                y += 20f
                rows.forEachIndexed { index, row ->
                    if (y > BOTTOM - 20f) { finishPage(); startPage() }
                    body.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                    drawWrapped("${index + 1}. $row", body)
                    body.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    y += 3f
                }
            }
            if (summary.isEmpty() && rows.isEmpty()) drawWrapped("No records available.", body)
            finishPage()

            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val safeName = title.replace(Regex("[^A-Za-z0-9_-]"), "_").take(35).ifBlank { "Report" }
            val fileName = "${safeName}_$stamp.pdf"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Apple Area Shop")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Could not create PDF file")
                context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
                    ?: throw IllegalStateException("Could not open PDF output")
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            } else {
                val base = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                val folder = File(base, "Apple Area Shop").apply { mkdirs() }
                FileOutputStream(File(folder, fileName)).use { document.writeTo(it) }
            }
            Toast.makeText(context, "PDF Downloads/Apple Area Shop ফোল্ডারে সেভ হয়েছে", Toast.LENGTH_LONG).show()
        } catch (error: Exception) {
            Toast.makeText(context, "PDF তৈরি করা যায়নি: ${error.localizedMessage ?: "Unknown error"}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }
}
