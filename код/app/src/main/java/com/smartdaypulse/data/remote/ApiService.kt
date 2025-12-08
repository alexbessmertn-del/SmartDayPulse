package com.smartdaypulse.data.remote

import com.smartdaypulse.data.remote.dto.ScheduleRequest
import com.smartdaypulse.data.remote.dto.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("health")
    suspend fun healthCheck(): Response<Unit>

    @POST("api/schedule")
    suspend fun scheduleTask(@Body request: ScheduleRequest): Response<ScheduleResponse>
}