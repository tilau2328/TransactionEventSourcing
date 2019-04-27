package ams.cqrs.transactions.controller

import ams.cqrs.transactions.core.account.CreateAccount
import ams.cqrs.transactions.core.transaction.RequestMoneyTransaction
import ams.cqrs.transactions.query.transaction.Transaction
import ams.cqrs.transactions.query.transaction.TransactionRepository
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.GenericCommandMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("transactions")
class TransactionController {
    @Autowired
    lateinit var commandBus: CommandBus

    @Autowired
    lateinit var repository: TransactionRepository

    @PostMapping("/{fromId}")
    fun transferMoney(@PathVariable fromId: String, @RequestBody request: RequestTransferRequest): String {
        val id = UUID.randomUUID().toString()
        commandBus.dispatch(
            GenericCommandMessage.asCommandMessage<CreateAccount>(
                RequestMoneyTransaction(id, fromId, request.toId, request.amount)))
        return id
    }

    @GetMapping("/for/{id}")
    fun listTransactions(@PathVariable id: String): List<Transaction> {
        return repository.findByFromId(id) + repository.findByToId(id)
    }

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: String): Optional<Transaction> {
        return repository.findById(id)
    }
}
