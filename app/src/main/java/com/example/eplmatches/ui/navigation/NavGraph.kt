package com.example.eplmatches.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.eplmatches.data.repository.MatchRepository
import com.example.eplmatches.ui.detail.MatchDetailScreen
import com.example.eplmatches.ui.detail.MatchDetailViewModel
import com.example.eplmatches.ui.list.MatchListScreen
import com.example.eplmatches.ui.list.MatchListViewModel

object Routes {
    const val MATCH_LIST = "match_list"
    const val MATCH_DETAIL = "match_detail/{matchNumber}"

    fun matchDetail(matchNumber: Int) = "match_detail/$matchNumber"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: MatchRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MATCH_LIST
    ) {
        composable(Routes.MATCH_LIST) {
            val viewModel: MatchListViewModel = viewModel(
                factory = MatchListViewModel.Factory(repository)
            )
            MatchListScreen(
                viewModel = viewModel,
                onMatchClick = { matchNumber ->
                    navController.navigate(Routes.matchDetail(matchNumber))
                }
            )
        }

        composable(
            route = Routes.MATCH_DETAIL,
            arguments = listOf(
                navArgument("matchNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val matchNumber = backStackEntry.arguments?.getInt("matchNumber") ?: return@composable
            val viewModel: MatchDetailViewModel = viewModel(
                factory = MatchDetailViewModel.Factory(matchNumber, repository)
            )
            MatchDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
