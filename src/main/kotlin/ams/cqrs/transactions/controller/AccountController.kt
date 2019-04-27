package ams.cqrs.transactions.controller

import ams.cqrs.transactions.core.account.CreateAccount
import ams.cqrs.transactions.core.account.DepositMoney
import ams.cqrs.transactions.core.account.WithdrawMoney
import ams.cqrs.transactions.query.balance.Balance
import ams.cqrs.transactions.query.balance.BalanceRepository
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("accounts")
class AccountController {
    @Autowired
    lateinit var commandBus: CommandBus

    @Autowired
    lateinit var repository: BalanceRepository

    @PostMapping("")
    fun createAccount(): String {
        val id = UUID.randomUUID().toString()
        val txId = UUID.randomUUID().toString()
        commandBus.dispatch(asCommandMessage<CreateAccount>(CreateAccount(id, txId, 1000)))
        return id
    }

    @GetMapping("/{id}")
    fun getBalance(@PathVariable id: String): Optional<Balance> {
        return repository.findById(id)
    }

    @PostMapping("/{id}/withdraw")
    fun withdrawMoney(@PathVariable id: String, @RequestBody request: WithdrawMoneyRequest): String {
        val txId = UUID.randomUUID().toString()
        commandBus.dispatch(asCommandMessage<WithdrawMoney>(WithdrawMoney(id, txId, request.amount)))
        return id
    }

    @PostMapping("/{id}/deposit")
    fun depositMoney(@PathVariable id: String, @RequestBody request: DepositMoneyRequest): String {
        val txId = UUID.randomUUID().toString()
        commandBus.dispatch(asCommandMessage<DepositMoney>(DepositMoney(id, txId, request.amount)))
        return id
    }
}