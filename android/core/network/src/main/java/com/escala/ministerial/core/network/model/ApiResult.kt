package com.escala.ministerial.core.network.model

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Error -> this
    is ApiResult.Loading -> this
}

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(call())
} catch (e: retrofit2.HttpException) {
    ApiResult.Error(
        message = e.response()?.errorBody()?.string() ?: "Erro HTTP ${e.code()}",
        code = e.code(),
    )
} catch (e: java.io.IOException) {
    ApiResult.Error("Sem conexão com o servidor. Verifique sua rede.")
} catch (e: Exception) {
    ApiResult.Error(e.message ?: "Erro desconhecido")
}
