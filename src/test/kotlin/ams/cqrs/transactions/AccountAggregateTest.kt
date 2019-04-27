package ams.cqrs.transactions

import ams.cqrs.transactions.command.AccountAggregate
import ams.cqrs.transactions.core.account.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountAggregateTest {
    private lateinit var fixture: FixtureConfiguration<AccountAggregate>

    @BeforeEach
    fun setup() {
        fixture = AggregateTestFixture(AccountAggregate::class.java)
    }

    @Test
    fun createAccount() {
        fixture.givenNoPriorActivity()
            .`when`(CreateAccount(ID, TX_ID, OVERDRAFT_LIMIT))
            .expectSuccessfulHandlerExecution()
            .expectEvents(AccountCreated(ID, TX_ID, OVERDRAFT_LIMIT, 0))
    }

    @Test
    fun depositMoney() {
        fixture.given(AccountCreated(ID, TX_ID, OVERDRAFT_LIMIT, OVERDRAFT_LIMIT))
            .`when`(DepositMoney(ID, TX_ID, OVERDRAFT_LIMIT))
            .expectSuccessfulHandlerExecution()
            .expectEvents(MoneyDeposited(ID, TX_ID, OVERDRAFT_LIMIT, OVERDRAFT_LIMIT))
    }

    @Test
    fun withdrawReasonableAmount() {
        fixture.given(AccountCreated(ID, TX_ID, OVERDRAFT_LIMIT, OVERDRAFT_LIMIT))
            .`when`(WithdrawMoney(ID, TX_ID, OVERDRAFT_LIMIT))
            .expectSuccessfulHandlerExecution()
            .expectEvents(MoneyWithdrawn(ID, TX_ID, OVERDRAFT_LIMIT, -OVERDRAFT_LIMIT))
    }

    @Test
    fun withdrawAbsurdAmount() {
        fixture.given(AccountCreated(ID, TX_ID, OVERDRAFT_LIMIT, OVERDRAFT_LIMIT))
            .`when`(WithdrawMoney(ID, TX_ID, OVERDRAFT_LIMIT + 1))
            .expectNoEvents()
            .expectException(OverdraftLimitExceeded::class.java)
    }

    @Test
    fun exceedLimitForWithdrawingTwice() {
        fixture.given(AccountCreated(ID, TX_ID, OVERDRAFT_LIMIT, OVERDRAFT_LIMIT),
                MoneyWithdrawn(ID, TX_ID, OVERDRAFT_LIMIT, -OVERDRAFT_LIMIT))
            .`when`(WithdrawMoney(ID, TX_ID, 1))
            .expectNoEvents()
            .expectException(OverdraftLimitExceeded::class.java)
    }

    companion object {
        const val ID = "id"
        const val TX_ID= "tx_id"
        const val OVERDRAFT_LIMIT = 1000
    }
}