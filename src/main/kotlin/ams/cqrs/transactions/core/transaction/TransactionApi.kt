package ams.cqrs.transactions.core.transaction

import org.axonframework.commandhandling.TargetAggregateIdentifier

// Commands
data class RequestMoneyTransaction(val transactionId: String, val fromId: String, val toId: String, val amount: Int)
data class CompleteMoneyTransfer(@TargetAggregateIdentifier val transactionId: String)
data class CancelMoneyTransfer(@TargetAggregateIdentifier val transactionId: String)

// Events
data class MoneyTransferRequested(val transactionId: String, val fromId: String, val toId: String, val amount: Int)
data class MoneyTransferCompleted(val transactionId: String)
data class MoneyTransferCanceled(val transactionId: String)
