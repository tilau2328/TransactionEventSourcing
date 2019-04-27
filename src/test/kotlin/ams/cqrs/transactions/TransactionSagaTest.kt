package ams.cqrs.transactions

import ams.cqrs.transactions.core.account.*
import ams.cqrs.transactions.core.transaction.*
import ams.cqrs.transactions.saga.TransactionSaga
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionSagaTest {
    private lateinit var fixture: FixtureConfiguration

    @BeforeEach
    fun setup() {
        fixture = SagaTestFixture(TransactionSaga::class.java)
    }

    @Test
    fun transactionRequested() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .expectActiveSagas(1)
            .expectDispatchedCommands(WithdrawMoney(FROM_ID, TX_ID, AMOUNT))
    }

    @Test
    fun overdraftLimitExceeded() {
        fixture.setCallbackBehavior { _, _ ->
            throw OverdraftLimitExceeded()
        }
        fixture.givenNoPriorActivity()
            .whenPublishingA(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .expectDispatchedCommands(WithdrawMoney(FROM_ID, TX_ID, AMOUNT), CancelMoneyTransfer(TX_ID))
    }

    @Test
    fun transactionCanceled() {
        fixture.givenAPublished(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .whenPublishingA(MoneyTransferCanceled(TX_ID))
            .expectActiveSagas(0)
            .expectNoDispatchedCommands()
    }

    @Test
    fun moneyWithdrawnAfterRequest() {
        fixture.givenAPublished(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .whenPublishingA(MoneyWithdrawn(FROM_ID, TX_ID, AMOUNT, BALANCE))
            .expectDispatchedCommands(DepositMoney(TO_ID, TX_ID, AMOUNT))
    }

    @Test
    fun moneyDepositedAfterWithdrawal() {
        fixture.givenAPublished(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .andThenAPublished(MoneyWithdrawn(FROM_ID, TX_ID, AMOUNT, BALANCE))
            .whenPublishingA(MoneyDeposited(TO_ID, TX_ID, AMOUNT, BALANCE))
            .expectDispatchedCommands(CompleteMoneyTransfer(TX_ID))
    }

    @Test
    fun transactionCompleted() {
        fixture.givenAPublished(MoneyTransferRequested(TX_ID, FROM_ID, TO_ID, AMOUNT))
            .andThenAPublished(MoneyWithdrawn(FROM_ID, TX_ID, AMOUNT, BALANCE))
            .andThenAPublished(MoneyDeposited(TO_ID, TX_ID, AMOUNT, BALANCE))
            .whenPublishingA(MoneyTransferCompleted(TX_ID))
            .expectActiveSagas(0)
            .expectNoDispatchedCommands()
    }

    companion object {
        const val TX_ID = "tx_id"
        const val TO_ID = "to_id"
        const val FROM_ID = "from_id"
        const val AMOUNT = 1000
        const val BALANCE = 1000
    }
}