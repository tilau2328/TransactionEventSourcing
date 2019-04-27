package ams.cqrs.transactions.query.balance

import org.springframework.data.repository.CrudRepository

interface BalanceRepository: CrudRepository<Balance, String>