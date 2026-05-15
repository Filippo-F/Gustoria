package com.example.gustoria.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.Profile
import com.example.gustoria.Search
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils
import com.example.gustoria.viewmodel.HomeViewModel

// categorie mostrate nella Home (sezione 2)
private val homeCategories = listOf(
    "All", "Quick Meals", "Vegan", "Italian", "Gluten-Free", "Desserts", "Vegetarian"
)

@MultiPreview
@Preview
@Composable
fun HomeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        HomeScreen(
            navCtrl = rememberNavController(),
            recipeRepository = PreviewUtils.createFakeRecipeRepo(),
            userRepository = PreviewUtils.createFakeUserRepo()
        )
    }
}

class HomeActions(val navCtrl: NavHostController) {
    val navigateToProfile: () -> Unit = {
        navCtrl.navigate(Profile)
    }
    val navigateToSearch: () -> Unit = {
        navCtrl.navigate(Search)
    }
}

@Composable
fun HomeScreen(
    navCtrl: NavHostController,
    recipeRepository: RecipeRepoInterface,
    userRepository: UserRepoInterface,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(recipeRepository, userRepository)
    )
) {
    val actions = remember(navCtrl) { HomeActions(navCtrl) }

    val featuredRecipe by viewModel.featuredRecipe.collectAsStateWithLifecycle()
    val myRecipes     by viewModel.myRecipes.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // variante TopNavbar
        item {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "GUSTORIA",
                leadingContent = {
                    IconButton(onClick = actions.navigateToProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    }
                },
                trailingContent = {
                    IconButton(onClick = { /* TODO: navigazione */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }

        // sezione 1: "Recommended For You"
        item {
            HomeSectionHeader(title = "RECOMMENDED FOR YOU")
            Spacer(Modifier.height(8.dp))
            if (featuredRecipe != null) {
                RecipeCard(
                    recipe = featuredRecipe!!,
                    onClick = { /* TODO: navigazione */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // sezione 2: "Explore Categories"
        item {
            HomeSectionHeader(title = "EXPLORE CATEGORIES")
            Spacer(Modifier.height(8.dp))
            CategoryChipsRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                    // TODO: navigazione a Search con categoria
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        // sezione 3: "Recent Creations"
        item {
            HomeSectionHeader(title = "RECENT CREATIONS")
            Spacer(Modifier.height(8.dp))
            if (myRecipes.isEmpty()) {
                Text(
                    text = "No recipes created yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                RecentCreationsRow(
                    recipes = myRecipes,
                    onRecipeClick = { /* TODO: navigazione */ }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Composable privati

@Composable
private fun HomeSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun CategoryChipsRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(homeCategories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
private fun RecentCreationsRow(
    recipes: List<com.example.gustoria.dataclass.Recipe>,
    onRecipeClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                modifier = Modifier.width(180.dp)
            )
        }
    }
}
