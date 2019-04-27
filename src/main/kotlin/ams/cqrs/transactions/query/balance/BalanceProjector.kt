package ams.cqrs.transactions.query.balance

import ams.cqrs.transactions.core.account.AccountCreated
import ams.cqrs.transactions.core.account.MoneyDeposited
import ams.cqrs.transactions.core.account.MoneyWithdrawn
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BalanceProjector {
    @Autowired
    lateinit var repository: BalanceRepository

    @EventHandler
    fun handle(event: AccountCreated) {
        val account = Balance(event.accountId, event.balance)
        repository.save(account)
    }

    @EventHandler
    fun handle(event: MoneyDeposited) {
        updateBalance(event.accountId, event.balance)
    }

    @EventHandler
    fun handle(event: MoneyWithdrawn) {
        updateBalance(event.accountId, event.balance)
    }

    private fun updateBalance(accountId: String, balance: Int) {
        val value = repository.findById(accountId)
        if(!value.isPresent) return
        val account = value.get()
        account.balance = balance
        repository.save(account)
    }
}