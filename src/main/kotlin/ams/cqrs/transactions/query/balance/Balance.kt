package ams.cqrs.transactions.query.balance

import javax.persistence.Basic
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Balance(@Id val accountId: String, @Basic var balance: Int)