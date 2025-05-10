package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class TransactionRequest(
    val toolId: Int,
    val email: String
)

data class Transaction(
    val transaction_id: Int,
    val returnImage: String?,
    val transaction_type: String?,
    val reason: String?,
    val condition_before: String?,
    val condition_after: String?,
    val borrow_date: String,
    val due_date: String?,
    val return_date: String?,
    val status: String,
    val created_at: String,
    val updated_at: String?
)

data class TransactionResponse(
    val transaction: Transaction
)

data class MyTransactionItem(
    val transaction_id: Int,
    val user_id: Int,
    val user_firstName: String,
    val user_lastName: String,
    val email: String,
    val tool_id: Int,
    val tool_name: String,
    val borrow_date: String,
    val due_date: String?,
    val return_date: String?,
    val transaction_type: String?,
    val status: String
)

data class MyTransactionsResponse(
    val transactions: List<MyTransactionItem>
)

interface TransactionService {
    @POST("transaction/addTransaction")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body request: TransactionRequest
    ): Response<TransactionResponse>

    @GET("transaction/getTransaction/{transactionId}")
    suspend fun getTransaction(
        @Header("Authorization") token: String,
        @Path("transactionId") transactionId: Int
    ): Response<TransactionResponse>

    @GET("transaction/myTransactions/{email}")
    suspend fun getMyTransactions(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<MyTransactionsResponse>

    companion object {
        fun create(): TransactionService {
            return ToolTrackApi.retrofitInstance().create(TransactionService::class.java)
        }
    }
}
