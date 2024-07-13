package di

import data.MongoDB
import domain.MongoRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val mongoModule = module {
    single<MongoRepository> { MongoDB() }
}

fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}