package ams.cqrs.transactions.command

import ams.cqrs.transactions.core.transaction.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Aggregate
class TransactionAggregate() {
    @Id
    @AggregateIdentifier
    lateinit var transactionId: String

    @CommandHandler
    constructor(cmd: RequestMoneyTransaction): this() {
        AggregateLifecycle.apply(MoneyTransferRequested(cmd.transactionId, cmd.fromId, cmd.toId, cmd.amount))
    }

    @CommandHandler
    fun handle(cmd: CompleteMoneyTransfer) {
        // TODO: verify state
        AggregateLifecycle.apply(MoneyTransferCompleted(cmd.transactionId))
    }

    @CommandHandler
    fun handle(cmd: CancelMoneyTransfer) {
        // TODO: verify state
        AggregateLifecycle.apply(MoneyTransferCanceled(cmd.transactionId))
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferRequested) {
        this.transactionId = event.transactionId
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferCompleted) {
        AggregateLifecycle.markDeleted()
    }

    @EventSourcingHandler
    fun on(event: MoneyTransferCanceled) {
        AggregateLifecycle.markDeleted()
    }
}