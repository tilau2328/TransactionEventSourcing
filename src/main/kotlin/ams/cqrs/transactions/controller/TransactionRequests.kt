package ams.cqrs.transactions.controller

data class RequestTransferRequest(val amount: Int, val toId: String)