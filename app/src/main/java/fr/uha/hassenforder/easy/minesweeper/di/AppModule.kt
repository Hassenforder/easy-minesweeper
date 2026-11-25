package fr.uha.hassenforder.easy.minesweeper.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.navigation.NavigatorType
import fr.uha.hassenforder.easy.minesweeper.database.MineSweeperDatabase
import fr.uha.hassenforder.easy.minesweeper.database.dao.PuzzleDao
import fr.uha.hassenforder.easy.minesweeper.navigation.GreetingDestination
import fr.uha.hassenforder.easy.minesweeper.navigation.rootNavGraph
import fr.uha.hassenforder.easy.minesweeper.repository.PuzzleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = MineSweeperDatabase.create(appContext)

    @Singleton
    @Provides
    fun providePuzzleDao(db: MineSweeperDatabase) = db.getPuzzleDao()

    @Singleton
    @Provides
    fun provideNavigator() : Navigator = Navigator(
        startDestination = GreetingDestination.GreetingScreen,
        type = NavigatorType.SINGLETOP,
        navigationGraph = { rootNavGraph() }
    )

    @Singleton
    @Provides
    fun providePersonRepository(
        ioDispatcher: CoroutineDispatcher,
        puzzleDao: PuzzleDao,
    ) = PuzzleRepository(ioDispatcher, puzzleDao)

}