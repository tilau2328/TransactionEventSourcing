package ams.cqrs.transactions

import ams.cqrs.transactions.command.AccountAggregate
import org.axonframework.commandhandling.model.GenericJpaRepository
import org.axonframework.commandhandling.model.Repository
import org.axonframework.common.jpa.ContainerManagedEntityManagerProvider
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager

@SpringBootApplication
class PracticeApplication {
	@Bean
	fun eventStorageEngine(): EventStorageEngine {
		return InMemoryEventStorageEngine()
	}

	@Bean
	fun jpaAccountRepository(eventBus: EventBus): Repository<AccountAggregate> {
		return GenericJpaRepository(entityManagerProvider(), AccountAggregate::class.java, eventBus)
	}

	@Bean
	fun entityManagerProvider(): EntityManagerProvider {
		return ContainerManagedEntityManagerProvider()
	}

	@Bean
	fun axonTransactionManager(tx: PlatformTransactionManager): TransactionManager {
		return SpringTransactionManager(tx)
	}
}

fun main(args: Array<String>) {
	runApplication<PracticeApplication>(*args)
}
