package com.example.torii.model

data class Video(
    val title: String,
    val duration: String,
    val datePosted: String,
    val thumbnailUrl: String,
    val videoId: String,
    val subtitles: List<SubtitleLine>
)

data class SubtitleLine(
    val timeStart: Float, // Thời gian bắt đầu (giây)
    val timeEnd: Float,   // Thời gian kết thúc (giây)
    val jpText: String,   // Phụ đề tiếng Nhật
    val enText: String,    // Phụ đề tiếng Anh
    val isHighlighted: Boolean = false // Trạng thái highlight
)
