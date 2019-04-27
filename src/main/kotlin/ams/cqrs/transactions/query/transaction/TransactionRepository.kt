package ams.cqrs.transactions.query.transaction

import org.springframework.data.repository.CrudRepository

interface TransactionRepository: CrudRepository<Transaction, String> {
    fun findByFromId(fromId: String): List<Transaction>
    fun findByToId(fromId: String): List<Transaction>
}