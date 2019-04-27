package ams.cqrs.transactions.core.account

import org.axonframework.commandhandling.TargetAggregateIdentifier

// Commands
data class CreateAccount(val accountId: String, val txId: String, val overdraftLimit: Int)
data class DepositMoney(@TargetAggregateIdentifier val accountId: String, val txId: String, val amount: Int)
data class WithdrawMoney(@TargetAggregateIdentifier val accountId: String, val txId: String, val amount: Int)

// Events
abstract class BalanceUpdatedEvent(val accountId: String, val txId: String, val balance: Int)
class AccountCreated(accountId: String, txId: String, val overdraftLimit: Int, balance: Int): BalanceUpdatedEvent(accountId, txId, balance)
class MoneyDeposited(accountId: String, txId: String, val amount: Int, balance: Int): BalanceUpdatedEvent(accountId, txId, balance)
class MoneyWithdrawn(accountId: String, txId: String, val amount: Int, balance: Int): BalanceUpdatedEvent(accountId, txId, balance)