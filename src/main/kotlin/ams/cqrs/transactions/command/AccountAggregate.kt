package ams.cqrs.transactions.command

import ams.cqrs.transactions.core.account.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventhandling.EventHandler
import org.axonframework.spring.stereotype.Aggregate
import javax.persistence.Basic
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Aggregate
class AccountAggregate() {
    @Id
    @AggregateIdentifier
    private lateinit var accountId: String

    @Basic
    private var overdraftLimit: Int = 1000

    @Basic
    private var balance: Int = 0

    @CommandHandler
    constructor(command: CreateAccount): this() {
        AggregateLifecycle.apply(AccountCreated(command.accountId, command.txId, command.overdraftLimit, balance))
    }

    @CommandHandler
    fun handle(command: WithdrawMoney) {
        if(balance + overdraftLimit >= command.amount)
            AggregateLifecycle.apply(MoneyWithdrawn(command.accountId, command.txId, command.amount, balance - command.amount))
        else
            throw OverdraftLimitExceeded()
    }

    @CommandHandler
    fun handle(command: DepositMoney) {
        AggregateLifecycle.apply(MoneyDeposited(command.accountId, command.txId, command.amount, balance + command.amount))
    }

    @EventHandler
    fun on(event: AccountCreated) {
        accountId = event.accountId
        overdraftLimit = event.overdraftLimit
    }

    @EventHandler
    fun on(event: MoneyWithdrawn) {
        balance = event.balance
    }

    @EventHandler
    fun on(event: MoneyDeposited) {
        balance = event.balance
    }
}