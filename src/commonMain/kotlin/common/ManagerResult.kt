package common

sealed class ManagerResult {
    object Success : ManagerResult()
    object Cancel : ManagerResult()
    data class Failure(val message: String) : ManagerResult()
}