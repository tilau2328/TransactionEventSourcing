package ams.cqrs.transactions.query.transaction

import javax.persistence.*


@Entity
data class Transaction(
    @Id val id: String,
    @Basic var amount: Int = 0,
    @Basic val toId: String = "",
    @Basic val fromId: String = ""
) {
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    var state: TransactionState = TransactionState.CREATED
}