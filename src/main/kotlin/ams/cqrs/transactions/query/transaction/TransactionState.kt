package ams.cqrs.transactions.query.transaction

enum class TransactionState {
    CREATED, COMPLETED, CANCELED
}