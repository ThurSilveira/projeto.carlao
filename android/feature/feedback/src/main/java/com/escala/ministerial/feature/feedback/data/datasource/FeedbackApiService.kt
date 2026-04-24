package com.escala.ministerial.feature.feedback.data.datasource

import com.escala.ministerial.feature.feedback.data.dto.FeedbackDto
import com.escala.ministerial.feature.feedback.data.dto.ResponderFeedbackRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FeedbackApiService {
    @GET("feedbacks")
    suspend fun getAll(): List<FeedbackDto>

    @POST("feedbacks")
    suspend fun create(@Body dto: FeedbackDto): FeedbackDto

    @PUT("feedbacks/{id}/responder")
    suspend fun responder(@Path("id") id: Long, @Body request: ResponderFeedbackRequest): FeedbackDto
}
