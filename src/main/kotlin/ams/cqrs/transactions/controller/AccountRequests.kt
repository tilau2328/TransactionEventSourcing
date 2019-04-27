package ams.cqrs.transactions.controller

data class WithdrawMoneyRequest(val amount: Int)
data class DepositMoneyRequest(val amount: Int)