package ams.cqrs.transactions.saga

import ams.cqrs.transactions.core.account.*
import ams.cqrs.transactions.core.transaction.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
class TransactionSaga {
    @Autowired
    @Transient
    lateinit var commandGateway: CommandGateway

    private lateinit var targetAccount: String

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyTransferRequested) {
        this.targetAccount = event.toId
        println("Money transfer requested from=${event.fromId} to=${event.toId} amount=${event.amount}")
        try {
            commandGateway.sendAndWait<WithdrawMoney>(WithdrawMoney(event.fromId, event.transactionId, event.amount))
        } catch (e: Exception) {
            println(e)
            commandGateway.send<CancelMoneyTransfer>(CancelMoneyTransfer(event.transactionId))
        }
    }

    @SagaEventHandler(associationProperty = "txId", keyName = "transactionId")
    fun on(event: MoneyWithdrawn) {
        commandGateway.send<DepositMoney>(DepositMoney(this.targetAccount, event.txId, event.amount))
    }

    @SagaEventHandler(associationProperty = "txId", keyName = "transactionId")
    fun on(event: MoneyDeposited) {
        commandGateway.send<CompleteMoneyTransfer>(CompleteMoneyTransfer(event.txId))
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyTransferCompleted) {

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    fun on(event: MoneyTransferCanceled) {

    }
}