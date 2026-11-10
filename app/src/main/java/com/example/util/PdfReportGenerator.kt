package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Creates readable, paginated A4 PDF reports with properly shaped Bengali/Unicode text. */
object PdfReportGenerator {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT = 40f
    private const val RIGHT = 555f
    private const val CONTENT_WIDTH = RIGHT - LEFT
    private const val TOP = 132f
    private const val BOTTOM = 790f

    fun export(
        context: Context,
        shopName: String,
        title: String,
        summary: List<Pair<String, String>>,
        rows: List<String>
    ) {
        val document = PdfDocument()
        val ink = android.graphics.Color.rgb(30, 41, 59)
        val muted = android.graphics.Color.rgb(100, 116, 139)
        val accentColor = android.graphics.Color.rgb(2, 132, 199)
        val lineColor = android.graphics.Color.rgb(226, 232, 240)
        val cardColor = android.graphics.Color.rgb(248, 250, 252)

        val body = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ink
            textSize = 9.5f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        val small = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = muted
            textSize = 8f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        val bold = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ink
            textSize = 10f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
        val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ink
            textSize = 12f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }
        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = cardColor
            style = Paint.Style.FILL
        }
        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            strokeWidth = 2.5f
        }

        fun layout(text: String, paint: TextPaint, width: Int): StaticLayout {
            val safe = text.ifBlank { "—" }
            return StaticLayout.Builder.obtain(safe, 0, safe.length, paint, width.coerceAtLeast(20))
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false)
                .setLineSpacing(2f, 1f)
                .build()
        }

        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null
        var y = TOP

        fun startPage() {
            pageNumber++
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(info)
            canvas = page!!.canvas
            val c = canvas!!
            c.drawColor(android.graphics.Color.WHITE)

            val shopLayout = layout(shopName.ifBlank { "Apple Area Shop" }, sectionPaint, CONTENT_WIDTH.toInt())
            c.save()
            c.translate(LEFT, 28f)
            shopLayout.draw(c)
            c.restore()

            val titlePaint = TextPaint(sectionPaint).apply { textSize = 15f }
            val titleLayout = layout(title, titlePaint, CONTENT_WIDTH.toInt())
            c.save()
            c.translate(LEFT, 51f)
            titleLayout.draw(c)
            c.restore()

            c.drawLine(LEFT, 91f, RIGHT, 91f, accentPaint)
            val generated = "Generated: " + SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            val dateLayout = layout(generated, small, CONTENT_WIDTH.toInt())
            c.save()
            c.translate(LEFT, 101f)
            dateLayout.draw(c)
            c.restore()
            y = TOP
        }

        fun finishPage() {
            val c = canvas ?: return
            val footer = TextPaint(small).apply { textSize = 8f }
            val footerText = "Apple Area Shop  •  Page $pageNumber"
            val footerLayout = layout(footerText, footer, CONTENT_WIDTH.toInt())
            c.save()
            c.translate(LEFT, 812f)
            footerLayout.draw(c)
            c.restore()
            document.finishPage(page!!)
            page = null
            canvas = null
        }

        fun ensureSpace(height: Float) {
            if (y + height > BOTTOM) {
                finishPage()
                startPage()
            }
        }

        fun drawSectionHeading(label: String) {
            ensureSpace(30f)
            val l = layout(label, sectionPaint, CONTENT_WIDTH.toInt())
            canvas!!.save()
            canvas!!.translate(LEFT, y)
            l.draw(canvas!!)
            canvas!!.restore()
            y += l.height + 9f
        }

        try {
            startPage()

            if (summary.isNotEmpty()) {
                drawSectionHeading("SUMMARY")
                val labelWidth = 330
                val valueWidth = CONTENT_WIDTH.toInt() - labelWidth - 24
                summary.forEachIndexed { index, (label, value) ->
                    val leftLayout = layout(label, body, labelWidth)
                    val rightLayout = layout(value, bold, valueWidth)
                    val rowHeight = maxOf(leftLayout.height, rightLayout.height, 18) + 12
                    ensureSpace(rowHeight.toFloat())
                    val top = y
                    if (index % 2 == 0) {
                        canvas!!.drawRoundRect(RectF(LEFT, top - 3f, RIGHT, top + rowHeight - 3f), 5f, 5f, fillPaint)
                    }
                    canvas!!.save()
                    canvas!!.translate(LEFT + 9f, top + 3f)
                    leftLayout.draw(canvas!!)
                    canvas!!.restore()
                    canvas!!.save()
                    canvas!!.translate(LEFT + labelWidth + 15f, top + 3f)
                    rightLayout.draw(canvas!!)
                    canvas!!.restore()
                    y += rowHeight
                }
                y += 14f
            }

            if (rows.isNotEmpty()) {
                drawSectionHeading("DETAILS  •  ${rows.size} RECORDS")
                rows.forEachIndexed { index, row ->
                    // Each source record becomes its own wrapped card instead of one long,
                    // unstructured line. Pipe-separated fields remain visible and readable.
                    val parts = row.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                    val recordText = parts.joinToString("     •     ").ifBlank { row }
                    val textLayout = layout("${index + 1}.  $recordText", body, (CONTENT_WIDTH - 20f).toInt())
                    val cardHeight = textLayout.height + 16f
                    ensureSpace(cardHeight)
                    val top = y
                    canvas!!.drawRoundRect(RectF(LEFT, top - 3f, RIGHT, top + cardHeight - 3f), 5f, 5f, fillPaint)
                    canvas!!.drawRoundRect(RectF(LEFT, top - 3f, RIGHT, top + cardHeight - 3f), 5f, 5f, rulePaint)
                    canvas!!.save()
                    canvas!!.translate(LEFT + 10f, top + 5f)
                    textLayout.draw(canvas!!)
                    canvas!!.restore()
                    y += cardHeight + 5f
                }
            }

            if (summary.isEmpty() && rows.isEmpty()) {
                val emptyLayout = layout("No records available.", body, CONTENT_WIDTH.toInt())
                canvas!!.save()
                canvas!!.translate(LEFT, y)
                emptyLayout.draw(canvas!!)
                canvas!!.restore()
            }
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
