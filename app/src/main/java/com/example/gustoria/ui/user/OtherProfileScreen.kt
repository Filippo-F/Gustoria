package com.example.gustoria.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.res.stringArrayResource
import com.example.gustoria.R
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.gustoria.dataclass.CookingRole
import androidx.compose.material3.CircularProgressIndicator
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.dataclass.Recipe
import androidx.compose.foundation.lazy.items
import com.example.gustoria.dataclass.User
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeCardContent

@MultiPreview
@Composable
fun OtherProfileScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            user = null,
            recipeCount = 0,
            likeCount = 0,
            isFollowing = false,
            recipes = emptyList(),
            onBack = {},
            onToggleFollow = {},
            onRecipeClick = {}
        )
    }
}

@Composable
fun ProfileImage(imageUrl: String?, firstName: String, lastName: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            coil.compose.AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                val initials = (firstName.take(1) + lastName.take(1)).uppercase()

                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileInfo(firstName: String, lastName: String, nickname: String, cookingRole: CookingRole, description: String){
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Full name
        Text(
            text = "$firstName $lastName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        val cookingRoles = stringArrayResource(R.array.cooking_roles)

        // Nickname + cookingRole
        Text(
            text = buildString {
                append(nickname)
                if (cookingRole != CookingRole.NONE) {
                    append(" • ")
                    append(cookingRoles[cookingRole.ordinal])
                }
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )


        // Description
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}

@Composable
fun ValueBox(
    value: Int,
    text: String,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .width(100.dp)
            .height(70.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OtherProfileScreen(
    user: User?,
    recipeCount: Int,
    likeCount: Int,
    isFollowing: Boolean,
    recipes: List<Recipe>,
    onBack: () -> Unit,
    onToggleFollow: () -> Unit,
    onRecipeClick: (String) -> Unit
) {

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = user?.nickname ?: "Profile",
                onBack = onBack,
                extraIcon = Icons.Default.Share,
                extraIconDescription = "Share",
                onClickExtra = {}
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    ProfileImage(
                        imageUrl = user.profileImageUri,
                        firstName = user.firstName,
                        lastName = user.lastName
                    )
                }

                item {
                    ProfileInfo(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        nickname = user.nickname,
                        cookingRole = user.cookingRole,
                        description = user.description
                    )
                }

                item {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = onToggleFollow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isFollowing)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = if (isFollowing) "Unfollow" else "Follow")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ValueBox(recipeCount, "Recipes")
                        ValueBox(user.numberOfFollowers, "Followers")
                        ValueBox(likeCount, "Likes")
                    }
                }
                item {
                    Text(
                        text = "Recent Recipes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (recipes.isEmpty()) {
                    item {
                        Text(
                            text = "No recipes published yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    val recentRecipes = recipes.sortedByDescending { it.createdAt }.take(3)
                    items(recentRecipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            content = { RecipeCardContent(recipe, imageHeight = 160.dp) }
                        )
                    }
                }
            }
        }
    }
}
