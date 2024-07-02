package di

import data.MongoDB
import domain.MongoRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.home.HomeViewModel
import presentation.screen.task.TaskViewModel

val mongoModule = module {
    single<MongoRepository> { MongoDB() }
    factory { HomeViewModel(mongoDB = get()) }
    factory { TaskViewModel(mongoDB = get()) }
}

fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}