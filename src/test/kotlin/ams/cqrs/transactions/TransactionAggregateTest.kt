package ams.cqrs.transactions

import ams.cqrs.transactions.command.TransactionAggregate
import ams.cqrs.transactions.core.transaction.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionAggregateTest {
    private lateinit var fixture: FixtureConfiguration<TransactionAggregate>

    @BeforeEach
    fun setup() {
        fixture = AggregateTestFixture(TransactionAggregate::class.java)
    }

    @Test
    fun requestTransaction() {
        fixture.givenNoPriorActivity()
                .`when`(RequestMoneyTransaction(TX_ID, FROM_ID, TO_ID, AMOUNT))
                .expectSuccessfulHandlerExecution()
                .expectEvents(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
    }

    @Test
    fun completeTransaction() {
        fixture.given(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
                .`when`(CompleteMoneyTransfer(TX_ID))
                .expectSuccessfulHandlerExecution()
                .expectEvents(MoneyTransferCompleted(TX_ID))
    }

    @Test
    fun cancelTransaction() {
        fixture.given(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
                .`when`(CancelMoneyTransfer(TX_ID))
                .expectSuccessfulHandlerExecution()
                .expectEvents(MoneyTransferCanceled(TX_ID))
    }

    companion object {
        const val TX_ID = "tx_id"
        const val TO_ID = "to_id"
        const val FROM_ID = "from_id"
        const val AMOUNT = 1000
    }
}