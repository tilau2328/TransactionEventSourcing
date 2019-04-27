package ams.cqrs.transactions.query.transaction

import ams.cqrs.transactions.core.transaction.MoneyTransferCanceled
import ams.cqrs.transactions.core.transaction.MoneyTransferCompleted
import ams.cqrs.transactions.core.transaction.MoneyTransferRequested
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TransactionProjector {
    @Autowired
    lateinit var repository: TransactionRepository

    @EventHandler
    fun handle(event: MoneyTransferRequested) {
        val transaction = Transaction(event.transactionId, event.amount, event.fromId, event.toId)
        repository.save(transaction)
    }

    @EventHandler
    fun handle(event: MoneyTransferCompleted) {
        val transaction = repository.findById(event.transactionId)
        if(!transaction.isPresent) return
        transaction.get().state = TransactionState.COMPLETED
        repository.save(transaction.get())
    }

    @EventHandler
    fun handle(event: MoneyTransferCanceled) {
        val transaction = repository.findById(event.transactionId)
        if(!transaction.isPresent) return
        transaction.get().state = TransactionState.CANCELED
        repository.save(transaction.get())
    }
}